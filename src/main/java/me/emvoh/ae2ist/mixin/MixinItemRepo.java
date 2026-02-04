package me.emvoh.ae2ist.mixin;

import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.me.ItemRepo;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import me.emvoh.ae2ist.enums.Target;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(value = ItemRepo.class, remap = false)
public abstract class MixinItemRepo {

    @Shadow private List<IAEItemStack> view;
    @Shadow private String searchString;
    @Shadow private IPartitionList<IAEItemStack> myPartitionList;

    /**
     * @author emvoh
     * @reason Replace AE2 terminal search with improved query parsing and extra prefixes.
     */
    @Overwrite(remap = false)
    private void addIAE(IAEItemStack is, Enum viewMode) {
        final boolean needsZeroCopy = viewMode == ViewItems.CRAFTABLE;

        if (this.myPartitionList != null && !this.myPartitionList.isListed(is)) {
            return;
        }

        if (viewMode == ViewItems.CRAFTABLE && !is.isCraftable()) {
            return;
        }
        if (viewMode == ViewItems.STORED && is.getStackSize() == 0) {
            return;
        }

        final String query = lower(this.searchString).trim();
        if (query.isEmpty()) {
            if (needsZeroCopy) {
                IAEItemStack copy = is.copy();
                copy.setStackSize(0);
                this.view.add(copy);
            } else {
                this.view.add(is);
            }
            return;
        }

        // Original setting behavior:
        // enabled = normal terms also search tooltip
        // disabled = only # searches tooltip
        final boolean tooltipSearchEnabled =
                me.emvoh.ae2ist.config.AE2ISTConfig.client.searchTooltipsForNormalTerms;


        // Base strings (null-safe)
        final String itemName = lower(Platform.getItemDisplayName(is));
        String modId = null;
        String modName = null;

        // Lazy stuff only computed if a term needs it
        ItemStack stack = null;

        // REGISTRY can use getItem() directly (no ItemStack needed)
        String registryId = null;

        // Two tooltip caches:
        // tooltipLower: normal lowercase tooltip (keeps spaces), used for "old setting" behavior
        // tooltipText: normalized tooltip (spaces removed), used for explicit # searching
        String tooltipLower = null;
        String tooltipText = null;

        int[] oreIds = null;

        boolean found = false;

        // OR groups split by |
        for (String orPart : query.split("\\|")) {
            String part = orPart.trim();

            // Empty OR part matches everything
            if (part.isEmpty()) {
                found = true;
                break;
            }

            boolean groupMatches = true;

            // AND terms split by spaces (with quotes support)
            for (String raw : splitSearchTerms(part)) {
                if (raw.isEmpty()) {
                    continue;
                }

                boolean neg = false;
                char c0 = raw.charAt(0);
                if (c0 == '-' || c0 == '!') {
                    neg = true;
                    raw = raw.substring(1);
                    if (raw.isEmpty()) {
                        continue;
                    }
                }

                char prefix = raw.charAt(0);
                String term = raw;

                Target target = Target.NAME;

                if (prefix == '@' || prefix == '#' || prefix == '$' || prefix == '&' || prefix == '*') {
                    term = raw.substring(1);
                    if (term.isEmpty()) {
                        continue;
                    }

                    if (prefix == '@') target = Target.MOD;
                    else if (prefix == '#') target = Target.TOOLTIP;
                    else if (prefix == '$') target = Target.OREDICT;
                    else target = Target.REGISTRY; // & or *
                }

                boolean termMatches = false;

                switch (target) {
                    case NAME:
                        termMatches = itemName.contains(term);

                        if (!termMatches && tooltipSearchEnabled) {
                            if (tooltipLower == null) {
                                List<String> lines = Platform.getTooltip(is);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < lines.size(); i++) {
                                    String line = lines.get(i);
                                    if (line == null) continue;
                                    if (sb.length() > 0) sb.append('\n');
                                    sb.append(line);
                                }

                                String joined = sb.toString();
                                tooltipLower = lower(joined);
                                tooltipText = normalizeTooltip(joined);
                            }

                            termMatches = tooltipLower.contains(term);
                        }
                        break;

                    case MOD:
                        if (modId == null) {
                            modId = lower(Platform.getModId(is));
                        }

                        if (modId.contains(term)) {
                            termMatches = true;
                            break;
                        }

                        if (modName == null) {
                            modName = getModNameSafe(modId);
                        }
                        termMatches = modName.contains(term);
                        break;

                    case TOOLTIP:
                        if (tooltipText == null) {
                            List<String> lines = Platform.getTooltip(is);
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i);
                                if (line == null) continue;
                                if (sb.length() > 0) sb.append('\n');
                                sb.append(line);
                            }
                            String joined = sb.toString();
                            tooltipLower = lower(joined);
                            tooltipText = normalizeTooltip(joined);
                        }
                        termMatches = tooltipText.contains(normalizeTooltip(term));
                        break;

                    case OREDICT:
                        if (stack == null) {
                            ItemStack s = is.createItemStack();
                            stack = s == null ? ItemStack.EMPTY : s;
                        }
                        if (!stack.isEmpty()) {
                            if (oreIds == null) {
                                oreIds = OreDictionary.getOreIDs(stack);
                                if (oreIds == null) oreIds = new int[0];
                            }
                            for (int id : oreIds) {
                                String oreName = OreDictionary.getOreName(id);
                                if (oreName != null && lower(oreName).contains(term)) {
                                    termMatches = true;
                                    break;
                                }
                            }
                        }
                        break;

                    case REGISTRY:
                        if (registryId == null) {
                            ResourceLocation rl = is.getItem() == null ? null : is.getItem().getRegistryName();
                            registryId = lower(rl == null ? "" : rl.toString());
                        }
                        termMatches = registryId.contains(term);
                        break;
                }

                boolean passes = neg ? !termMatches : termMatches;
                if (!passes) {
                    groupMatches = false;
                    break;
                }
            }

            if (groupMatches) {
                found = true;
                break;
            }
        }

        if (found) {
            if (needsZeroCopy) {
                is = is.copy();
                is.setStackSize(0);
            }
            this.view.add(is);
        }
    }

    private static String lower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    private static String normalizeTooltip(String s) {
        return lower(s).replace(" ", "");
    }

    private static String getModNameSafe(String modId) {
        if (modId == null || modId.isEmpty()) {
            return "";
        }

        try {
            ModContainer c = Loader.instance().getIndexedModList().get(modId);
            if (c != null && c.getName() != null) {
                return lower(c.getName());
            }
        } catch (Throwable ignored) {
        }

        return "";
    }

    private static List<String> splitSearchTerms(String input) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (!inQuotes && Character.isWhitespace(ch)) {
                if (cur.length() > 0) {
                    out.add(cur.toString());
                    cur.setLength(0);
                }
                continue;
            }

            cur.append(ch);
        }

        if (cur.length() > 0) {
            out.add(cur.toString());
        }

        return out;
    }
}

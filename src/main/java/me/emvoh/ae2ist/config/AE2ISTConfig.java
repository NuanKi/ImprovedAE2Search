package me.emvoh.ae2ist.config;

import me.emvoh.ae2ist.Tags;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MODID, name = Tags.MODID) // creates config/ae2ist.cfg
public class AE2ISTConfig {

    @Config.Name("client")
    public static final Client client = new Client();

    public static class Client {

        @Config.Name("search_max_length")
        @Config.LangKey("ae2ist.config.search_max_length")
        @Config.Comment("Max characters allowed in the AE2 terminal search box.")
        @Config.RangeInt(min = 1, max = 256)
        public int searchMaxLength = 60;

        @Config.Name("search_tooltips_for_normal_terms")
        @Config.LangKey("ae2ist.config.search_tooltips_for_normal_terms")
        @Config.Comment({
                "When enabled, normal search terms also match tooltip text.",
                "When disabled, only #prefixed terms search tooltips."
        })

        public boolean searchTooltipsForNormalTerms = true;
    }
}

package me.emvoh.ae2ist.client;

import me.emvoh.ae2ist.Tags;
import me.emvoh.ae2ist.config.AE2ISTConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class GuiConfigAE2IST extends GuiConfig {

    public GuiConfigAE2IST(GuiScreen parent) {
        super(parent, getElements(), Tags.MODID, false, false, Tags.MODNAME + " Settings");
    }

    private static List<IConfigElement> getElements() {
        IConfigElement root = ConfigElement.from(AE2ISTConfig.class);

        for (IConfigElement cat : root.getChildElements()) {
            if ("client".equals(cat.getName())) {
                return cat.getChildElements();
            }
        }

        return root.getChildElements();
    }
}

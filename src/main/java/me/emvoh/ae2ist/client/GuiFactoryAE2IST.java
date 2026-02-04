package me.emvoh.ae2ist.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Collections;
import java.util.Set;

public class GuiFactoryAE2IST implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) { }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiConfigAE2IST(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Collections.emptySet();
    }
}

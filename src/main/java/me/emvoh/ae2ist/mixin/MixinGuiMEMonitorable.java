package me.emvoh.ae2ist.mixin;

import appeng.client.gui.implementations.GuiMEMonitorable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMEMonitorable.class, remap = false)
public abstract class MixinGuiMEMonitorable {

    @Shadow private appeng.client.gui.widgets.MEGuiTextField searchField;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void ae2ist$increaseSearchLength(CallbackInfo ci) {
        if (this.searchField != null) {
            int len = me.emvoh.ae2ist.config.AE2ISTConfig.client.searchMaxLength;
            len = Math.max(1, Math.min(256, len));
            this.searchField.setMaxStringLength(len);

        }
    }
}

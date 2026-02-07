package me.emvoh.ae2ist.mixin;

import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.widgets.MEGuiTextField;
import me.emvoh.ae2ist.config.AE2ISTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMEMonitorable.class, remap = false)
public abstract class MixinGuiMEMonitorable {

    @Shadow(remap = false)
    private MEGuiTextField searchField;

    @Inject(
            method = {
                    "initGui()V",          // dev
                    "func_73866_w_()V"     // obf runtime
            },
            at = @At("TAIL"),
            remap = false
    )
    private void ae2ist$increaseSearchLength(CallbackInfo ci) {
        if (this.searchField != null) {
            int len = AE2ISTConfig.client.searchMaxLength;
            len = Math.max(1, Math.min(256, len));
            this.searchField.setMaxStringLength(len);
        }
    }
}

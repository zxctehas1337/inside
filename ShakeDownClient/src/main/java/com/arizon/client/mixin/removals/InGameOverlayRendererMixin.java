package com.arizon.client.mixin.removals;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.Removals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remove fire overlay
 */
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void onRenderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        Removals removals = (Removals) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Removals");
        
        if (removals != null && removals.isEnabled() && removals.removeFireOverlay) {
            ci.cancel();
        }
    }
}

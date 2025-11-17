package com.arizon.client.mixin.removals;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.Removals;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remove fog
 */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    
    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        Removals removals = (Removals) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Removals");
        
        if (removals != null && removals.isEnabled() && removals.removeFog) {
            ci.cancel();
        }
    }
}

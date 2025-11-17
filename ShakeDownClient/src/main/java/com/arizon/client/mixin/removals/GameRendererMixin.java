package com.arizon.client.mixin.removals;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.Removals;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remove hurt cam shake
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(CallbackInfo ci) {
        Removals removals = (Removals) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Removals");
        
        if (removals != null && removals.isEnabled() && removals.removeHurtCam) {
            ci.cancel();
        }
    }
}

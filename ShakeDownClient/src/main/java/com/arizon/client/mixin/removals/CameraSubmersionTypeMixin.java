package com.arizon.client.mixin.removals;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.Removals;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Remove water/lava overlay
 */
@Mixin(Camera.class)
public class CameraSubmersionTypeMixin {
    
    @Inject(method = "getSubmersionType", at = @At("RETURN"), cancellable = true)
    private void onGetSubmersionType(CallbackInfoReturnable<net.minecraft.client.render.CameraSubmersionType> cir) {
        Removals removals = (Removals) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Removals");
        
        if (removals != null && removals.isEnabled()) {
            if (removals.removeWaterOverlay && cir.getReturnValue() == net.minecraft.client.render.CameraSubmersionType.WATER) {
                cir.setReturnValue(net.minecraft.client.render.CameraSubmersionType.NONE);
            }
        }
    }
}

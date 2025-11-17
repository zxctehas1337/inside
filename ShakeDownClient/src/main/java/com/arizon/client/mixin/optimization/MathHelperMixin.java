package com.arizon.client.mixin.optimization;

import com.arizon.client.optimization.OptimizationEngine;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fast Math - Replace expensive sin/cos with lookup tables
 * ALWAYS ACTIVE for maximum FPS
 */
@Mixin(MathHelper.class)
public class MathHelperMixin {
    
    @Inject(method = "sin", at = @At("HEAD"), cancellable = true)
    private static void fastSin(float value, CallbackInfoReturnable<Float> cir) {
        if (OptimizationEngine.isActive()) {
            cir.setReturnValue(OptimizationEngine.fastSin(value));
        }
    }
    
    @Inject(method = "cos", at = @At("HEAD"), cancellable = true)
    private static void fastCos(float value, CallbackInfoReturnable<Float> cir) {
        if (OptimizationEngine.isActive()) {
            cir.setReturnValue(OptimizationEngine.fastCos(value));
        }
    }
}

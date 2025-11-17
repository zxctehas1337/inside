package com.arizon.client.mixin.optimization;

import com.arizon.client.optimization.OptimizationEngine;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Optimize entity rendering - skip entities far away
 * ALWAYS ACTIVE for maximum FPS
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderEntity(Entity entity, double x, double y, double z, float yaw, 
                                float tickDelta, MatrixStack matrices, 
                                VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!OptimizationEngine.isActive()) return;
        
        // Skip rendering entities very far away
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance > 128) { // Don't render entities beyond 128 blocks
            ci.cancel();
        }
    }
}

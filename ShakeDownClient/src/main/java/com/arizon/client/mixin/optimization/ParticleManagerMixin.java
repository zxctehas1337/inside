package com.arizon.client.mixin.optimization;

import com.arizon.client.optimization.OptimizationEngine;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

/**
 * Optimize particle rendering - cull excessive particles
 * ALWAYS ACTIVE for maximum FPS
 */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    
    @Shadow
    private Queue<?> newParticles;
    
    private int particleCount = 0;
    private static final int MAX_PARTICLES = 500; // Limit particles for FPS
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!OptimizationEngine.isActive()) return;
        
        // Count and limit particles
        if (newParticles != null) {
            particleCount = newParticles.size();
            
            // Clear excessive particles
            if (particleCount > MAX_PARTICLES) {
                int toRemove = particleCount - MAX_PARTICLES;
                for (int i = 0; i < toRemove; i++) {
                    newParticles.poll();
                }
            }
        }
    }
}

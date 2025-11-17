package com.arizon.client.mixin;

import com.arizon.client.module.ModuleManager;
import com.arizon.client.module.modules.render.Chams;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Chams Mixin - Renders entities THROUGH WALLS with CUSTOM COLORS
 */
@Mixin(EntityRenderer.class)
public class ChamsMixin {
    
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderEntity(Entity entity, float yaw, float tickDelta, MatrixStack matrices, 
                               VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Chams chams = (Chams) ModuleManager.getInstance().getModuleByName("Chams");
        
        if (chams != null && chams.isEnabled()) {
            if (entity instanceof LivingEntity && entity != net.minecraft.client.MinecraftClient.getInstance().player) {
                // Check distance
                double distance = net.minecraft.client.MinecraftClient.getInstance().player.distanceTo(entity);
                if (distance > chams.range) return;
                
                // Filter by type
                boolean shouldRender = false;
                if (entity instanceof net.minecraft.entity.player.PlayerEntity && chams.showPlayers) {
                    shouldRender = true;
                } else if (entity instanceof net.minecraft.entity.mob.MobEntity && chams.showMobs) {
                    shouldRender = true;
                }
                
                if (!shouldRender) return;
                
                // Filter invisible
                if (!chams.showInvisible && entity.isInvisible()) return;
                
                // Filter naked
                if (!chams.showNaked && entity instanceof net.minecraft.entity.player.PlayerEntity) {
                    net.minecraft.entity.player.PlayerEntity player = (net.minecraft.entity.player.PlayerEntity) entity;
                    boolean hasArmor = false;
                    for (net.minecraft.item.ItemStack armor : player.getArmorItems()) {
                        if (!armor.isEmpty()) {
                            hasArmor = true;
                            break;
                        }
                    }
                    if (!hasArmor) return;
                }
                
                // RENDER THROUGH WALLS - disable depth test
                RenderSystem.disableDepthTest();
                
                // CUSTOM COLOR OVERLAY
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                
                // Яркий цвет для видимости через стены
                float r = chams.colorR / 255f;
                float g = chams.colorG / 255f;
                float b = chams.colorB / 255f;
                float a = chams.colorA / 255f;
                
                RenderSystem.setShaderColor(r, g, b, a * 0.7f);
            }
        }
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderEntityEnd(Entity entity, float yaw, float tickDelta, MatrixStack matrices, 
                                   VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Chams chams = (Chams) ModuleManager.getInstance().getModuleByName("Chams");
        
        if (chams != null && chams.isEnabled()) {
            if (entity instanceof LivingEntity && entity != net.minecraft.client.MinecraftClient.getInstance().player) {
                // RESTORE RENDER STATE
                RenderSystem.enableDepthTest();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            }
        }
    }
}

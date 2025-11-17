package com.arizon.client.mixin;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.SwingAnimations;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    
    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void onRenderItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, 
                             float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, 
                             VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        
        SwingAnimations swingAnim = (SwingAnimations) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Swing Animations");
        
        if (swingAnim == null || !swingAnim.isEnabled()) return;
        
        // Apply swing animation based on mode
        if (swingProgress > 0.0f) {
            float animProgress = swingProgress * swingAnim.speed;
            
            switch (swingAnim.mode) {
                case "1.7":
                    // Classic 1.7 animation - smooth swing down
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-animProgress * 40.0f));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(animProgress * 20.0f));
                    matrices.translate(0, animProgress * 0.2f, 0);
                    break;
                    
                case "Smooth":
                    // Smooth circular motion
                    float smoothAngle = (float)Math.sin(animProgress * Math.PI) * 30.0f;
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-smoothAngle));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(smoothAngle * 0.5f));
                    break;
                    
                case "Spin":
                    // 360 degree spin
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(animProgress * 360.0f));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-animProgress * 20.0f));
                    break;
                    
                case "Push":
                    // Push forward animation
                    matrices.translate(0, 0, -animProgress * 0.5f);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-animProgress * 30.0f));
                    float pushScale = 1.0f + animProgress * 0.3f;
                    matrices.scale(pushScale, pushScale, pushScale);
                    break;
            }
        }
    }
}

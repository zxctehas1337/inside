package com.arizon.client.mixin;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.ChinaHat;
import com.arizon.client.module.modules.render.JumpCircles;
import com.arizon.client.render.TargetESP;
import com.arizon.client.render.ESPRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Renders custom effects in world
 */
@Mixin(WorldRenderer.class)
public class WorldRenderContextMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderWorld(MatrixStack matrices, float tickDelta, long limitTime, 
                               boolean renderBlockOutline, Camera camera, 
                               GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, 
                               Matrix4f positionMatrix, CallbackInfo ci) {
        // Render China Hat
        ChinaHat chinaHat = (ChinaHat) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("China Hat");
        if (chinaHat != null && chinaHat.isEnabled()) {
            chinaHat.render(matrices, tickDelta);
        }
        
        // Render Jump Circles
        JumpCircles jumpCircles = (JumpCircles) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Jump Circles");
        if (jumpCircles != null && jumpCircles.isEnabled()) {
            jumpCircles.render(matrices, tickDelta);
        }
        
        // Render Target ESP
        TargetESP.render(matrices, tickDelta);
        
        // Render ESP
        ESPRenderer.render(matrices, tickDelta);
    }
}

package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

/**
 * China Hat - Renders a cone hat above player
 */
public class ChinaHat extends Module {
    
    public float radius = 0.6f;
    public float height = 0.3f;
    public int segments = 30;
    
    public ChinaHat() {
        super("China Hat");
    }
    
    @Override
    public String getDescription() {
        return "Renders a Chinese hat above your head";
    }
    
    @Override
    public boolean hasSettings() {
        return false;
    }
    
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onUpdate() {
        // Rendering handled in WorldRenderEvent
    }
    
    public void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        
        matrices.push();
        
        // Position exactly on player head (like Nursultan client)
        double playerX = mc.player.prevX + (mc.player.getX() - mc.player.prevX) * tickDelta;
        double playerY = mc.player.prevY + (mc.player.getY() - mc.player.prevY) * tickDelta;
        double playerZ = mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ) * tickDelta;
        
        matrices.translate(
            playerX - mc.gameRenderer.getCamera().getPos().x,
            playerY + mc.player.getEyeHeight(mc.player.getPose()) + 0.3 - mc.gameRenderer.getCamera().getPos().y,
            playerZ - mc.gameRenderer.getCamera().getPos().z
        );
        
        // Rotate with player body
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.player.bodyYaw));
        
        // Enable blending for transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest(); // Enable depth test so it doesn't render through blocks
        RenderSystem.disableCull();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        // Rainbow color - BRIGHT and SMOOTH
        float hue = ((System.currentTimeMillis() % 3000) / 3000.0f);
        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f); // Full saturation and brightness
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        float a = 0.7f;
        
        // Set shader for colored rendering
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        // Draw cone
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        
        // Top point - bright rainbow color
        buffer.vertex(matrices.peek().getPositionMatrix(), 0, height, 0)
            .color(r, g, b, a).next();
        
        // Bottom circle - slightly darker but still colorful
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float x = (float) (Math.cos(angle) * radius);
            float z = (float) (Math.sin(angle) * radius);
            
            // Calculate rainbow color for each segment
            float segmentHue = (hue + (i / (float)segments) * 0.1f) % 1.0f;
            int segmentRgb = java.awt.Color.HSBtoRGB(segmentHue, 1.0f, 0.8f);
            float sr = ((segmentRgb >> 16) & 0xFF) / 255.0f;
            float sg = ((segmentRgb >> 8) & 0xFF) / 255.0f;
            float sb = (segmentRgb & 0xFF) / 255.0f;
            
            buffer.vertex(matrices.peek().getPositionMatrix(), x, 0, z)
                .color(sr, sg, sb, a).next();
        }
        
        tessellator.draw();
        
        // Draw outline
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float x = (float) (Math.cos(angle) * radius);
            float z = (float) (Math.sin(angle) * radius);
            
            buffer.vertex(matrices.peek().getPositionMatrix(), x, 0, z)
                .color(r, g, b, 1.0f).next();
        }
        
        tessellator.draw();
        
        // Restore GL state
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        
        matrices.pop();
    }
}

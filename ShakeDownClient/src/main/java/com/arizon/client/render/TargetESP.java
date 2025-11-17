package com.arizon.client.render;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.combat.Aura;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Renders a diamond/rhombus with crystals around Aura target
 */
public class TargetESP {
    
    public static void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Aura aura = (Aura) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Aura");
        
        if (aura == null || !aura.isEnabled() || aura.currentTarget == null) return;
        
        LivingEntity target = aura.currentTarget;
        
        matrices.push();
        
        // Get target position
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        double x = target.prevX + (target.getX() - target.prevX) * tickDelta - cameraPos.x;
        double y = target.prevY + (target.getY() - target.prevY) * tickDelta - cameraPos.y;
        double z = target.prevZ + (target.getZ() - target.prevZ) * tickDelta - cameraPos.z;
        
        matrices.translate(x, y + target.getHeight() / 2, z);
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        // Purple/violet color scheme
        float time = System.currentTimeMillis() % 2000 / 2000.0f;
        
        // Purple gradient colors
        float r1 = 0.6f + (float)Math.sin(time * Math.PI * 2) * 0.2f;
        float g1 = 0.2f;
        float b1 = 0.9f;
        
        float r2 = 0.8f;
        float g2 = 0.3f + (float)Math.cos(time * Math.PI * 2) * 0.2f;
        float b2 = 1.0f;
        
        // Draw nested diamonds (from large to small)
        int diamondLayers = 5;
        for (int layer = diamondLayers; layer >= 0; layer--) {
            float layerSize = 0.7f * (layer / (float)diamondLayers);
            float layerAlpha = 0.1f + (layer / (float)diamondLayers) * 0.2f;
            
            if (layer == diamondLayers) {
                // Outermost diamond - solid outline only
                buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
                
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, layerSize, 0)
                    .color(r1, g1, b1, 1.0f).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), layerSize, 0, 0)
                    .color(r2, g2, b2, 1.0f).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, -layerSize, 0)
                    .color(r1, g1, b1, 1.0f).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), -layerSize, 0, 0)
                    .color(r2, g2, b2, 1.0f).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, layerSize, 0)
                    .color(r1, g1, b1, 1.0f).next();
                
                tessellator.draw();
            } else {
                // Inner diamonds - transparent fill
                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, layerSize, 0)
                    .color(r1, g1, b1, layerAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), layerSize, 0, 0)
                    .color(r2, g2, b2, layerAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, -layerSize, 0)
                    .color(r1, g1, b1, layerAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), -layerSize, 0, 0)
                    .color(r2, g2, b2, layerAlpha).next();
                
                tessellator.draw();
                
                // Outline for inner diamonds
                buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
                
                float outlineAlpha = 0.3f + (layer / (float)diamondLayers) * 0.4f;
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, layerSize, 0)
                    .color(r1, g1, b1, outlineAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), layerSize, 0, 0)
                    .color(r2, g2, b2, outlineAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, -layerSize, 0)
                    .color(r1, g1, b1, outlineAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), -layerSize, 0, 0)
                    .color(r2, g2, b2, outlineAlpha).next();
                buffer.vertex(matrices.peek().getPositionMatrix(), 0, layerSize, 0)
                    .color(r1, g1, b1, outlineAlpha).next();
                
                tessellator.draw();
            }
        }
        
        // Draw scattered purple crystals around target (RANDOM positions)
        int crystalCount = 32; // МНОГО кристаллов
        float crystalSize = 0.13f; // Маленькие
        long seed = (long)(target.getX() * 1000 + target.getZ() * 1000); // Seed based on position
        java.util.Random random = new java.util.Random(seed);
        
        for (int i = 0; i < crystalCount; i++) {
            // Random scattered position around target
            float angle = random.nextFloat() * 360.0f;
            float distance = 0.6f + random.nextFloat() * 0.6f; // 0.6 to 1.2 blocks
            float heightOffset = (random.nextFloat() - 0.5f) * 0.8f; // -0.4 to +0.4
            
            float cx = (float) Math.cos(Math.toRadians(angle)) * distance;
            float cz = (float) Math.sin(Math.toRadians(angle)) * distance;
            
            // Floating animation
            float floatOffset = (float)Math.sin((System.currentTimeMillis() + i * 100) / 500.0) * 0.1f;
            
            matrices.push();
            matrices.translate(cx, heightOffset + floatOffset, cz);
            
            // Purple crystal colors with variation
            float crystalHue = 0.75f + (float)Math.sin((time + i * 0.2f) * Math.PI * 2) * 0.1f;
            float cr = 0.6f + i * 0.05f;
            float cg = 0.2f;
            float cb = 0.95f;
            
            // Draw crystal (3D diamond shape)
            buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            
            // Top pyramid (4 faces)
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, crystalSize * 2.5f, 0)
                .color(cr, cg, cb, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, crystalSize * 2.5f, 0)
                .color(cr, cg, cb, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), -crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, crystalSize * 2.5f, 0)
                .color(cr, cg, cb, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), -crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, -crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, crystalSize * 2.5f, 0)
                .color(cr, cg, cb, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, -crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            
            // Bottom pyramid (4 faces)
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, -crystalSize * 2.5f, 0)
                .color(cr * 0.6f, cg * 0.6f, cb * 0.6f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, -crystalSize * 2.5f, 0)
                .color(cr * 0.6f, cg * 0.6f, cb * 0.6f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), -crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, -crystalSize * 2.5f, 0)
                .color(cr * 0.6f, cg * 0.6f, cb * 0.6f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, -crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), -crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, -crystalSize * 2.5f, 0)
                .color(cr * 0.6f, cg * 0.6f, cb * 0.6f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), crystalSize, 0, 0)
                .color(cr * 0.7f, cg * 0.7f, cb * 0.7f, 0.9f).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, -crystalSize)
                .color(cr * 0.8f, cg * 0.8f, cb * 0.8f, 0.9f).next();
            
            tessellator.draw();
            
            matrices.pop();
        }
        
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        
        matrices.pop();
    }
}

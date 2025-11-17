package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Jump Circles - Renders expanding circles when player jumps
 */
public class JumpCircles extends Module {
    
    private final List<Circle> circles = new ArrayList<>();
    private boolean wasOnGround = true;
    
    public JumpCircles() {
        super("Jump Circles");
    }
    
    @Override
    public String getDescription() {
        return "Renders circles when you jump";
    }
    
    @Override
    public boolean hasSettings() {
        return false;
    }
    
    @Override
    public void onEnable() {
        circles.clear();
    }
    
    @Override
    public void onDisable() {
        circles.clear();
    }
    
    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        
        // Detect jump (leaving ground)
        if (wasOnGround && !mc.player.isOnGround() && mc.player.getVelocity().y > 0) {
            // Add new circle WITHOUT clearing old ones
            circles.add(new Circle(mc.player.getPos(), System.currentTimeMillis()));
        }
        
        wasOnGround = mc.player.isOnGround();
        
        // Update and remove old circles
        Iterator<Circle> iterator = circles.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            if (System.currentTimeMillis() - circle.time > 2000) { // 2 seconds lifetime
                iterator.remove();
            }
        }
    }
    
    public void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (circles.isEmpty()) return;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest(); // Enable depth test so it doesn't render through blocks
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        
        for (Circle circle : circles) {
            matrices.push();
            
            // Calculate progress (0 to 1)
            float progress = (System.currentTimeMillis() - circle.time) / 2000.0f;
            float radius = progress * 3.0f; // Expand to 3 blocks
            float alpha = 1.0f - progress; // Fade out
            
            // Position at circle location
            matrices.translate(
                circle.pos.x - cameraPos.x,
                circle.pos.y - cameraPos.y,
                circle.pos.z - cameraPos.z
            );
            
            // Rainbow color based on progress - BRIGHT and VIVID
            float hue = ((System.currentTimeMillis() % 3000) / 3000.0f + progress * 0.5f) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f); // Full saturation and brightness
            float r = ((rgb >> 16) & 0xFF) / 255.0f;
            float g = ((rgb >> 8) & 0xFF) / 255.0f;
            float b = (rgb & 0xFF) / 255.0f;
            
            int segments = 60;
            
            // Draw filled circle (inner glow)
            buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0.01f, 0)
                .color(r, g, b, alpha * 0.3f).next();
            
            for (int i = 0; i <= segments; i++) {
                double angle = 2 * Math.PI * i / segments;
                float x = (float) (Math.cos(angle) * radius);
                float z = (float) (Math.sin(angle) * radius);
                
                buffer.vertex(matrices.peek().getPositionMatrix(), x, 0.01f, z)
                    .color(r, g, b, 0.0f).next();
            }
            tessellator.draw();
            
            // Draw thick outline (3 lines for thickness)
            for (int thickness = 0; thickness < 3; thickness++) {
                buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
                
                float offset = thickness * 0.02f;
                for (int i = 0; i <= segments; i++) {
                    double angle = 2 * Math.PI * i / segments;
                    float x = (float) (Math.cos(angle) * (radius + offset));
                    float z = (float) (Math.sin(angle) * (radius + offset));
                    
                    buffer.vertex(matrices.peek().getPositionMatrix(), x, 0.01f, z)
                        .color(r, g, b, alpha * (1.0f - thickness * 0.2f)).next();
                }
                
                tessellator.draw();
            }
            
            matrices.pop();
        }
        
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
    
    private static class Circle {
        Vec3d pos;
        long time;
        
        Circle(Vec3d pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}

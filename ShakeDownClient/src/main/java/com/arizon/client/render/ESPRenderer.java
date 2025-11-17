package com.arizon.client.render;

import com.arizon.client.module.ModuleManager;
import com.arizon.client.module.modules.render.ESP;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * ESP Renderer - Renders CUSTOM ESP boxes around entities THROUGH WALLS
 */
public class ESPRenderer {
    
    private static long startTime = System.currentTimeMillis();
    
    public static void render(MatrixStack matrices, float tickDelta) {
        ESP esp = (ESP) ModuleManager.getInstance().getModuleByName("ESP");
        if (esp == null || !esp.isEnabled()) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        
        // SETUP FOR RENDERING THROUGH WALLS
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        // Animation time
        float time = (System.currentTimeMillis() - startTime) / 1000.0f;
        
        // Render ESP for all entities
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (entity.isRemoved()) continue;
            
            LivingEntity living = (LivingEntity) entity;
            double distance = mc.player.distanceTo(entity);
            
            if (distance > esp.range) continue;
            
            // Filter by type
            boolean shouldRender = false;
            if (entity instanceof net.minecraft.entity.player.PlayerEntity && esp.showPlayers) {
                shouldRender = true;
            } else if (entity instanceof net.minecraft.entity.mob.MobEntity && esp.showMobs) {
                shouldRender = true;
            }
            
            if (!shouldRender) continue;
            
            // Filter invisible
            if (!esp.showInvisible && entity.isInvisible()) continue;
            
            // Filter naked (no armor)
            if (!esp.showNaked && entity instanceof net.minecraft.entity.player.PlayerEntity) {
                net.minecraft.entity.player.PlayerEntity player = (net.minecraft.entity.player.PlayerEntity) entity;
                boolean hasArmor = false;
                for (net.minecraft.item.ItemStack armor : player.getArmorItems()) {
                    if (!armor.isEmpty()) {
                        hasArmor = true;
                        break;
                    }
                }
                if (!hasArmor) continue;
            }
            
            // CUSTOM HITBOX (расширенный, не дефолтный)
            Box customBox = getCustomBox(entity, tickDelta);
            
            // Outline color
            float or = esp.outlineR / 255f;
            float og = esp.outlineG / 255f;
            float ob = esp.outlineB / 255f;
            float oa = esp.outlineA / 255f;
            
            // Rainbow mode
            if (esp.rainbowMode) {
                float hue = (time + entity.getId() * 0.1f) % 1.0f;
                int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                or = ((rgb >> 16) & 0xFF) / 255f;
                og = ((rgb >> 8) & 0xFF) / 255f;
                ob = (rgb & 0xFF) / 255f;
            }
            
            // Danger mode - red outline for low health
            if (esp.danger && living.getHealth() < living.getMaxHealth() * 0.3f) {
                or = 1.0f;
                og = 0.0f;
                ob = 0.0f;
            }
            
            // Pulse animation
            float pulseMultiplier = 1.0f;
            if (esp.pulseAnimation) {
                pulseMultiplier = 0.7f + (float) Math.sin(time * 3.0f + entity.getId()) * 0.3f;
            }
            
            // Render based on mode
            if (esp.mode.equals("Box")) {
                // Box режим - контур + заливка бокса
                float fbr = esp.fillBoxR / 255f;
                float fbg = esp.fillBoxG / 255f;
                float fbb = esp.fillBoxB / 255f;
                float fba = esp.fillBoxA / 255f;
                
                if (esp.rainbowMode) {
                    fbr = or; fbg = og; fbb = ob;
                }
                
                // Glow effect (несколько слоев)
                if (esp.glowEffect) {
                    for (int i = 3; i > 0; i--) {
                        Box glowBox = customBox.expand(i * 0.05);
                        float glowAlpha = (oa * pulseMultiplier) / (i * 3.0f);
                        renderCustomBoxFilled(matrices, glowBox, or, og, ob, glowAlpha);
                    }
                }
                
                renderCustomBox(matrices, customBox, or * pulseMultiplier, og * pulseMultiplier, ob * pulseMultiplier, oa * pulseMultiplier);
                renderCustomBoxFilled(matrices, customBox, fbr, fbg, fbb, fba * pulseMultiplier);
            } else if (esp.mode.equals("Outline")) {
                // Outline режим - контур + заливка контура
                float for_ = esp.fillOutlineR / 255f;
                float fog = esp.fillOutlineG / 255f;
                float fob = esp.fillOutlineB / 255f;
                float foa = esp.fillOutlineA / 255f;
                
                if (esp.rainbowMode) {
                    for_ = or; fog = og; fob = ob;
                }
                
                if (esp.glowEffect) {
                    for (int i = 3; i > 0; i--) {
                        Box glowBox = customBox.expand(i * 0.05);
                        float glowAlpha = (oa * pulseMultiplier) / (i * 3.0f);
                        renderCustomBoxFilled(matrices, glowBox, or, og, ob, glowAlpha);
                    }
                }
                
                renderCustomBox(matrices, customBox, or * pulseMultiplier, og * pulseMultiplier, ob * pulseMultiplier, oa * pulseMultiplier);
                renderCustomBoxFilled(matrices, customBox, for_, fog, fob, foa * pulseMultiplier);
            } else if (esp.mode.equals("Corner Box")) {
                // Corner Box режим - углы + заливка контура
                float for_ = esp.fillOutlineR / 255f;
                float fog = esp.fillOutlineG / 255f;
                float fob = esp.fillOutlineB / 255f;
                float foa = esp.fillOutlineA / 255f;
                
                if (esp.rainbowMode) {
                    for_ = or; fog = og; fob = ob;
                }
                
                if (esp.glowEffect) {
                    for (int i = 3; i > 0; i--) {
                        Box glowBox = customBox.expand(i * 0.05);
                        float glowAlpha = (oa * pulseMultiplier) / (i * 3.0f);
                        renderCustomBoxFilled(matrices, glowBox, or, og, ob, glowAlpha);
                    }
                }
                
                renderCustomCornerBox(matrices, customBox, or * pulseMultiplier, og * pulseMultiplier, ob * pulseMultiplier, oa * pulseMultiplier);
                renderCustomBoxFilled(matrices, customBox, for_, fog, fob, foa * pulseMultiplier);
            }
            
            // Particles effect
            if (esp.particles) {
                renderParticles(matrices, customBox, or, og, ob, time, entity.getId());
            }
            
            // Render health bar
            if (esp.showHealthBar) {
                renderHealthBar(matrices, customBox, living);
            }
            
            // Render armor (только для игроков)
            if (esp.showArmor && entity instanceof PlayerEntity) {
                renderArmorBar(matrices, customBox, (PlayerEntity) entity);
            }
            
            // Render inventory line (только для игроков)
            if (esp.showInventory && entity instanceof PlayerEntity) {
                renderInventoryLine(matrices, customBox, (PlayerEntity) entity);
            }
            
            // Render distance
            if (esp.showDistance) {
                renderDistance(matrices, customBox, distance);
            }
            
            // Render name
            if (esp.showName) {
                renderName(matrices, customBox, entity);
            }
        }
        
        matrices.pop();
        
        // RESTORE RENDER STATE
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
    
    /**
     * КАСТОМНЫЙ ХИТБОКС - расширенный и красивый
     */
    private static Box getCustomBox(Entity entity, float tickDelta) {
        // Интерполяция позиции для плавности
        double x = entity.prevX + (entity.getX() - entity.prevX) * tickDelta;
        double y = entity.prevY + (entity.getY() - entity.prevY) * tickDelta;
        double z = entity.prevZ + (entity.getZ() - entity.prevZ) * tickDelta;
        
        // Расширяем хитбокс для красоты
        float width = entity.getWidth() * 0.6f; // Немного шире
        float height = entity.getHeight() + 0.1f; // Немного выше
        
        return new Box(
            x - width, y, z - width,
            x + width, y + height, z + width
        );
    }
    
    /**
     * КАСТОМНЫЙ BOX - красивые линии с градиентом
     */
    private static void renderCustomBox(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        RenderSystem.lineWidth(2.5f); // Толстые линии
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        // Bottom face (яркие линии)
        drawLine(buffer, matrix, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, a);
        drawLine(buffer, matrix, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, a);
        drawLine(buffer, matrix, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, b, a);
        drawLine(buffer, matrix, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, r, g, b, a);
        
        // Top face (яркие линии)
        drawLine(buffer, matrix, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a);
        drawLine(buffer, matrix, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        drawLine(buffer, matrix, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
        drawLine(buffer, matrix, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, r, g, b, a);
        
        // Vertical edges (градиент снизу вверх)
        drawGradientLine(buffer, matrix, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, a * 0.5f, r, g, b, a);
        drawGradientLine(buffer, matrix, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a * 0.5f, r, g, b, a);
        drawGradientLine(buffer, matrix, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, r, g, b, a * 0.5f, r, g, b, a);
        drawGradientLine(buffer, matrix, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a * 0.5f, r, g, b, a);
        
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
    }
    
    /**
     * ЗАЛИВКА БОКСА - полупрозрачная
     */
    private static void renderCustomBoxFilled(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        // Bottom
        buffer.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(r, g, b, a).next();
        
        // Top
        buffer.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(r, g, b, a).next();
        
        // Sides
        buffer.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        
        buffer.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        
        buffer.vertex(matrix, (float)box.maxX, (float)box.minY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.maxX, (float)box.maxY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        
        buffer.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.maxZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.maxY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        buffer.vertex(matrix, (float)box.minX, (float)box.minY, (float)box.minZ).color(r, g, b, a * 0.8f).next();
        
        tessellator.draw();
    }
    
    /**
     * OUTLINE - только контур
     */
    private static void renderCustomOutline(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        renderCustomBox(matrices, box, r, g, b, a);
    }
    
    /**
     * CORNER BOX - красивые углы
     */
    private static void renderCustomCornerBox(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        RenderSystem.lineWidth(3.0f); // Еще толще для углов
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        float cornerSize = 0.3f;
        
        // 8 углов с градиентом
        // Bottom corners
        drawCorner(buffer, matrix, box.minX, box.minY, box.minZ, cornerSize, r, g, b, a);
        drawCorner(buffer, matrix, box.maxX, box.minY, box.minZ, -cornerSize, r, g, b, a);
        drawCorner(buffer, matrix, box.maxX, box.minY, box.maxZ, -cornerSize, r, g, b, a);
        drawCorner(buffer, matrix, box.minX, box.minY, box.maxZ, cornerSize, r, g, b, a);
        
        // Top corners
        drawCorner(buffer, matrix, box.minX, box.maxY, box.minZ, cornerSize, r, g, b, a);
        drawCorner(buffer, matrix, box.maxX, box.maxY, box.minZ, -cornerSize, r, g, b, a);
        drawCorner(buffer, matrix, box.maxX, box.maxY, box.maxZ, -cornerSize, r, g, b, a);
        drawCorner(buffer, matrix, box.minX, box.maxY, box.maxZ, cornerSize, r, g, b, a);
        
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
    }
    
    // Helper methods
    private static void drawLine(BufferBuilder buffer, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        buffer.vertex(matrix, (float)x1, (float)y1, (float)z1).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)x2, (float)y2, (float)z2).color(r, g, b, a).next();
    }
    
    private static void drawGradientLine(BufferBuilder buffer, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        buffer.vertex(matrix, (float)x1, (float)y1, (float)z1).color(r1, g1, b1, a1).next();
        buffer.vertex(matrix, (float)x2, (float)y2, (float)z2).color(r2, g2, b2, a2).next();
    }
    
    private static void drawCorner(BufferBuilder buffer, Matrix4f matrix, double x, double y, double z, float size, float r, float g, float b, float a) {
        // X axis
        buffer.vertex(matrix, (float)x, (float)y, (float)z).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)(x + size), (float)y, (float)z).color(r, g, b, a * 0.5f).next();
        
        // Y axis
        buffer.vertex(matrix, (float)x, (float)y, (float)z).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)x, (float)(y + Math.abs(size)), (float)z).color(r, g, b, a * 0.5f).next();
        
        // Z axis
        buffer.vertex(matrix, (float)x, (float)y, (float)z).color(r, g, b, a).next();
        buffer.vertex(matrix, (float)x, (float)y, (float)(z + size)).color(r, g, b, a * 0.5f).next();
    }
    
    /**
     * HEALTH BAR - улучшенный бар здоровья с градиентом и свечением
     */
    private static void renderHealthBar(MatrixStack matrices, Box box, LivingEntity living) {
        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();
        float healthPercent = Math.min(health / maxHealth, 1.0f);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Health bar position (слева от бокса, шире)
        double x = box.minX - 0.12;
        double yStart = box.minY;
        double yEnd = box.maxY;
        double z = (box.minZ + box.maxZ) / 2.0;
        
        double barHeight = yEnd - yStart;
        double healthHeight = barHeight * healthPercent;
        double barWidth = 0.08;
        
        // Background (темный с градиентом)
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(0.1f, 0.1f, 0.1f, 0.7f).next();
        buffer.vertex(matrix, (float)x, (float)yEnd, (float)z).color(0.05f, 0.05f, 0.05f, 0.7f).next();
        buffer.vertex(matrix, (float)(x - barWidth), (float)yEnd, (float)z).color(0.05f, 0.05f, 0.05f, 0.7f).next();
        buffer.vertex(matrix, (float)(x - barWidth), (float)yStart, (float)z).color(0.1f, 0.1f, 0.1f, 0.7f).next();
        tessellator.draw();
        
        // Health bar (яркий градиент)
        float r1, g1, b1;
        if (healthPercent > 0.5f) {
            // Зеленый -> Желтый
            r1 = (1.0f - healthPercent) * 2.0f;
            g1 = 1.0f;
            b1 = 0.0f;
        } else {
            // Желтый -> Красный
            r1 = 1.0f;
            g1 = healthPercent * 2.0f;
            b1 = 0.0f;
        }
        
        // Основной бар с градиентом снизу вверх
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(r1 * 0.8f, g1 * 0.8f, b1, 0.9f).next();
        buffer.vertex(matrix, (float)x, (float)(yStart + healthHeight), (float)z).color(r1, g1, b1, 0.9f).next();
        buffer.vertex(matrix, (float)(x - barWidth), (float)(yStart + healthHeight), (float)z).color(r1, g1, b1, 0.9f).next();
        buffer.vertex(matrix, (float)(x - barWidth), (float)yStart, (float)z).color(r1 * 0.8f, g1 * 0.8f, b1, 0.9f).next();
        tessellator.draw();
        
        // Свечение (glow effect)
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x + 0.01), (float)yStart, (float)z).color(r1, g1, b1, 0.3f).next();
        buffer.vertex(matrix, (float)(x + 0.01), (float)(yStart + healthHeight), (float)z).color(r1, g1, b1, 0.5f).next();
        buffer.vertex(matrix, (float)(x - barWidth - 0.01), (float)(yStart + healthHeight), (float)z).color(r1, g1, b1, 0.5f).next();
        buffer.vertex(matrix, (float)(x - barWidth - 0.01), (float)yStart, (float)z).color(r1, g1, b1, 0.3f).next();
        tessellator.draw();
        
        // Яркий контур
        RenderSystem.lineWidth(2.0f);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        buffer.vertex(matrix, (float)(x - barWidth), (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x - barWidth), (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)x, (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)(x - barWidth), (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)x, (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)(x - barWidth), (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
        
        // Деления на баре (каждые 25%)
        RenderSystem.lineWidth(1.0f);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        for (int i = 1; i < 4; i++) {
            double markY = yStart + (barHeight * i / 4.0);
            buffer.vertex(matrix, (float)(x - barWidth), (float)markY, (float)z).color(0.3f, 0.3f, 0.3f, 0.8f).next();
            buffer.vertex(matrix, (float)x, (float)markY, (float)z).color(0.3f, 0.3f, 0.3f, 0.8f).next();
        }
        tessellator.draw();
    }
    
    /**
     * ARMOR BAR - бар брони справа от бокса
     */
    private static void renderArmorBar(MatrixStack matrices, Box box, PlayerEntity player) {
        int armor = player.getArmor();
        if (armor == 0) return;
        
        float armorPercent = Math.min(armor / 20.0f, 1.0f);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Armor bar position (справа от бокса)
        double x = box.maxX + 0.12;
        double yStart = box.minY;
        double yEnd = box.maxY;
        double z = (box.minZ + box.maxZ) / 2.0;
        
        double barHeight = yEnd - yStart;
        double armorHeight = barHeight * armorPercent;
        double barWidth = 0.08;
        
        // Background
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(0.1f, 0.1f, 0.1f, 0.7f).next();
        buffer.vertex(matrix, (float)x, (float)yEnd, (float)z).color(0.05f, 0.05f, 0.05f, 0.7f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)yEnd, (float)z).color(0.05f, 0.05f, 0.05f, 0.7f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)yStart, (float)z).color(0.1f, 0.1f, 0.1f, 0.7f).next();
        tessellator.draw();
        
        // Armor bar (синий градиент)
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(0.3f, 0.6f, 1.0f, 0.9f).next();
        buffer.vertex(matrix, (float)x, (float)(yStart + armorHeight), (float)z).color(0.5f, 0.8f, 1.0f, 0.9f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)(yStart + armorHeight), (float)z).color(0.5f, 0.8f, 1.0f, 0.9f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)yStart, (float)z).color(0.3f, 0.6f, 1.0f, 0.9f).next();
        tessellator.draw();
        
        // Glow
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x - 0.01), (float)yStart, (float)z).color(0.5f, 0.8f, 1.0f, 0.3f).next();
        buffer.vertex(matrix, (float)(x - 0.01), (float)(yStart + armorHeight), (float)z).color(0.5f, 0.8f, 1.0f, 0.5f).next();
        buffer.vertex(matrix, (float)(x + barWidth + 0.01), (float)(yStart + armorHeight), (float)z).color(0.5f, 0.8f, 1.0f, 0.5f).next();
        buffer.vertex(matrix, (float)(x + barWidth + 0.01), (float)yStart, (float)z).color(0.5f, 0.8f, 1.0f, 0.3f).next();
        tessellator.draw();
        
        // Outline
        RenderSystem.lineWidth(2.0f);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)x, (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)(x + barWidth), (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)x, (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)yEnd, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)x, (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + barWidth), (float)yStart, (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
    }
    
    /**
     * INVENTORY LINE - улучшенная линия с инвентарем и количеством
     */
    private static void renderInventoryLine(MatrixStack matrices, Box box, PlayerEntity player) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Линия над боксом (выше и ярче)
        double y = box.maxY + 0.4;
        double xStart = box.minX;
        double xEnd = box.maxX;
        double z = (box.minZ + box.maxZ) / 2.0;
        
        // Яркая линия с градиентом
        RenderSystem.lineWidth(3.0f);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)xStart, (float)y, (float)z).color(0.0f, 1.0f, 1.0f, 0.8f).next();
        buffer.vertex(matrix, (float)xEnd, (float)y, (float)z).color(0.0f, 1.0f, 1.0f, 0.8f).next();
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
        
        // Рисуем предметы инвентаря
        double itemY = y + 0.2;
        double itemSpacing = (xEnd - xStart) / 9.0;
        
        for (int i = 0; i < 9; i++) {
            net.minecraft.item.ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            
            double itemX = xStart + (i + 0.5) * itemSpacing;
            float itemSize = 0.1f; // Больше размер
            
            // Определяем цвет по типу предмета
            float r = 0.8f, g = 0.8f, b = 0.8f;
            
            if (stack.getItem() instanceof net.minecraft.item.SwordItem || 
                stack.getItem() instanceof net.minecraft.item.AxeItem) {
                r = 1.0f; g = 0.2f; b = 0.2f; // Красный - оружие
            } else if (stack.getItem() instanceof net.minecraft.item.ToolItem) {
                r = 0.2f; g = 0.6f; b = 1.0f; // Синий - инструменты
            } else if (stack.getItem() instanceof net.minecraft.item.BlockItem) {
                r = 0.7f; g = 0.5f; b = 0.3f; // Коричневый - блоки
            } else if (stack.getItem().toString().contains("potion")) {
                r = 1.0f; g = 0.0f; b = 1.0f; // Фиолетовый - зелья
            } else if (stack.getItem() instanceof net.minecraft.item.BowItem || 
                       stack.getItem() instanceof net.minecraft.item.CrossbowItem) {
                r = 1.0f; g = 0.8f; b = 0.0f; // Желтый - луки
            }
            
            // Основной квадрат с градиентом
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(matrix, (float)(itemX - itemSize), (float)(itemY - itemSize), (float)z).color(r * 0.7f, g * 0.7f, b * 0.7f, 0.9f).next();
            buffer.vertex(matrix, (float)(itemX + itemSize), (float)(itemY - itemSize), (float)z).color(r, g, b, 0.9f).next();
            buffer.vertex(matrix, (float)(itemX + itemSize), (float)(itemY + itemSize), (float)z).color(r, g, b, 0.9f).next();
            buffer.vertex(matrix, (float)(itemX - itemSize), (float)(itemY + itemSize), (float)z).color(r * 0.7f, g * 0.7f, b * 0.7f, 0.9f).next();
            tessellator.draw();
            
            // Glow effect
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            float glowSize = itemSize + 0.02f;
            buffer.vertex(matrix, (float)(itemX - glowSize), (float)(itemY - glowSize), (float)z).color(r, g, b, 0.3f).next();
            buffer.vertex(matrix, (float)(itemX + glowSize), (float)(itemY - glowSize), (float)z).color(r, g, b, 0.3f).next();
            buffer.vertex(matrix, (float)(itemX + glowSize), (float)(itemY + glowSize), (float)z).color(r, g, b, 0.3f).next();
            buffer.vertex(matrix, (float)(itemX - glowSize), (float)(itemY + glowSize), (float)z).color(r, g, b, 0.3f).next();
            tessellator.draw();
            
            // Яркий контур
            RenderSystem.lineWidth(2.0f);
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            buffer.vertex(matrix, (float)(itemX - itemSize), (float)(itemY - itemSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
            buffer.vertex(matrix, (float)(itemX + itemSize), (float)(itemY - itemSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
            buffer.vertex(matrix, (float)(itemX + itemSize), (float)(itemY + itemSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
            buffer.vertex(matrix, (float)(itemX - itemSize), (float)(itemY + itemSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
            buffer.vertex(matrix, (float)(itemX - itemSize), (float)(itemY - itemSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
            tessellator.draw();
            RenderSystem.lineWidth(1.0f);
            
            // Индикатор количества (если больше 1)
            if (stack.getCount() > 1) {
                double countY = itemY - itemSize - 0.08;
                float countSize = 0.04f;
                
                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                buffer.vertex(matrix, (float)(itemX - countSize), (float)(countY - countSize), (float)z).color(1.0f, 1.0f, 0.0f, 0.9f).next();
                buffer.vertex(matrix, (float)(itemX + countSize), (float)(countY - countSize), (float)z).color(1.0f, 1.0f, 0.0f, 0.9f).next();
                buffer.vertex(matrix, (float)(itemX + countSize), (float)(countY + countSize), (float)z).color(1.0f, 1.0f, 0.0f, 0.9f).next();
                buffer.vertex(matrix, (float)(itemX - countSize), (float)(countY + countSize), (float)z).color(1.0f, 1.0f, 0.0f, 0.9f).next();
                tessellator.draw();
            }
        }
    }
    
    /**
     * DISTANCE - показ дистанции под боксом
     */
    private static void renderDistance(MatrixStack matrices, Box box, double distance) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        double y = box.minY - 0.3;
        double x = (box.minX + box.maxX) / 2.0;
        double z = (box.minZ + box.maxZ) / 2.0;
        
        // Рисуем линию дистанции
        int distInt = (int) distance;
        float barLength = Math.min(distInt / 50.0f, 1.0f) * 0.5f;
        
        RenderSystem.lineWidth(3.0f);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        // Градиент от зеленого (близко) к красному (далеко)
        float r = (float) Math.min(distance / 50.0, 1.0);
        float g = 1.0f - r;
        
        buffer.vertex(matrix, (float)(x - barLength), (float)y, (float)z).color(g, r, 0.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + barLength), (float)y, (float)z).color(g, r, 0.0f, 1.0f).next();
        
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
    }
    
    /**
     * NAME - показ имени над боксом с эффектом
     */
    private static void renderName(MatrixStack matrices, Box box, Entity entity) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        double y = box.maxY + 0.7;
        double x = (box.minX + box.maxX) / 2.0;
        double z = (box.minZ + box.maxZ) / 2.0;
        
        // Рисуем яркую линию над именем с градиентом
        RenderSystem.lineWidth(3.0f);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x - 0.4), (float)y, (float)z).color(0.0f, 0.8f, 1.0f, 0.5f).next();
        buffer.vertex(matrix, (float)x, (float)y, (float)z).color(0.0f, 1.0f, 1.0f, 1.0f).next();
        
        buffer.vertex(matrix, (float)x, (float)y, (float)z).color(0.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + 0.4), (float)y, (float)z).color(0.0f, 0.8f, 1.0f, 0.5f).next();
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
        
        // Светящаяся точка в центре
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        // Glow
        float glowSize = 0.08f;
        buffer.vertex(matrix, (float)(x - glowSize), (float)(y - glowSize), (float)z).color(0.0f, 1.0f, 1.0f, 0.3f).next();
        buffer.vertex(matrix, (float)(x + glowSize), (float)(y - glowSize), (float)z).color(0.0f, 1.0f, 1.0f, 0.3f).next();
        buffer.vertex(matrix, (float)(x + glowSize), (float)(y + glowSize), (float)z).color(0.0f, 1.0f, 1.0f, 0.3f).next();
        buffer.vertex(matrix, (float)(x - glowSize), (float)(y + glowSize), (float)z).color(0.0f, 1.0f, 1.0f, 0.3f).next();
        
        // Core
        float dotSize = 0.04f;
        buffer.vertex(matrix, (float)(x - dotSize), (float)(y - dotSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + dotSize), (float)(y - dotSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x + dotSize), (float)(y + dotSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, (float)(x - dotSize), (float)(y + dotSize), (float)z).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        
        tessellator.draw();
    }
    
    /**
     * PARTICLES - красивые частицы вокруг бокса
     */
    private static void renderParticles(MatrixStack matrices, Box box, float r, float g, float b, float time, int entityId) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        double centerX = (box.minX + box.maxX) / 2.0;
        double centerY = (box.minY + box.maxY) / 2.0;
        double centerZ = (box.minZ + box.maxZ) / 2.0;
        
        double width = (box.maxX - box.minX) / 2.0;
        double height = (box.maxY - box.minY) / 2.0;
        
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        // 8 частиц вокруг бокса
        for (int i = 0; i < 8; i++) {
            float angle = (time * 2.0f + i * (float) Math.PI / 4.0f + entityId * 0.1f) % (float) (Math.PI * 2);
            
            double px = centerX + Math.cos(angle) * (width + 0.3);
            double py = centerY + Math.sin(time * 3.0f + i) * height;
            double pz = centerZ + Math.sin(angle) * (width + 0.3);
            
            float particleSize = 0.05f + (float) Math.sin(time * 5.0f + i) * 0.02f;
            
            // Градиент от центра
            float alpha = 0.8f - (float) Math.abs(Math.sin(time * 2.0f + i)) * 0.4f;
            
            // Glow
            float glowSize = particleSize * 2.0f;
            buffer.vertex(matrix, (float)(px - glowSize), (float)(py - glowSize), (float)pz).color(r, g, b, alpha * 0.3f).next();
            buffer.vertex(matrix, (float)(px + glowSize), (float)(py - glowSize), (float)pz).color(r, g, b, alpha * 0.3f).next();
            buffer.vertex(matrix, (float)(px + glowSize), (float)(py + glowSize), (float)pz).color(r, g, b, alpha * 0.3f).next();
            buffer.vertex(matrix, (float)(px - glowSize), (float)(py + glowSize), (float)pz).color(r, g, b, alpha * 0.3f).next();
            
            // Core
            buffer.vertex(matrix, (float)(px - particleSize), (float)(py - particleSize), (float)pz).color(r, g, b, alpha).next();
            buffer.vertex(matrix, (float)(px + particleSize), (float)(py - particleSize), (float)pz).color(r, g, b, alpha).next();
            buffer.vertex(matrix, (float)(px + particleSize), (float)(py + particleSize), (float)pz).color(r, g, b, alpha).next();
            buffer.vertex(matrix, (float)(px - particleSize), (float)(py + particleSize), (float)pz).color(r, g, b, alpha).next();
        }
        
        tessellator.draw();
        
        // Вертикальные частицы (поднимаются вверх)
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        for (int i = 0; i < 4; i++) {
            float offset = (time * 0.5f + i * 0.25f) % 1.0f;
            double py = box.minY + (box.maxY - box.minY) * offset;
            
            double px = centerX + Math.cos(i * Math.PI / 2.0) * width * 0.8;
            double pz = centerZ + Math.sin(i * Math.PI / 2.0) * width * 0.8;
            
            float particleSize = 0.04f;
            float alpha = 1.0f - offset;
            
            buffer.vertex(matrix, (float)(px - particleSize), (float)(py - particleSize), (float)pz).color(r, g, b, alpha * 0.8f).next();
            buffer.vertex(matrix, (float)(px + particleSize), (float)(py - particleSize), (float)pz).color(r, g, b, alpha * 0.8f).next();
            buffer.vertex(matrix, (float)(px + particleSize), (float)(py + particleSize), (float)pz).color(r, g, b, alpha * 0.8f).next();
            buffer.vertex(matrix, (float)(px - particleSize), (float)(py + particleSize), (float)pz).color(r, g, b, alpha * 0.8f).next();
        }
        
        tessellator.draw();
    }
}

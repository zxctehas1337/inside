package com.arizon.client.gui.render;

import net.minecraft.client.gui.DrawContext;
import java.awt.Color;

/**
 * Advanced rendering helper for pixel-perfect GUI
 */
public class RenderHelper {
    
    /**
     * Draw perfect rounded rectangle using anti-aliasing
     */
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, Color color) {
        int rgb = color.getRGB();
        
        // Main rectangles
        context.fill(x + radius, y, x + width - radius, y + height, rgb);
        context.fill(x, y + radius, x + width, y + height - radius, rgb);
        
        // Corners with smooth anti-aliasing
        drawRoundedCorner(context, x + radius, y + radius, radius, rgb, 0); // Top-left
        drawRoundedCorner(context, x + width - radius, y + radius, radius, rgb, 1); // Top-right
        drawRoundedCorner(context, x + radius, y + height - radius, radius, rgb, 2); // Bottom-left
        drawRoundedCorner(context, x + width - radius, y + height - radius, radius, rgb, 3); // Bottom-right
    }
    
    /**
     * Draw rounded corner with anti-aliasing
     * corner: 0=TL, 1=TR, 2=BL, 3=BR
     */
    private static void drawRoundedCorner(DrawContext context, int centerX, int centerY, int radius, int rgb, int corner) {
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance <= radius) {
                    // Check if pixel is in correct quadrant
                    boolean inQuadrant = false;
                    switch (corner) {
                        case 0: inQuadrant = dx <= 0 && dy <= 0; break; // Top-left
                        case 1: inQuadrant = dx >= 0 && dy <= 0; break; // Top-right
                        case 2: inQuadrant = dx <= 0 && dy >= 0; break; // Bottom-left
                        case 3: inQuadrant = dx >= 0 && dy >= 0; break; // Bottom-right
                    }
                    
                    if (inQuadrant) {
                        // Anti-aliasing at edge
                        if (distance > radius - 1) {
                            float alpha = (float) (radius - distance);
                            int alphaValue = (int) (alpha * 255);
                            int colorWithAlpha = (alphaValue << 24) | (rgb & 0x00FFFFFF);
                            context.fill(centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, colorWithAlpha);
                        } else {
                            context.fill(centerX + dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, rgb);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Draw gradient rounded rectangle
     */
    public static void drawRoundedRectGradient(DrawContext context, int x, int y, int width, int height, 
                                               int radius, Color color1, Color color2) {
        // Draw gradient in steps
        int steps = height;
        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / steps;
            int r = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * ratio);
            int g = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * ratio);
            int b = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * ratio);
            int a = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * ratio);
            
            Color stepColor = new Color(r, g, b, a);
            
            if (i < radius || i >= height - radius) {
                // Rounded parts
                drawRoundedRect(context, x, y + i, width, 1, radius, stepColor);
            } else {
                // Straight parts
                context.fill(x, y + i, x + width, y + i + 1, stepColor.getRGB());
            }
        }
    }
    
    /**
     * Draw perfect circle with anti-aliasing
     */
    public static void drawCircle(DrawContext context, int centerX, int centerY, int radius, Color color) {
        int rgb = color.getRGB();
        
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                double distance = Math.sqrt(x * x + y * y);
                
                if (distance <= radius) {
                    // Anti-aliasing at edge
                    if (distance > radius - 1) {
                        float alpha = (float) (radius - distance);
                        int alphaValue = (int) (alpha * color.getAlpha());
                        int colorWithAlpha = (alphaValue << 24) | (rgb & 0x00FFFFFF);
                        context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, colorWithAlpha);
                    } else {
                        context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, rgb);
                    }
                }
            }
        }
    }
    
    /**
     * Draw shadow effect
     */
    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int radius, int shadowSize) {
        for (int i = 0; i < shadowSize; i++) {
            int alpha = (shadowSize - i) * 15;
            Color shadowColor = new Color(0, 0, 0, alpha);
            drawRoundedRect(context, x - i, y - i, width + i * 2, height + i * 2, radius + i, shadowColor);
        }
    }
}

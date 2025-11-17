package com.arizon.client.gui.components;

import com.arizon.client.gui.animation.Animation;
import com.arizon.client.gui.theme.NeonTheme;
import net.minecraft.client.gui.DrawContext;

/**
 * Animated slider with glow effect
 */
public class AnimatedSlider {
    
    private final Animation hoverAnimation;
    private boolean hovered;
    
    public AnimatedSlider() {
        this.hoverAnimation = new Animation(0.2f, Animation.AnimationType.EASE_OUT);
    }
    
    public void render(DrawContext context, int x, int y, int width, float value, float min, float max, boolean hovered, float delta) {
        this.hovered = hovered;
        hoverAnimation.setTarget(hovered ? 1 : 0);
        hoverAnimation.update(delta);
        
        int height = 4;
        float percent = (value - min) / (max - min);
        int fillWidth = (int)(width * percent);
        
        // Glow effect when hovered
        if (hoverAnimation.getValue() > 0) {
            float glowAlpha = hoverAnimation.getValue() * 0.2f;
            int glowColor = (int)(glowAlpha * 255) << 24 | (NeonTheme.GLOW_CYAN & 0x00FFFFFF);
            context.fill(x - 1, y - 1, x + width + 1, y + height + 1, glowColor);
        }
        
        // Track
        context.fill(x, y, x + width, y + height, NeonTheme.SLIDER_TRACK);
        
        // Fill with gradient
        for (int i = 0; i < fillWidth; i++) {
            float gradientProgress = (float)i / fillWidth;
            int color = interpolateColor(NeonTheme.ACCENT_CYAN, NeonTheme.ACCENT_BLUE, gradientProgress);
            context.fill(x + i, y, x + i + 1, y + height, color);
        }
        
        // Thumb with glow
        int thumbX = x + fillWidth - 4;
        int thumbY = y - 2;
        int thumbSize = 8;
        
        if (hoverAnimation.getValue() > 0) {
            float glowAlpha = hoverAnimation.getValue() * 0.4f;
            int glowColor = (int)(glowAlpha * 255) << 24 | (NeonTheme.GLOW_CYAN & 0x00FFFFFF);
            context.fill(thumbX - 2, thumbY - 2, thumbX + thumbSize + 2, thumbY + thumbSize + 2, glowColor);
        }
        
        context.fill(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize, NeonTheme.SLIDER_THUMB);
    }
    
    private int interpolateColor(int color1, int color2, float progress) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int)(a1 + (a2 - a1) * progress);
        int r = (int)(r1 + (r2 - r1) * progress);
        int g = (int)(g1 + (g2 - g1) * progress);
        int b = (int)(b1 + (b2 - b1) * progress);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

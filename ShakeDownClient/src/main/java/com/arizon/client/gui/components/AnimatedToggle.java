package com.arizon.client.gui.components;

import com.arizon.client.gui.animation.Animation;
import com.arizon.client.gui.theme.NeonTheme;
import net.minecraft.client.gui.DrawContext;

/**
 * Animated toggle switch with glow effect
 */
public class AnimatedToggle {
    
    private final Animation animation;
    private final Animation glowAnimation;
    private boolean enabled;
    
    public AnimatedToggle() {
        this.animation = new Animation(0.15f, Animation.AnimationType.EASE_OUT);
        this.glowAnimation = new Animation(0.2f, Animation.AnimationType.EASE_IN_OUT);
    }
    
    public void render(DrawContext context, int x, int y, boolean enabled, float delta) {
        this.enabled = enabled;
        animation.setTarget(enabled ? 1 : 0);
        animation.update(delta);
        
        int width = 40;
        int height = 20;
        
        // Background with animation
        int bgColor = enabled ? NeonTheme.TOGGLE_ON : NeonTheme.TOGGLE_OFF;
        
        // Draw glow effect when enabled
        if (enabled && animation.getValue() > 0.5f) {
            float glowAlpha = animation.getValue() * 0.3f;
            int glowColor = (int)(glowAlpha * 255) << 24 | (NeonTheme.GLOW_CYAN & 0x00FFFFFF);
            
            // Outer glow
            context.fill(x - 2, y - 2, x + width + 2, y + height + 2, glowColor);
        }
        
        // Main background
        context.fill(x, y, x + width, y + height, bgColor);
        
        // Animated circle position
        float progress = animation.getValue();
        int circleX = (int)(x + 2 + progress * (width - 18));
        int circleY = y + 2;
        int circleSize = 16;
        
        // Circle with glow
        if (enabled) {
            int glowColor = NeonTheme.GLOW_CYAN;
            context.fill(circleX - 1, circleY - 1, circleX + circleSize + 1, circleY + circleSize + 1, glowColor);
        }
        
        context.fill(circleX, circleY, circleX + circleSize, circleY + circleSize, NeonTheme.TOGGLE_CIRCLE);
    }
}

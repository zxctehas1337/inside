package com.arizon.client.hud.elements;

import com.arizon.client.hud.HudElement;
import com.arizon.client.gui.theme.ModernTheme;

import java.awt.*;

/**
 * FPS counter HUD element
 */
public class FpsElement extends HudElement {
    
    private int fps = 60;
    private long lastTime = System.currentTimeMillis();
    private int frameCount = 0;
    
    public FpsElement(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        // Calculate FPS
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastTime = currentTime;
        }
        
        String text = "FPS: " + fps;
        
        g.setFont(ModernTheme.FONT_BODY);
        FontMetrics fm = g.getFontMetrics();
        width = fm.stringWidth(text) + 10;
        height = fm.getHeight() + 6;
        
        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, width, height, 5, 5);
        
        // Border
        g.setColor(ModernTheme.ACCENT);
        g.drawRoundRect(x, y, width, height, 5, 5);
        
        // Text
        Color fpsColor = fps >= 60 ? ModernTheme.SUCCESS : 
                        fps >= 30 ? ModernTheme.WARNING : ModernTheme.ERROR;
        g.setColor(fpsColor);
        g.drawString(text, x + 5, y + fm.getAscent() + 3);
    }
    
    @Override
    public String getName() {
        return "FPS";
    }
}

package com.arizon.client.hud.elements;

import com.arizon.client.hud.HudElement;
import com.arizon.client.gui.theme.ModernTheme;

import java.awt.*;

/**
 * Coordinates display HUD element
 */
public class CoordinatesElement extends HudElement {
    
    private int posX = 0;
    private int posY = 64;
    private int posZ = 0;
    
    public CoordinatesElement(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        // Simulate changing coordinates
        posX = (int)(Math.sin(System.currentTimeMillis() / 1000.0) * 100);
        posZ = (int)(Math.cos(System.currentTimeMillis() / 1000.0) * 100);
        
        String text = String.format("XYZ: %d / %d / %d", posX, posY, posZ);
        
        g.setFont(ModernTheme.FONT_BODY);
        FontMetrics fm = g.getFontMetrics();
        width = fm.stringWidth(text) + 10;
        height = fm.getHeight() + 6;
        
        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, width, height, 5, 5);
        
        // Border
        g.setColor(ModernTheme.SECONDARY_ACCENT);
        g.drawRoundRect(x, y, width, height, 5, 5);
        
        // Text
        g.setColor(ModernTheme.TEXT_PRIMARY);
        g.drawString(text, x + 5, y + fm.getAscent() + 3);
    }
    
    @Override
    public String getName() {
        return "Coordinates";
    }
}

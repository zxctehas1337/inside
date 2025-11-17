package com.arizon.client.hud.elements;

import com.arizon.client.hud.HudElement;
import com.arizon.client.gui.theme.ModernTheme;

import java.awt.*;

/**
 * Armor display HUD element
 */
public class ArmorElement extends HudElement {
    
    private int armorValue = 20;
    
    public ArmorElement(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        String text = "Armor: " + armorValue;
        
        g.setFont(ModernTheme.FONT_BODY);
        FontMetrics fm = g.getFontMetrics();
        width = fm.stringWidth(text) + 40;
        height = fm.getHeight() + 6;
        
        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, width, height, 5, 5);
        
        // Border
        g.setColor(ModernTheme.ACCENT);
        g.drawRoundRect(x, y, width, height, 5, 5);
        
        // Armor icon (simple rectangle)
        g.setColor(new Color(150, 150, 150));
        g.fillRect(x + 5, y + 5, 15, 15);
        
        // Text
        g.setColor(ModernTheme.TEXT_PRIMARY);
        g.drawString(text, x + 25, y + fm.getAscent() + 3);
    }
    
    @Override
    public String getName() {
        return "Armor";
    }
}

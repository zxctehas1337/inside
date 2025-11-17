package com.arizon.client.hud.elements;

import com.arizon.client.hud.HudElement;
import com.arizon.client.gui.theme.ModernTheme;

import java.awt.*;

/**
 * Item count HUD element
 */
public class ItemCountElement extends HudElement {
    
    private int itemCount = 27;
    
    public ItemCountElement(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        String text = "Items: " + itemCount + "/36";
        
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
        return "Item Count";
    }
}

package com.arizon.client.hud.elements;

import com.arizon.client.ArizonClient;
import com.arizon.client.hud.HudElement;
import com.arizon.client.gui.theme.ModernTheme;

import java.awt.*;

/**
 * Client logo HUD element
 */
public class LogoElement extends HudElement {
    
    public LogoElement(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        String text = ArizonClient.getName();
        
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        width = fm.stringWidth(text) + 10;
        height = fm.getHeight() + 6;
        
        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, width, height, 5, 5);
        
        // Gradient text effect
        GradientPaint gradient = new GradientPaint(
            x, y, ModernTheme.ACCENT,
            x + width, y, ModernTheme.SECONDARY_ACCENT
        );
        g.setPaint(gradient);
        g.drawString(text, x + 5, y + fm.getAscent() + 3);
    }
    
    @Override
    public String getName() {
        return "Logo";
    }
}

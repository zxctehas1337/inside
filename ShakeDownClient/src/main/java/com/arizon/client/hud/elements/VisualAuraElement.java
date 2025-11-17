package com.arizon.client.hud.elements;

import com.arizon.client.hud.HudElement;
import com.arizon.client.gui.theme.ModernTheme;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Visual aura effect - shows attack range circle (VISUAL ONLY, no actual attacks)
 */
public class VisualAuraElement extends HudElement {
    
    private float radius = 3.5f;
    private float animationAngle = 0;
    private boolean showRange = true;
    
    public VisualAuraElement(int x, int y) {
        super(x, y);
        this.width = 200;
        this.height = 200;
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        // Animation
        animationAngle += 2;
        if (animationAngle >= 360) animationAngle = 0;
        
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int radiusPixels = (int)(radius * 20);
        
        // Background panel
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, width, height, 10, 10);
        
        // Title
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.setColor(ModernTheme.TEXT_PRIMARY);
        g.drawString("Visual Aura (Display Only)", x + 10, y + 20);
        
        // Range circle
        if (showRange) {
            g.setColor(new Color(124, 91, 255, 50));
            g.fillOval(centerX - radiusPixels, centerY - radiusPixels, 
                      radiusPixels * 2, radiusPixels * 2);
            
            // Border
            g.setColor(new Color(124, 91, 255, 150));
            g.setStroke(new BasicStroke(2));
            g.drawOval(centerX - radiusPixels, centerY - radiusPixels, 
                      radiusPixels * 2, radiusPixels * 2);
        }
        
        // Player indicator (center)
        g.setColor(ModernTheme.ACCENT);
        g.fillOval(centerX - 5, centerY - 5, 10, 10);
        
        // Rotating scan line effect
        double angleRad = Math.toRadians(animationAngle);
        int lineEndX = centerX + (int)(Math.cos(angleRad) * radiusPixels);
        int lineEndY = centerY + (int)(Math.sin(angleRad) * radiusPixels);
        
        g.setColor(new Color(124, 91, 255, 100));
        g.setStroke(new BasicStroke(2));
        g.drawLine(centerX, centerY, lineEndX, lineEndY);
        
        // Info text
        g.setFont(ModernTheme.FONT_SMALL);
        g.setColor(ModernTheme.TEXT_SECONDARY);
        g.drawString("Range: " + radius + " blocks", x + 10, y + height - 30);
        g.drawString("Status: Visual Effect Only", x + 10, y + height - 15);
        
        // Warning
        g.setColor(ModernTheme.WARNING);
        g.drawString("⚠ No actual attacks", x + 10, y + height - 45);
    }
    
    @Override
    public String getName() {
        return "Visual Aura";
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return radius;
    }
}

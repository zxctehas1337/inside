package com.arizon.client.hud;

import java.awt.*;

/**
 * Base class for HUD elements
 */
public abstract class HudElement {
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean dragging = false;
    protected int dragOffsetX;
    protected int dragOffsetY;
    protected boolean enabled = true;
    
    public HudElement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void render(Graphics2D g);
    
    public abstract String getName();
    
    public void updateSize(Graphics2D g) {
        // Override in subclasses to calculate size
    }
    
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }
    
    public void startDrag(int mouseX, int mouseY) {
        dragging = true;
        dragOffsetX = mouseX - x;
        dragOffsetY = mouseY - y;
    }
    
    public void stopDrag() {
        dragging = false;
    }
    
    public void updateDrag(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }
    }
    
    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
}

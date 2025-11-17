package com.arizon.client.hud;

import com.arizon.client.hud.elements.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD elements
 */
public class HudManager {
    
    private List<HudElement> elements;
    private boolean editMode = false;
    private HudElement draggedElement = null;
    
    public HudManager() {
        elements = new ArrayList<>();
        initializeElements();
    }
    
    private void initializeElements() {
        // Add default HUD elements
        elements.add(new LogoElement(10, 10));
        elements.add(new FpsElement(10, 40));
        elements.add(new CoordinatesElement(10, 70));
        elements.add(new ItemCountElement(10, 100));
        elements.add(new ArmorElement(10, 130));
        elements.add(new VisualAuraElement(600, 300));
    }
    
    public void render(Graphics2D g) {
        for (HudElement element : elements) {
            element.render(g);
            
            // Draw border in edit mode
            if (editMode && element.isEnabled()) {
                g.setColor(new Color(255, 255, 255, 100));
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, 
                           BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
                g.drawRect(element.getX(), element.getY(), 
                          element.getWidth(), element.getHeight());
            }
        }
        
        // Edit mode indicator
        if (editMode) {
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(new Color(255, 255, 0, 200));
            String text = "EDIT MODE - Drag elements to reposition";
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g.drawString(text, (800 - textWidth) / 2, 30);
        }
    }
    
    public void handleMousePressed(int mouseX, int mouseY) {
        if (!editMode) return;
        
        for (HudElement element : elements) {
            if (element.isEnabled() && element.isMouseOver(mouseX, mouseY)) {
                element.startDrag(mouseX, mouseY);
                draggedElement = element;
                break;
            }
        }
    }
    
    public void handleMouseReleased() {
        if (draggedElement != null) {
            draggedElement.stopDrag();
            draggedElement = null;
        }
    }
    
    public void handleMouseDragged(int mouseX, int mouseY) {
        if (draggedElement != null) {
            draggedElement.updateDrag(mouseX, mouseY);
        }
    }
    
    public void toggleEditMode() {
        editMode = !editMode;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public List<HudElement> getElements() {
        return elements;
    }
    
    public HudElement getElementByName(String name) {
        for (HudElement element : elements) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }
}

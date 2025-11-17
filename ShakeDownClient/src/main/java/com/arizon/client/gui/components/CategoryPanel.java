package com.arizon.client.gui.components;

import com.arizon.client.gui.theme.NeonTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Category panel for left sidebar - Neverlose style
 */
public class CategoryPanel {
    
    private final String name;
    private final String icon;
    private int x;
    private int y;
    private final int width = 70;
    private final int height = 30;
    
    public CategoryPanel(String name, String icon, int x, int y) {
        this.name = name;
        this.icon = icon;
        this.x = x;
        this.y = y;
    }
    
    public void render(DrawContext context, int mouseX, int mouseY, boolean selected) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        boolean hovered = isMouseOver(mouseX, mouseY);
        
        // Background
        if (selected) {
            context.fill(x, y, x + width, y + height, NeonTheme.CATEGORY_SELECTED);
            // Orange accent line on left
            context.fill(x, y, x + 3, y + height, NeonTheme.ACCENT_ORANGE);
        } else if (hovered) {
            context.fill(x, y, x + width, y + height, NeonTheme.CATEGORY_HOVER);
        }
        
        // Icon (centered)
        int iconColor = selected ? NeonTheme.ICON_ACTIVE : NeonTheme.ICON_NORMAL;
        int iconX = x + (width - client.textRenderer.getWidth(icon)) / 2;
        context.drawText(client.textRenderer, icon, iconX, y + 3, iconColor, false);
        
        // Category name (small, below icon)
        int textColor = selected ? NeonTheme.TEXT_PRIMARY : NeonTheme.TEXT_SECONDARY;
        String shortName = name.length() > 6 ? name.substring(0, 6) : name;
        int textX = x + (width - client.textRenderer.getWidth(shortName)) / 2;
        context.drawText(client.textRenderer, shortName, textX, y + 18, textColor, false);
    }
    
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y && mouseY <= y + height;
    }
    
    public String getName() {
        return name;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

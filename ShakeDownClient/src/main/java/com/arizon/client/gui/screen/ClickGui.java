package com.arizon.client.gui.screen;

import com.arizon.client.module.Module;
import com.arizon.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.*;

/**
 * ClickGUI - Modern module interface with categories
 */
public class ClickGui extends Screen {
    
    // Categories
    private enum Category {
        COMBAT("Combat", 0x8A4BFF),
        MOVEMENT("Movement", 0x4BFF8A),
        RENDER("Visual", 0xFF4B8A),
        PLAYER("Player", 0xFFB84B),
        WORLD("World", 0x4B8AFF),
        MISC("Misc", 0xFF4BFF);
        
        final String name;
        final int color;
        
        Category(String name, int color) {
            this.name = name;
            this.color = color;
        }
    }
    
    // Layout
    private static final int CATEGORY_WIDTH = 180;
    private static final int CATEGORY_HEIGHT = 25;
    private static final int MODULE_HEIGHT = 20;
    private static final int PADDING = 5;
    private static final int SPACING = 10;
    
    // State
    private final Map<Category, List<Module>> modulesByCategory = new HashMap<>();
    private final Map<Category, Boolean> expandedCategories = new HashMap<>();
    private final Map<Category, Float> categoryX = new HashMap<>();
    private final Map<Category, Float> categoryY = new HashMap<>();
    
    // Dragging
    private Category draggingCategory = null;
    private float dragOffsetX, dragOffsetY;
    
    // Colors
    private final Color BG_MAIN = new Color(15, 15, 20, 240);
    private final Color BG_HOVER = new Color(25, 25, 35, 255);
    private final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);
    private final Color TEXT_SECONDARY = new Color(175, 175, 185, 255);
    private final Color MODULE_ENABLED = new Color(138, 75, 255, 255);
    
    public ClickGui() {
        super(Text.literal("ClickGUI"));
        initializeCategories();
        loadModules();
    }
    
    private void initializeCategories() {
        // Initialize all categories as expanded
        for (Category cat : Category.values()) {
            expandedCategories.put(cat, true);
            modulesByCategory.put(cat, new ArrayList<>());
        }
        
        // Set initial positions (grid layout)
        int x = 50;
        int y = 50;
        int col = 0;
        
        for (Category cat : Category.values()) {
            categoryX.put(cat, (float) x);
            categoryY.put(cat, (float) y);
            
            col++;
            if (col >= 3) {
                col = 0;
                x = 50;
                y += 300;
            } else {
                x += CATEGORY_WIDTH + SPACING;
            }
        }
    }
    
    private void loadModules() {
        ModuleManager manager = ModuleManager.getInstance();
        if (manager == null) return;
        
        for (Module module : manager.getModules()) {
            Category category = getCategoryForModule(module);
            modulesByCategory.get(category).add(module);
        }
    }
    
    private Category getCategoryForModule(Module module) {
        String packageName = module.getClass().getPackage().getName();
        
        if (packageName.contains(".combat")) return Category.COMBAT;
        if (packageName.contains(".movement")) return Category.MOVEMENT;
        if (packageName.contains(".render")) return Category.RENDER;
        if (packageName.contains(".player")) return Category.PLAYER;
        if (packageName.contains(".world")) return Category.WORLD;
        
        return Category.MISC;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render each category
        for (Category category : Category.values()) {
            renderCategory(context, category, mouseX, mouseY);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderCategory(DrawContext context, Category category, int mouseX, int mouseY) {
        float x = categoryX.get(category);
        float y = categoryY.get(category);
        
        List<Module> modules = modulesByCategory.get(category);
        boolean expanded = expandedCategories.get(category);
        
        int totalHeight = CATEGORY_HEIGHT;
        if (expanded) {
            totalHeight += modules.size() * MODULE_HEIGHT;
        }
        
        // Background
        fillRect(context, (int)x, (int)y, CATEGORY_WIDTH, totalHeight, BG_MAIN);
        
        // Category header
        boolean headerHovered = mouseX >= x && mouseX <= x + CATEGORY_WIDTH &&
                               mouseY >= y && mouseY <= y + CATEGORY_HEIGHT;
        
        if (headerHovered) {
            fillRect(context, (int)x, (int)y, CATEGORY_WIDTH, CATEGORY_HEIGHT, BG_HOVER);
        }
        
        // Category color bar
        fillRect(context, (int)x, (int)y, 3, CATEGORY_HEIGHT, new Color(category.color));
        
        // Category name
        context.drawText(textRenderer, category.name, (int)x + 10, (int)y + 8, 
            TEXT_PRIMARY.getRGB(), true);
        
        // Expand/collapse indicator
        String indicator = expanded ? "▼" : "▶";
        context.drawText(textRenderer, indicator, 
            (int)x + CATEGORY_WIDTH - 15, (int)y + 8, TEXT_SECONDARY.getRGB(), false);
        
        // Module count
        String count = "(" + modules.size() + ")";
        int countWidth = textRenderer.getWidth(count);
        context.drawText(textRenderer, count, 
            (int)x + CATEGORY_WIDTH - countWidth - 25, (int)y + 8, 
            TEXT_SECONDARY.getRGB(), false);
        
        // Render modules if expanded
        if (expanded) {
            int moduleY = (int)y + CATEGORY_HEIGHT;
            
            for (Module module : modules) {
                renderModule(context, module, (int)x, moduleY, mouseX, mouseY);
                moduleY += MODULE_HEIGHT;
            }
        }
    }
    
    private void renderModule(DrawContext context, Module module, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + CATEGORY_WIDTH &&
                         mouseY >= y && mouseY <= y + MODULE_HEIGHT;
        
        // Background
        if (hovered) {
            fillRect(context, x, y, CATEGORY_WIDTH, MODULE_HEIGHT, BG_HOVER);
        }
        
        // Enabled indicator
        if (module.isEnabled()) {
            fillRect(context, x, y, 3, MODULE_HEIGHT, MODULE_ENABLED);
        }
        
        // Module name
        int textColor = module.isEnabled() ? MODULE_ENABLED.getRGB() : TEXT_PRIMARY.getRGB();
        context.drawText(textRenderer, module.getName(), x + 10, y + 6, textColor, false);
        
        // Settings icon (if has settings)
        if (module.hasSettings()) {
            context.drawText(textRenderer, "⚙", x + CATEGORY_WIDTH - 15, y + 6, 
                TEXT_SECONDARY.getRGB(), false);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Category category : Category.values()) {
            float x = categoryX.get(category);
            float y = categoryY.get(category);
            
            // Check category header click
            if (mouseX >= x && mouseX <= x + CATEGORY_WIDTH &&
                mouseY >= y && mouseY <= y + CATEGORY_HEIGHT) {
                
                if (button == 0) { // Left click - drag
                    draggingCategory = category;
                    dragOffsetX = (float) (mouseX - x);
                    dragOffsetY = (float) (mouseY - y);
                    return true;
                } else if (button == 1) { // Right click - expand/collapse
                    expandedCategories.put(category, !expandedCategories.get(category));
                    return true;
                }
            }
            
            // Check module clicks
            if (expandedCategories.get(category)) {
                List<Module> modules = modulesByCategory.get(category);
                int moduleY = (int)y + CATEGORY_HEIGHT;
                
                for (Module module : modules) {
                    if (mouseX >= x && mouseX <= x + CATEGORY_WIDTH &&
                        mouseY >= moduleY && mouseY <= moduleY + MODULE_HEIGHT) {
                        
                        if (button == 0) { // Left click - toggle
                            module.toggle();
                            return true;
                        } else if (button == 1 && module.hasSettings()) { // Right click - settings
                            // TODO: Open settings GUI
                            return true;
                        }
                    }
                    moduleY += MODULE_HEIGHT;
                }
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingCategory != null) {
            categoryX.put(draggingCategory, (float) (mouseX - dragOffsetX));
            categoryY.put(draggingCategory, (float) (mouseY - dragOffsetY));
            return true;
        }
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingCategory = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Utility methods
    private void fillRect(DrawContext context, int x, int y, int width, int height, Color color) {
        context.fill(x, y, x + width, y + height, color.getRGB());
    }
}

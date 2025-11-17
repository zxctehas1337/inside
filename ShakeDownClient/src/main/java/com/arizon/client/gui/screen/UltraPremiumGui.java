package com.arizon.client.gui.screen;

import com.arizon.client.gui.render.RenderHelper;
import com.arizon.client.module.Module;
import com.arizon.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.*;

/**
 * ULTRA PREMIUM GUI - Pixel-perfect design with smooth animations
 * Every pixel matters!
 */
public class UltraPremiumGui extends Screen {
    
    // ==================== DIMENSIONS ====================
    // Main GUI - Resizable
    private float guiX;
    private float guiY;
    private int guiWidth = 600;  // Увеличено для стандартного вида
    private int guiHeight = 340; // В 2 раза меньше
    private final int MIN_WIDTH = 500;
    private final int MIN_HEIGHT = 300;
    private final int MAX_WIDTH = 1200;
    private final int MAX_HEIGHT = 800;
    
    // Sidebar dimensions - narrow like in screenshot
    private final int SIDEBAR_WIDTH = 120;
    private final int SIDEBAR_PADDING = 8;
    
    // Search bar - compact
    private final int SEARCH_HEIGHT = 19;
    private final int SEARCH_MARGIN_BOTTOM = 6;
    
    // Category button - compact
    private final int CATEGORY_HEIGHT = 21;
    private final int CATEGORY_SPACING = 3;
    private final int CATEGORY_ICON_SIZE = 10;
    
    // Module item (no cards, just rows) - scaled down
    private final int MODULE_ITEM_WIDTH = 172;
    private final int MODULE_ITEM_HEIGHT = 24;
    private final int MODULE_ITEM_SPACING = 3;
    private final int MODULE_COLUMNS = 2;
    private final int MODULE_COLUMN_SPACING = 17;
    
    // Toggle switch - scaled down
    private final int TOGGLE_WIDTH = 19;
    private final int TOGGLE_HEIGHT = 10;
    private final int TOGGLE_CIRCLE_SIZE = 8;
    
    // Border radius - exact from screenshot
    private final int RADIUS_LARGE = 14;
    private final int RADIUS_MEDIUM = 10;
    private final int RADIUS_SMALL = 6;
    
    // ==================== COLORS (PIXEL-PERFECT FROM SCREENSHOT) ====================
    // Background colors - exact RGB values
    private final Color BG_MAIN = new Color(10, 10, 15, 248);
    private final Color BG_SIDEBAR = new Color(16, 16, 21, 255);
    private final Color BG_CONTENT = new Color(12, 12, 17, 255);
    private final Color BG_HOVER = new Color(24, 24, 30, 255);
    private final Color BG_SEARCH = new Color(20, 20, 26, 255);
    
    // Accent colors - Purple gradient (EXACT RGB from screenshot analysis)
    private final Color ACCENT_PRIMARY = new Color(138, 75, 255, 255);      // #8A4BFF
    private final Color ACCENT_SECONDARY = new Color(108, 55, 215, 255);    // #6C37D7
    private final Color ACCENT_GLOW = new Color(138, 75, 255, 45);
    
    // Text colors - EXACT RGB from screenshot
    private final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);       // #FFFFFF
    private final Color TEXT_SECONDARY = new Color(175, 175, 185, 255);     // #AFAFB9
    private final Color TEXT_TERTIARY = new Color(115, 115, 130, 255);      // #737382
    private final Color TEXT_DISABLED = new Color(75, 75, 90, 255);         // #4B4B5A
    
    // Toggle colors - EXACT RGB from screenshot
    private final Color TOGGLE_ON_BG = new Color(138, 75, 255, 255);        // #8A4BFF
    private final Color TOGGLE_OFF_BG = new Color(52, 52, 62, 255);         // #34343E
    private final Color TOGGLE_CIRCLE = new Color(255, 255, 255, 255);      // #FFFFFF
    
    // Shadow colors
    private final Color SHADOW_STRONG = new Color(0, 0, 0, 100);
    private final Color SHADOW_MEDIUM = new Color(0, 0, 0, 60);
    private final Color SHADOW_LIGHT = new Color(0, 0, 0, 30);
    
    // ==================== STATE ====================
    private boolean dragging = false;
    private float dragOffsetX, dragOffsetY;
    
    // Resizing
    private boolean resizing = false;
    private int resizeStartWidth, resizeStartHeight;
    private double resizeStartX, resizeStartY;
    
    // Smooth scrolling with physics
    private float scrollOffset = 0;
    private float scrollVelocity = 0;
    private float scrollTarget = 0;
    private final float SCROLL_DAMPING = 0.82f;
    private final float SCROLL_SPRING = 0.15f;
    
    // Categories
    private final List<Category> categories = new ArrayList<>();
    private Category selectedCategory;
    private Category hoveredCategory = null;
    
    // Modules
    private Module hoveredModule = null;
    private Module selectedModule = null; // For settings panel
    
    // Settings panel animation (теперь справа от меню)
    private float settingsPanelOffset = 0; // 0 = hidden, 1 = visible
    private float settingsPanelTargetOffset = 0;
    private final int SETTINGS_PANEL_WIDTH = 200; // Увеличена ширина
    
    // Settings panel scroll
    private float settingsScrollOffset = 0;
    private float settingsScrollVelocity = 0;
    private float settingsScrollTarget = 0;
    
    // Slider dragging
    private boolean draggingSlider = false;
    private String draggingSliderName = "";
    
    // Dropdown state
    private String openDropdown = null;
    
    // Search
    private String searchQuery = "";
    private boolean searchFocused = false;
    
    // Animation time
    private long lastFrameTime;
    private float deltaTime;
    
    // GUI open/close animation
    private float guiScale = 0.0f;
    private float guiAlpha = 0.0f;
    private boolean closing = false;
    
    public UltraPremiumGui() {
        super(Text.literal("Ultra Premium GUI"));
        initCategories();
        lastFrameTime = System.currentTimeMillis();
    }
    
    @Override
    protected void init() {
        super.init();
        // Position GUI slightly to the left
        guiX = (width - guiWidth) / 2f - 100; // Сдвиг влево на 100px
        guiY = (height - guiHeight) / 2f;
    }
    
    private void initCategories() {
        categories.add(new Category("Combat", "⚔", "Боевые модули"));
        categories.add(new Category("Movement", "➤", "Движение"));
        categories.add(new Category("Visuals", "👁", "Визуальные эффекты"));
        categories.add(new Category("Player", "👤", "Игрок"));
        categories.add(new Category("Other", "⋯", "Прочее"));
        
        selectedCategory = categories.get(0);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Calculate delta time for smooth animations
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTime;
        
        // Update animations
        updateGuiAnimation();
        updateScrollPhysics();
        updateSettingsPanelAnimation();
        
        // Close GUI when animation finishes
        if (closing && guiScale <= 0.01f) {
            super.close();
            return;
        }
        
        // Render background blur effect
        renderBlurredBackground(context);
        
        // Apply scale and alpha for animation
        context.getMatrices().push();
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(guiScale, guiScale, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);
        
        int x = (int) guiX;
        int y = (int) guiY;
        
        // ==================== OUTER GLOW ====================
        renderOuterGlow(context, x, y);
        
        // ==================== MAIN CONTAINER ====================
        RenderHelper.drawRoundedRect(context, x, y, guiWidth, guiHeight, RADIUS_LARGE, BG_MAIN);
        
        // Close button (X)
        int closeX = x + guiWidth - 25;
        int closeY = y + 8;
        boolean closeHovered = mouseX >= closeX && mouseX <= closeX + 15 &&
                              mouseY >= closeY && mouseY <= closeY + 15;
        context.drawText(textRenderer, "✕", closeX, closeY, 
            closeHovered ? TEXT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);
        
        // Resize handle (bottom-right corner)
        int resizeX = x + guiWidth - 12;
        int resizeY = y + guiHeight - 12;
        context.drawText(textRenderer, "⋰", resizeX, resizeY, TEXT_TERTIARY.getRGB(), false);
        
        // ==================== SIDEBAR ====================
        renderSidebar(context, x, y, mouseX, mouseY);
        
        // ==================== CONTENT AREA ====================
        renderContentArea(context, x, y, mouseX, mouseY);
        
        // ==================== SETTINGS PANEL ====================
        if (settingsPanelOffset > 0.01f) {
            renderSettingsPanel(context, x, y, mouseX, mouseY);
        }
        
        context.getMatrices().pop();
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    // ==================== RENDER OUTER GLOW ====================
    private void renderOuterGlow(DrawContext context, int x, int y) {
        // Subtle purple glow (3 layers)
        for (int i = 0; i < 3; i++) {
            int offset = (3 - i) * 2;
            int alpha = 10 - (i * 3);
            Color glowColor = new Color(
                ACCENT_PRIMARY.getRed(),
                ACCENT_PRIMARY.getGreen(),
                ACCENT_PRIMARY.getBlue(),
                alpha
            );
            
            fillRoundedRect(context, 
                x - offset, 
                y - offset, 
                guiWidth + offset * 2, 
                guiHeight + offset * 2, 
                RADIUS_LARGE + offset, 
                glowColor);
        }
    }
    
    // ==================== RENDER SIDEBAR ====================
    private void renderSidebar(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int sidebarX = x + 10;
        int sidebarY = y + 10;
        int sidebarWidth = SIDEBAR_WIDTH;
        int sidebarHeight = guiHeight - 20;
        
        // Sidebar background with subtle gradient
        fillRoundedRectGradient(context, 
            sidebarX, sidebarY, 
            sidebarWidth, sidebarHeight, 
            RADIUS_MEDIUM, 
            BG_SIDEBAR, 
            new Color(BG_SIDEBAR.getRed() - 3, BG_SIDEBAR.getGreen() - 3, BG_SIDEBAR.getBlue() - 3, 255));
        
        // Inner shadow for depth
        renderInnerShadow(context, sidebarX, sidebarY, sidebarWidth, sidebarHeight, RADIUS_MEDIUM);
        
        int contentX = sidebarX + SIDEBAR_PADDING;
        int contentY = sidebarY + SIDEBAR_PADDING;
        int contentWidth = sidebarWidth - SIDEBAR_PADDING * 2;
        
        // ==================== SEARCH BAR ====================
        renderSearchBar(context, contentX, contentY, contentWidth, mouseX, mouseY);
        
        contentY += SEARCH_HEIGHT + SEARCH_MARGIN_BOTTOM;
        
        // ==================== CATEGORIES LABEL ====================
        context.drawText(textRenderer, "Функции", contentX + 2, contentY, TEXT_DISABLED.getRGB(), false);
        contentY += 20;
        
        // ==================== CATEGORIES ====================
        renderCategories(context, contentX, contentY, contentWidth, mouseX, mouseY);
    }
    
    // ==================== RENDER SEARCH BAR ====================
    private void renderSearchBar(DrawContext context, int x, int y, int width, int mouseX, int mouseY) {
        boolean isHovered = mouseX >= x && mouseX <= x + width &&
                          mouseY >= y && mouseY <= y + SEARCH_HEIGHT;
        
        // Background (darker)
        fillRoundedRect(context, x, y, width, SEARCH_HEIGHT, RADIUS_SMALL, BG_SEARCH);
        
        // Search icon
        int iconX = x + 12;
        int iconY = y + (SEARCH_HEIGHT - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, "🔍", iconX, iconY, TEXT_TERTIARY.getRGB(), false);
        
        // Search text or placeholder
        int textX = iconX + 20;
        int textY = y + (SEARCH_HEIGHT - textRenderer.fontHeight) / 2;
        
        if (searchQuery.isEmpty()) {
            context.drawText(textRenderer, "Поиск", textX, textY, TEXT_TERTIARY.getRGB(), false);
        } else {
            context.drawText(textRenderer, searchQuery, textX, textY, TEXT_PRIMARY.getRGB(), false);
        }
    }
    
    // ==================== RENDER CATEGORIES ====================
    private void renderCategories(DrawContext context, int x, int y, int width, int mouseX, int mouseY) {
        int currentY = y;
        hoveredCategory = null;
        
        for (Category category : categories) {
            boolean isSelected = category == selectedCategory;
            boolean isHovered = mouseX >= x && mouseX <= x + width &&
                              mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT;
            
            if (isHovered) {
                hoveredCategory = category;
            }
            
            renderCategoryButton(context, x, currentY, width, category, isSelected, isHovered);
            
            currentY += CATEGORY_HEIGHT + CATEGORY_SPACING;
        }
    }
    
    // ==================== RENDER CATEGORY BUTTON ====================
    private void renderCategoryButton(DrawContext context, int x, int y, int width, Category category, 
                                     boolean isSelected, boolean isHovered) {
        if (isSelected) {
            // Selected: Purple gradient with rounded corners
            fillRoundedRectGradient(context, x, y, width, CATEGORY_HEIGHT, RADIUS_SMALL,
                ACCENT_PRIMARY, ACCENT_SECONDARY);
        } else if (isHovered) {
            // Hovered: Subtle lighter background
            fillRoundedRect(context, x, y, width, CATEGORY_HEIGHT, RADIUS_SMALL, 
                new Color(25, 25, 32, 255));
        }
        
        // Icon
        int iconX = x + 12;
        int iconY = y + (CATEGORY_HEIGHT - textRenderer.fontHeight) / 2;
        
        context.drawText(textRenderer, category.icon, iconX, iconY, 
            isSelected ? TEXT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);
        
        // Category name (no description in sidebar)
        int nameX = iconX + 22;
        int nameY = y + (CATEGORY_HEIGHT - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, category.name, nameX, nameY, 
            isSelected ? TEXT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);
    }
    
    // ==================== RENDER CONTENT AREA ====================
    private void renderContentArea(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int contentX = x + SIDEBAR_WIDTH + 20;
        int contentY = y + 15;
        int contentWidth = guiWidth - SIDEBAR_WIDTH - 35;
        int contentHeight = guiHeight - 30;
        
        // Content background (very dark, almost black)
        fillRoundedRect(context, contentX, contentY, contentWidth, contentHeight, 
            RADIUS_MEDIUM, BG_CONTENT);
        
        // Modules grid with scroll
        int modulesY = contentY + 15;
        int modulesHeight = contentHeight - 30;
        
        renderModulesGrid(context, contentX + 20, modulesY, contentWidth - 40, modulesHeight, mouseX, mouseY);
        
        // Scrollbar (thin, subtle)
        renderScrollbar(context, contentX + contentWidth - 10, modulesY, 4, modulesHeight);
    }
    
    // ==================== RENDER CONTENT HEADER ====================
    private void renderContentHeader(DrawContext context, int x, int y, int width) {
        int headerHeight = 70;
        
        // Category icon (large)
        int iconSize = 40;
        int iconX = x + 20;
        int iconY = y + 15;
        
        // Icon background with gradient
        fillRoundedRectGradient(context, iconX, iconY, iconSize, iconSize, RADIUS_SMALL,
            new Color(ACCENT_PRIMARY.getRed(), ACCENT_PRIMARY.getGreen(), ACCENT_PRIMARY.getBlue(), 40),
            new Color(ACCENT_SECONDARY.getRed(), ACCENT_SECONDARY.getGreen(), ACCENT_SECONDARY.getBlue(), 40));
        
        context.drawText(textRenderer, selectedCategory.icon, iconX + 10, iconY + 12, 
            ACCENT_PRIMARY.getRGB(), false);
        
        // Category name (large)
        int nameX = iconX + iconSize + 15;
        int nameY = y + 18;
        
        // Draw with shadow for depth
        context.drawText(textRenderer, selectedCategory.name, nameX + 1, nameY + 1, 
            new Color(0, 0, 0, 80).getRGB(), false);
        context.drawText(textRenderer, selectedCategory.name, nameX, nameY, 
            TEXT_PRIMARY.getRGB(), true);
        
        // Module count
        List<Module> modules = getModulesForCategory(selectedCategory);
        String countText = modules.size() + " модулей";
        context.drawText(textRenderer, countText, nameX, nameY + 16, TEXT_TERTIARY.getRGB(), false);
        
        // Separator line
        int lineY = y + headerHeight - 5;
        fillRoundedRect(context, x + 20, lineY, width - 40, 1, 0, 
            new Color(TEXT_TERTIARY.getRed(), TEXT_TERTIARY.getGreen(), TEXT_TERTIARY.getBlue(), 30));
    }
    
    // ==================== RENDER MODULES GRID ====================
    private void renderModulesGrid(DrawContext context, int x, int y, int width, int height, int mouseX, int mouseY) {
        // Enable scissor for clipping
        enableScissor(x, y, width, height);
        
        int currentY = y - (int) scrollOffset;
        hoveredModule = null;
        
        // Render all categories with their modules
        for (Category category : categories) {
            List<Module> modules = getModulesForCategory(category);
            if (modules.isEmpty()) continue;
            
            // Category header
            if (currentY + 40 >= y && currentY <= y + height) {
                renderCategoryHeader(context, x, currentY, width, category);
            }
            currentY += 50;
            
            // Modules in 2 columns
            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);
                
                int col = i % MODULE_COLUMNS;
                int row = i / MODULE_COLUMNS;
                
                int itemX = x + col * (MODULE_ITEM_WIDTH + MODULE_COLUMN_SPACING);
                int itemY = currentY + row * (MODULE_ITEM_HEIGHT + MODULE_ITEM_SPACING);
                
                // Skip if outside visible area
                if (itemY + MODULE_ITEM_HEIGHT < y || itemY > y + height) continue;
                
                boolean isHovered = mouseX >= itemX && mouseX <= itemX + MODULE_ITEM_WIDTH &&
                                  mouseY >= itemY && mouseY <= itemY + MODULE_ITEM_HEIGHT &&
                                  mouseY >= y && mouseY <= y + height;
                
                if (isHovered) {
                    hoveredModule = module;
                }
                
                renderModuleItem(context, itemX, itemY, module, isHovered, mouseX, mouseY);
            }
            
            int rows = (modules.size() + MODULE_COLUMNS - 1) / MODULE_COLUMNS;
            currentY += rows * (MODULE_ITEM_HEIGHT + MODULE_ITEM_SPACING) + 30;
        }
        
        disableScissor();
    }
    
    // ==================== RENDER CATEGORY HEADER ====================
    private void renderCategoryHeader(DrawContext context, int x, int y, int width, Category category) {
        // Icon (larger)
        int iconSize = 20;
        context.drawText(textRenderer, category.icon, x, y, TEXT_PRIMARY.getRGB(), false);
        
        // Name (large, bold, white)
        int nameX = x + iconSize + 8;
        context.drawText(textRenderer, category.name, nameX, y, TEXT_PRIMARY.getRGB(), true);
    }
    
    // ==================== RENDER MODULE ITEM (NO CARD) ====================
    private void renderModuleItem(DrawContext context, int x, int y, Module module, boolean isHovered, int mouseX, int mouseY) {
        // Hover background (very subtle, barely visible)
        if (isHovered) {
            fillRoundedRect(context, x - 8, y - 3, MODULE_ITEM_WIDTH + 16, MODULE_ITEM_HEIGHT + 6, 
                RADIUS_SMALL, new Color(20, 20, 26, 120));
        }
        
        int contentX = x;
        int contentY = y + 5;
        
        // Module name (white, medium size)
        context.drawText(textRenderer, module.getName(), contentX, contentY, 
            TEXT_PRIMARY.getRGB(), false);
        
        // Module description (gray, smaller)
        String description = getModuleDescription(module);
        int descY = contentY + 13;
        
        // Truncate if too long
        int maxDescWidth = MODULE_ITEM_WIDTH - 60;
        if (textRenderer.getWidth(description) > maxDescWidth) {
            while (textRenderer.getWidth(description + "...") > maxDescWidth && description.length() > 0) {
                description = description.substring(0, description.length() - 1);
            }
            description += "...";
        }
        
        context.drawText(textRenderer, description, contentX, descY, TEXT_TERTIARY.getRGB(), false);
        
        // Toggle switch (compact style, right aligned)
        int toggleX = x + MODULE_ITEM_WIDTH - TOGGLE_WIDTH - 5;
        int toggleY = y + (MODULE_ITEM_HEIGHT - TOGGLE_HEIGHT) / 2;
        
        boolean toggleHovered = mouseX >= toggleX && mouseX <= toggleX + TOGGLE_WIDTH &&
                               mouseY >= toggleY && mouseY <= toggleY + TOGGLE_HEIGHT;
        
        renderCompactToggle(context, toggleX, toggleY, module.isEnabled(), toggleHovered);
    }
    
    // ==================== RENDER COMPACT TOGGLE ====================
    private void renderCompactToggle(DrawContext context, int x, int y, boolean enabled, boolean hovered) {
        // Background track (rounded pill shape)
        if (enabled) {
            fillRoundedRect(context, x, y, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT / 2, TOGGLE_ON_BG);
        } else {
            fillRoundedRect(context, x, y, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT / 2, TOGGLE_OFF_BG);
        }
        
        // Circle thumb (smooth animation position)
        int circleX = enabled ? x + TOGGLE_WIDTH - TOGGLE_CIRCLE_SIZE - 2 : x + 2;
        int circleY = y + 2;
        
        // Circle with slight shadow
        fillCircle(context, circleX + TOGGLE_CIRCLE_SIZE / 2 + 1, 
            circleY + TOGGLE_CIRCLE_SIZE / 2 + 1, 
            TOGGLE_CIRCLE_SIZE / 2, new Color(0, 0, 0, 30));
        
        fillCircle(context, circleX + TOGGLE_CIRCLE_SIZE / 2, 
            circleY + TOGGLE_CIRCLE_SIZE / 2, 
            TOGGLE_CIRCLE_SIZE / 2, TOGGLE_CIRCLE);
    }
    
    // ==================== RENDER PREMIUM TOGGLE ====================
    private void renderPremiumToggle(DrawContext context, int x, int y, boolean enabled, boolean hovered) {
        // Background track with gradient
        if (enabled) {
            fillRoundedRectGradient(context, x, y, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT / 2,
                ACCENT_PRIMARY, ACCENT_SECONDARY);
            
            // Inner glow
            fillRoundedRect(context, x + 2, y + 2, TOGGLE_WIDTH - 4, TOGGLE_HEIGHT - 4, 
                (TOGGLE_HEIGHT - 4) / 2, new Color(255, 255, 255, 20));
        } else {
            fillRoundedRect(context, x, y, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT / 2, TOGGLE_OFF_BG);
        }
        
        // Hover effect
        if (hovered) {
            drawRoundedBorder(context, x, y, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT / 2,
                new Color(255, 255, 255, 40), 1);
        }
        
        // Circle thumb with smooth animation
        int circleX = enabled ? x + TOGGLE_WIDTH - TOGGLE_CIRCLE_SIZE - 2 : x + 2;
        int circleY = y + 2;
        
        // Circle shadow
        fillCircle(context, circleX + TOGGLE_CIRCLE_SIZE / 2 + 1, 
            circleY + TOGGLE_CIRCLE_SIZE / 2 + 1, 
            TOGGLE_CIRCLE_SIZE / 2, new Color(0, 0, 0, 40));
        
        // Circle
        fillCircle(context, circleX + TOGGLE_CIRCLE_SIZE / 2, 
            circleY + TOGGLE_CIRCLE_SIZE / 2, 
            TOGGLE_CIRCLE_SIZE / 2, TOGGLE_CIRCLE);
        
        // Inner circle highlight
        fillCircle(context, circleX + TOGGLE_CIRCLE_SIZE / 2, 
            circleY + TOGGLE_CIRCLE_SIZE / 2 - 1, 
            TOGGLE_CIRCLE_SIZE / 2 - 3, new Color(255, 255, 255, 180));
    }
    
    // ==================== RENDER SCROLLBAR ====================
    private void renderScrollbar(DrawContext context, int x, int y, int width, int height) {
        float maxScroll = getMaxScroll();
        if (maxScroll <= 0) return;
        
        // Track (very subtle, almost invisible)
        fillRoundedRect(context, x, y, width, height, width / 2, 
            new Color(20, 20, 26, 80));
        
        // Thumb (purple, rounded)
        float thumbHeight = Math.max(40, height * (height / (height + maxScroll)));
        float thumbY = y + (scrollOffset / maxScroll) * (height - thumbHeight);
        
        fillRoundedRect(context, x, (int) thumbY, width, (int) thumbHeight, width / 2, 
            new Color(ACCENT_PRIMARY.getRed(), ACCENT_PRIMARY.getGreen(), ACCENT_PRIMARY.getBlue(), 180));
    }
    
    private void scrollToCategorysmooth(Category category) {
        int targetY = 0;
        
        // Calculate Y position of category
        for (Category cat : categories) {
            if (cat == category) break;
            
            List<Module> modules = getModulesForCategory(cat);
            if (!modules.isEmpty()) {
                targetY += 50; // Header
                int rows = (modules.size() + MODULE_COLUMNS - 1) / MODULE_COLUMNS;
                targetY += rows * (MODULE_ITEM_HEIGHT + MODULE_ITEM_SPACING) + 30;
            }
        }
        
        scrollTarget = targetY;
    }
    
    // ==================== GUI ANIMATION ====================
    private void updateGuiAnimation() {
        if (closing) {
            // Close animation
            guiScale -= deltaTime * 4.0f;
            guiAlpha -= deltaTime * 5.0f;
            
            if (guiScale < 0) guiScale = 0;
            if (guiAlpha < 0) guiAlpha = 0;
        } else {
            // Open animation
            guiScale += (1.0f - guiScale) * 0.2f;
            guiAlpha += (1.0f - guiAlpha) * 0.25f;
            
            if (guiScale > 0.99f) guiScale = 1.0f;
            if (guiAlpha > 0.99f) guiAlpha = 1.0f;
        }
    }
    
    @Override
    public void close() {
        closing = true;
    }
    
    // ==================== SCROLL PHYSICS ====================
    private void updateScrollPhysics() {
        // Spring physics for smooth scrolling (main content)
        float diff = scrollTarget - scrollOffset;
        scrollVelocity += diff * SCROLL_SPRING;
        scrollVelocity *= SCROLL_DAMPING;
        
        scrollOffset += scrollVelocity;
        
        // Clamp
        float maxScroll = getMaxScroll();
        if (scrollOffset < 0) {
            scrollOffset = 0;
            scrollVelocity = 0;
        } else if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
            scrollVelocity = 0;
        }
        
        // Stop if too slow
        if (Math.abs(scrollVelocity) < 0.1f && Math.abs(diff) < 0.1f) {
            scrollVelocity = 0;
        }
        
        // Settings panel scroll physics (smooth animation)
        float settingsDiff = settingsScrollTarget - settingsScrollOffset;
        settingsScrollVelocity += settingsDiff * SCROLL_SPRING;
        settingsScrollVelocity *= SCROLL_DAMPING;
        
        settingsScrollOffset += settingsScrollVelocity;
        
        // Clamp settings scroll
        float maxSettingsScroll = getMaxSettingsScroll();
        if (settingsScrollOffset < 0) {
            settingsScrollOffset = 0;
            settingsScrollVelocity = 0;
        } else if (settingsScrollOffset > maxSettingsScroll) {
            settingsScrollOffset = maxSettingsScroll;
            settingsScrollVelocity = 0;
        }
        
        // Stop if too slow
        if (Math.abs(settingsScrollVelocity) < 0.1f && Math.abs(settingsDiff) < 0.1f) {
            settingsScrollVelocity = 0;
        }
    }
    
    private float getMaxSettingsScroll() {
        if (selectedModule == null) return 0;
        
        // Calculate total height of settings
        int totalHeight = 0;
        String moduleName = selectedModule.getName();
        
        if (moduleName.equals("Aura")) {
            // Range (15 + 30) + PVP (15 + 38) + Target (15 + 38) + Attack (15 + 38) + 5 checkboxes (20*5)
            totalHeight = 45 + 53 + 53 + 53 + 100 + 50; // = 354, увеличим для запаса
            totalHeight = 600;
        } else if (moduleName.equals("Triggerbot")) {
            totalHeight = 200;
        } else if (moduleName.equals("Speed")) {
            totalHeight = 150;
        } else if (moduleName.equals("Removals")) {
            totalHeight = 300;
        } else if (moduleName.equals("SwingAnimations")) {
            totalHeight = 200;
        } else if (moduleName.equals("ElytraTarget")) {
            // 2 checkboxes + 4 sliders = 45 + 4*50 = 245
            totalHeight = 280;
        }
        
        int visibleHeight = guiHeight - 100;
        return Math.max(0, totalHeight - visibleHeight);
    }
    
    private float getMaxScroll() {
        int totalHeight = 0;
        
        // Calculate total height for all categories
        for (Category category : categories) {
            List<Module> modules = getModulesForCategory(category);
            if (modules.isEmpty()) continue;
            
            // Category header
            totalHeight += 50;
            
            // Modules
            int rows = (modules.size() + MODULE_COLUMNS - 1) / MODULE_COLUMNS;
            totalHeight += rows * (MODULE_ITEM_HEIGHT + MODULE_ITEM_SPACING) + 30;
        }
        
        int visibleHeight = guiHeight - 180;
        return Math.max(0, totalHeight - visibleHeight);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int x = (int) guiX;
        int y = (int) guiY;
        
        // Check if mouse is over settings panel
        if (selectedModule != null && settingsPanelOffset > 0.5f) {
            int panelX = x + guiWidth + 10 + (int)((1.0f - settingsPanelOffset) * -SETTINGS_PANEL_WIDTH);
            
            if (mouseX >= panelX && mouseX <= panelX + SETTINGS_PANEL_WIDTH &&
                mouseY >= y && mouseY <= y + guiHeight) {
                
                // Smooth scroll with spring physics for settings panel
                settingsScrollTarget -= (float) verticalAmount * 30;
                settingsScrollTarget = Math.max(0, Math.min(settingsScrollTarget, getMaxSettingsScroll()));
                
                return true;
            }
        }
        
        // Check if mouse is over main content area
        int contentX = x + SIDEBAR_WIDTH + 20;
        int contentY = y + 15;
        int contentWidth = guiWidth - SIDEBAR_WIDTH - 35;
        int contentHeight = guiHeight - 30;
        
        if (mouseX >= contentX && mouseX <= contentX + contentWidth &&
            mouseY >= contentY && mouseY <= contentY + contentHeight) {
            
            // Smooth scroll with inertia (main content)
            scrollTarget -= (float) verticalAmount * 40;
            scrollTarget = Math.max(0, Math.min(scrollTarget, getMaxScroll()));
            return true;
        }
        
        return false;
    }
    
    // ==================== MOUSE EVENTS ====================
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (int) guiX;
        int y = (int) guiY;
        
        // Search bar click
        int searchX = x + 10 + SIDEBAR_PADDING;
        int searchY = y + 10 + SIDEBAR_PADDING;
        int searchWidth = SIDEBAR_WIDTH - SIDEBAR_PADDING * 2;
        if (mouseX >= searchX && mouseX <= searchX + searchWidth &&
            mouseY >= searchY && mouseY <= searchY + SEARCH_HEIGHT) {
            searchFocused = true;
            return true;
        } else {
            searchFocused = false;
        }
        
        // Close button (X)
        int closeX = x + guiWidth - 25;
        int closeY = y + 8;
        if (mouseX >= closeX && mouseX <= closeX + 15 &&
            mouseY >= closeY && mouseY <= closeY + 15) {
            this.close();
            return true;
        }
        
        // Resize handle (bottom-right corner)
        int resizeSize = 15;
        if (mouseX >= x + guiWidth - resizeSize && mouseX <= x + guiWidth &&
            mouseY >= y + guiHeight - resizeSize && mouseY <= y + guiHeight) {
            resizing = true;
            resizeStartWidth = guiWidth;
            resizeStartHeight = guiHeight;
            resizeStartX = mouseX;
            resizeStartY = mouseY;
            return true;
        }
        
        // Dragging (title bar area)
        if (mouseY >= y && mouseY <= y + 30 && mouseX >= x && mouseX <= x + guiWidth - 30) {
            dragging = true;
            dragOffsetX = (float) (mouseX - guiX);
            dragOffsetY = (float) (mouseY - guiY);
            return true;
        }
        
        // Category clicks
        if (hoveredCategory != null) {
            selectedCategory = hoveredCategory;
            
            // Scroll to selected category
            scrollToCategorysmooth(hoveredCategory);
            return true;
        }
        
        // Module clicks
        if (hoveredModule != null) {
            // Right click - open settings
            if (button == 1 && hoveredModule.hasSettings()) {
                selectedModule = hoveredModule;
                settingsPanelTargetOffset = 1.0f;
                // Reset settings scroll
                settingsScrollOffset = 0;
                settingsScrollTarget = 0;
                settingsScrollVelocity = 0;
                return true;
            }
            // Left click - toggle
            else if (button == 0) {
                hoveredModule.toggle();
                return true;
            }
        }
        
        // Close settings panel (теперь справа от меню)
        if (selectedModule != null && settingsPanelOffset > 0.5f) {
            int panelX = x + guiWidth + 10 + (int)((1.0f - settingsPanelOffset) * -SETTINGS_PANEL_WIDTH);
            int closeBtnX = panelX + SETTINGS_PANEL_WIDTH - 30;
            int closeBtnY = y + 20;
            
            if (mouseX >= closeBtnX && mouseX <= closeBtnX + 15 &&
                mouseY >= closeBtnY && mouseY <= closeBtnY + 15) {
                selectedModule = null;
                settingsPanelTargetOffset = 0;
                return true;
            }
            
            // Handle settings panel interactions
            if (handleSettingsPanelClick(mouseX, mouseY, button)) {
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private boolean handleSettingsPanelClick(double mouseX, double mouseY, int button) {
        if (selectedModule == null) return false;
        
        int x = (int) guiX;
        int panelX = x + guiWidth + 10 + (int)((1.0f - settingsPanelOffset) * -SETTINGS_PANEL_WIDTH);
        int panelY = (int) guiY;
        int headerY = panelY + 20;
        int lineY = headerY + 25;
        int contentY = lineY + 15;
        int contentX = panelX + 15;
        int contentWidth = SETTINGS_PANEL_WIDTH - 30;
        
        // Track Y position for each element (with scroll offset)
        int currentY = contentY - (int)settingsScrollOffset;
        String moduleName = selectedModule.getName();
        
        if (moduleName.equals("Aura")) {
            com.arizon.client.module.modules.combat.Aura aura = 
                (com.arizon.client.module.modules.combat.Aura) selectedModule;
            
            // Range slider
            currentY += 15; // Label
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "aura_range")) {
                return true;
            }
            currentY += 30; // Slider + spacing
            
            // PVP Mode dropdown
            currentY += 15; // Label
            if (handleDropdownClick(mouseX, mouseY, contentX, currentY, contentWidth, "aura_pvp", 
                new String[]{"Single", "Multi"}, aura.pvpMode, (val) -> aura.pvpMode = val)) {
                return true;
            }
            currentY += (openDropdown != null && openDropdown.equals("aura_pvp")) ? 60 : 38;
            
            // Target Mode dropdown
            currentY += 15;
            if (handleDropdownClick(mouseX, mouseY, contentX, currentY, contentWidth, "aura_target",
                new String[]{"Closest", "Health", "Distance"}, aura.targetMode, (val) -> aura.targetMode = val)) {
                return true;
            }
            currentY += (openDropdown != null && openDropdown.equals("aura_target")) ? 88 : 38;
            
            // Attack Mode dropdown
            currentY += 15;
            if (handleDropdownClick(mouseX, mouseY, contentX, currentY, contentWidth, "aura_attack",
                new String[]{"Single", "Multi", "HolyWorld"}, aura.attackMode, (val) -> aura.attackMode = val)) {
                return true;
            }
            currentY += (openDropdown != null && openDropdown.equals("aura_attack")) ? 88 : 38;
            
            // Checkboxes
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "aura_players", (val) -> aura.attackPlayers = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "aura_mobs", (val) -> aura.attackMobs = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "aura_animals", (val) -> aura.attackAnimals = val)) return true;
            currentY += 25;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "aura_targethud", (val) -> aura.showTargetHUD = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "aura_crit", (val) -> aura.critOnly = val)) return true;
        } else if (moduleName.equals("Triggerbot")) {
            com.arizon.client.module.modules.combat.Triggerbot trigger = 
                (com.arizon.client.module.modules.combat.Triggerbot) selectedModule;
            
            // Mode dropdown
            currentY += 15;
            if (handleDropdownClick(mouseX, mouseY, contentX, currentY, contentWidth, "trigger_mode",
                new String[]{"Always", "LookingAt"}, trigger.mode, (val) -> trigger.mode = val)) {
                return true;
            }
            currentY += (openDropdown != null && openDropdown.equals("trigger_mode")) ? 60 : 38;
            
            // Delay slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "trigger_delay")) {
                return true;
            }
        } else if (moduleName.equals("Speed")) {
            com.arizon.client.module.modules.movement.Speed speed = 
                (com.arizon.client.module.modules.movement.Speed) selectedModule;
            
            // Speed slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "speed_value")) {
                return true;
            }
        } else if (moduleName.equals("Removals")) {
            com.arizon.client.module.modules.render.Removals removals = 
                (com.arizon.client.module.modules.render.Removals) selectedModule;
            
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_hurt", (val) -> removals.removeHurtCam = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_fire", (val) -> removals.removeFireOverlay = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_water", (val) -> removals.removeWaterOverlay = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_pumpkin", (val) -> removals.removePumpkinOverlay = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_boss", (val) -> removals.removeBossBar = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_score", (val) -> removals.removeScoreboard = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_fog", (val) -> removals.removeFog = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "rem_weather", (val) -> removals.removeWeather = val)) return true;
        } else if (moduleName.equals("SwingAnimations")) {
            com.arizon.client.module.modules.render.SwingAnimations swing = 
                (com.arizon.client.module.modules.render.SwingAnimations) selectedModule;
            
            // Mode dropdown
            currentY += 15;
            if (handleDropdownClick(mouseX, mouseY, contentX, currentY, contentWidth, "swing_mode",
                new String[]{"1.7", "Smooth", "Spin", "Push"}, swing.mode, (val) -> swing.mode = val)) {
                return true;
            }
            currentY += (openDropdown != null && openDropdown.equals("swing_mode")) ? 110 : 38;
            
            // Speed slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "swing_speed")) {
                return true;
            }
        } else if (moduleName.equals("ElytraTarget")) {
            com.arizon.client.module.modules.movement.ElytraTarget elytra = 
                (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
            
            // Auto Firework checkbox
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "elytra_firework", (val) -> elytra.autoFirework = val)) return true;
            currentY += 20;
            
            // Keep Flying checkbox
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "elytra_keepflying", (val) -> elytra.keepFlying = val)) return true;
            currentY += 20;
            
            // Auto Activate Elytra checkbox
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "elytra_autoactivate", (val) -> elytra.autoActivateElytra = val)) return true;
            currentY += 25;
            
            // Speed slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "elytra_speed")) {
                return true;
            }
            currentY += 40;
            
            // Attack Distance slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "elytra_attackdist")) {
                return true;
            }
            currentY += 40;
            
            // Search Range slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "elytra_searchrange")) {
                return true;
            }
            currentY += 40;
            
            // Firework Delay slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "elytra_delay")) {
                return true;
            }
        } else if (moduleName.equals("ESP")) {
            com.arizon.client.module.modules.render.ESP esp = 
                (com.arizon.client.module.modules.render.ESP) selectedModule;
            
            // Mode dropdown
            currentY += 15;
            if (handleDropdownClick(mouseX, mouseY, contentX, currentY, contentWidth, "esp_mode",
                new String[]{"Box", "Outline", "Corner Box"}, esp.mode, (val) -> esp.mode = val)) {
                return true;
            }
            currentY += (openDropdown != null && openDropdown.equals("esp_mode")) ? 88 : 38;
            
            // Health Bar checkbox
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_healthbar", (val) -> esp.showHealthBar = val)) return true;
            currentY += 20;
            
            // Inventory checkbox
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_inventory", (val) -> esp.showInventory = val)) return true;
            currentY += 20;
            
            // Danger checkbox
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_danger", (val) -> esp.danger = val)) return true;
            currentY += 25;
            
            // Filters
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_players", (val) -> esp.showPlayers = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_mobs", (val) -> esp.showMobs = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_invisible", (val) -> esp.showInvisible = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "esp_naked", (val) -> esp.showNaked = val)) return true;
            currentY += 25;
            
            // Range slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "esp_range")) {
                return true;
            }
        } else if (moduleName.equals("Chams")) {
            com.arizon.client.module.modules.render.Chams chams = 
                (com.arizon.client.module.modules.render.Chams) selectedModule;
            
            // Filters
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "chams_players", (val) -> chams.showPlayers = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "chams_mobs", (val) -> chams.showMobs = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "chams_invisible", (val) -> chams.showInvisible = val)) return true;
            currentY += 20;
            if (handleCheckboxClick(mouseX, mouseY, contentX, currentY, "chams_naked", (val) -> chams.showNaked = val)) return true;
            currentY += 25;
            
            // Range slider
            currentY += 15;
            if (handleSliderClick(mouseX, mouseY, contentX, currentY, contentWidth, "chams_range")) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean handleSliderClick(double mouseX, double mouseY, int x, int y, int width, String id) {
        int sliderHeight = 20;
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + sliderHeight) {
            draggingSlider = true;
            draggingSliderName = id;
            return true;
        }
        return false;
    }
    
    private boolean handleDropdownClick(double mouseX, double mouseY, int x, int y, int width, 
                                       String id, String[] options, String currentValue, 
                                       java.util.function.Consumer<String> setter) {
        int dropdownHeight = 28;
        boolean isOpen = id.equals(openDropdown);
        
        // Main dropdown click
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + dropdownHeight) {
            openDropdown = isOpen ? null : id;
            return true;
        }
        
        // Options click (if open)
        if (isOpen) {
            int optionY = y + dropdownHeight + 2;
            for (String option : options) {
                if (!option.equals(currentValue)) {
                    int optionHeight = 24;
                    if (mouseX >= x && mouseX <= x + width && mouseY >= optionY && mouseY <= optionY + optionHeight) {
                        setter.accept(option);
                        openDropdown = null;
                        return true;
                    }
                    optionY += optionHeight + 2;
                }
            }
        }
        
        return false;
    }
    
    private boolean handleCheckboxClick(double mouseX, double mouseY, int x, int y, String id, 
                                       java.util.function.Consumer<Boolean> setter) {
        int checkboxSize = 16;
        int height = 20;
        
        if (mouseX >= x && mouseX <= x + checkboxSize && mouseY >= y + 2 && mouseY <= y + 2 + checkboxSize) {
            // Toggle value
            if (selectedModule != null) {
                String moduleName = selectedModule.getName();
                if (moduleName.equals("Aura")) {
                    com.arizon.client.module.modules.combat.Aura aura = 
                        (com.arizon.client.module.modules.combat.Aura) selectedModule;
                    
                    if (id.equals("aura_players")) setter.accept(!aura.attackPlayers);
                    else if (id.equals("aura_mobs")) setter.accept(!aura.attackMobs);
                    else if (id.equals("aura_animals")) setter.accept(!aura.attackAnimals);
                    else if (id.equals("aura_targethud")) setter.accept(!aura.showTargetHUD);
                    else if (id.equals("aura_crit")) setter.accept(!aura.critOnly);
                } else if (moduleName.equals("Removals")) {
                    com.arizon.client.module.modules.render.Removals removals = 
                        (com.arizon.client.module.modules.render.Removals) selectedModule;
                    
                    if (id.equals("rem_hurt")) setter.accept(!removals.removeHurtCam);
                    else if (id.equals("rem_fire")) setter.accept(!removals.removeFireOverlay);
                    else if (id.equals("rem_water")) setter.accept(!removals.removeWaterOverlay);
                    else if (id.equals("rem_pumpkin")) setter.accept(!removals.removePumpkinOverlay);
                    else if (id.equals("rem_boss")) setter.accept(!removals.removeBossBar);
                    else if (id.equals("rem_score")) setter.accept(!removals.removeScoreboard);
                    else if (id.equals("rem_fog")) setter.accept(!removals.removeFog);
                    else if (id.equals("rem_weather")) setter.accept(!removals.removeWeather);
                } else if (moduleName.equals("ElytraTarget")) {
                    com.arizon.client.module.modules.movement.ElytraTarget elytra = 
                        (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
                    
                    if (id.equals("elytra_firework")) setter.accept(!elytra.autoFirework);
                    else if (id.equals("elytra_keepflying")) setter.accept(!elytra.keepFlying);
                    else if (id.equals("elytra_autoactivate")) setter.accept(!elytra.autoActivateElytra);
                } else if (moduleName.equals("ESP")) {
                    com.arizon.client.module.modules.render.ESP esp = 
                        (com.arizon.client.module.modules.render.ESP) selectedModule;
                    
                    if (id.equals("esp_healthbar")) setter.accept(!esp.showHealthBar);
                    else if (id.equals("esp_inventory")) setter.accept(!esp.showInventory);
                    else if (id.equals("esp_danger")) setter.accept(!esp.danger);
                    else if (id.equals("esp_players")) setter.accept(!esp.showPlayers);
                    else if (id.equals("esp_mobs")) setter.accept(!esp.showMobs);
                    else if (id.equals("esp_invisible")) setter.accept(!esp.showInvisible);
                    else if (id.equals("esp_naked")) setter.accept(!esp.showNaked);
                } else if (moduleName.equals("Chams")) {
                    com.arizon.client.module.modules.render.Chams chams = 
                        (com.arizon.client.module.modules.render.Chams) selectedModule;
                    
                    if (id.equals("chams_players")) setter.accept(!chams.showPlayers);
                    else if (id.equals("chams_mobs")) setter.accept(!chams.showMobs);
                    else if (id.equals("chams_invisible")) setter.accept(!chams.showInvisible);
                    else if (id.equals("chams_naked")) setter.accept(!chams.showNaked);
                }
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (resizing) {
            int deltaWidth = (int) (mouseX - resizeStartX);
            int deltaHeight = (int) (mouseY - resizeStartY);
            
            guiWidth = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, resizeStartWidth + deltaWidth));
            guiHeight = Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, resizeStartHeight + deltaHeight));
            return true;
        }
        
        if (dragging) {
            guiX = (float) (mouseX - dragOffsetX);
            guiY = (float) (mouseY - dragOffsetY);
            return true;
        }
        
        // Handle slider dragging
        if (draggingSlider) {
            handleSliderDrag(mouseX, mouseY);
            return true;
        }
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    private void handleSliderDrag(double mouseX, double mouseY) {
        if (!draggingSlider || selectedModule == null) return;
        
        int x = (int) guiX;
        int panelX = x + guiWidth + 10 + (int)((1.0f - settingsPanelOffset) * -SETTINGS_PANEL_WIDTH);
        int contentX = panelX + 15;
        int contentWidth = SETTINGS_PANEL_WIDTH - 30;
        
        // Calculate percentage
        float percent = (float)(mouseX - contentX) / contentWidth;
        percent = Math.max(0, Math.min(1, percent));
        
        String moduleName = selectedModule.getName();
        
        if (draggingSliderName.equals("aura_range")) {
            com.arizon.client.module.modules.combat.Aura aura = 
                (com.arizon.client.module.modules.combat.Aura) selectedModule;
            aura.range = 3.0f + percent * (6.0f - 3.0f);
        } else if (draggingSliderName.equals("trigger_delay")) {
            com.arizon.client.module.modules.combat.Triggerbot trigger = 
                (com.arizon.client.module.modules.combat.Triggerbot) selectedModule;
            trigger.delay = (int)(percent * 1000);
        } else if (draggingSliderName.equals("speed_value")) {
            com.arizon.client.module.modules.movement.Speed speed = 
                (com.arizon.client.module.modules.movement.Speed) selectedModule;
            speed.speed = 0.1f + percent * (3.0f - 0.1f);
        } else if (draggingSliderName.equals("swing_speed")) {
            com.arizon.client.module.modules.render.SwingAnimations swing = 
                (com.arizon.client.module.modules.render.SwingAnimations) selectedModule;
            swing.speed = 0.5f + percent * (2.0f - 0.5f);
        } else if (draggingSliderName.equals("elytra_speed")) {
            com.arizon.client.module.modules.movement.ElytraTarget elytra = 
                (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
            elytra.speed = 0.5f + percent * (3.0f - 0.5f);
        } else if (draggingSliderName.equals("elytra_attackdist")) {
            com.arizon.client.module.modules.movement.ElytraTarget elytra = 
                (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
            elytra.attackDistance = 1.0f + percent * (5.0f - 1.0f);
        } else if (draggingSliderName.equals("elytra_searchrange")) {
            com.arizon.client.module.modules.movement.ElytraTarget elytra = 
                (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
            elytra.searchRange = 50.0f + percent * (100.0f - 50.0f);
        } else if (draggingSliderName.equals("elytra_delay")) {
            com.arizon.client.module.modules.movement.ElytraTarget elytra = 
                (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
            elytra.fireworkDelay = 5 + (int)(percent * (60 - 5));
        } else if (draggingSliderName.equals("esp_range")) {
            com.arizon.client.module.modules.render.ESP esp = 
                (com.arizon.client.module.modules.render.ESP) selectedModule;
            esp.range = 10 + (int)(percent * (100 - 10));
        } else if (draggingSliderName.equals("chams_range")) {
            com.arizon.client.module.modules.render.Chams chams = 
                (com.arizon.client.module.modules.render.Chams) selectedModule;
            chams.range = 10 + (int)(percent * (100 - 10));
        }
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        resizing = false;
        draggingSlider = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (searchFocused) {
                searchFocused = false;
                return true;
            }
            this.close();
            return true;
        }
        
        // Handle search input
        if (searchFocused) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused) {
            searchQuery += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
    
    // ==================== UTILITY METHODS ====================
    
    private void fillRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, Color color) {
        int rgb = color.getRGB();
        
        // Main body
        context.fill(x + radius, y, x + width - radius, y + height, rgb);
        context.fill(x, y + radius, x + width, y + height - radius, rgb);
        
        // Corners (approximation with small fills)
        for (int i = 0; i < radius; i++) {
            int offset = (int) Math.sqrt(radius * radius - i * i);
            
            // Top-left
            context.fill(x + radius - offset, y + i, x + radius, y + i + 1, rgb);
            // Top-right
            context.fill(x + width - radius, y + i, x + width - radius + offset, y + i + 1, rgb);
            // Bottom-left
            context.fill(x + radius - offset, y + height - i - 1, x + radius, y + height - i, rgb);
            // Bottom-right
            context.fill(x + width - radius, y + height - i - 1, x + width - radius + offset, y + height - i, rgb);
        }
    }
    
    private void fillRoundedRectGradient(DrawContext context, int x, int y, int width, int height, 
                                        int radius, Color color1, Color color2) {
        // Top half
        fillRoundedRect(context, x, y, width, height / 2 + radius, radius, color1);
        // Bottom half
        fillRoundedRect(context, x, y + height / 2 - radius, width, height / 2 + radius, radius, color2);
    }
    
    private void fillCircle(DrawContext context, int centerX, int centerY, int radius, Color color) {
        int rgb = color.getRGB();
        
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    context.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, rgb);
                }
            }
        }
    }
    
    private void drawRoundedBorder(DrawContext context, int x, int y, int width, int height, 
                                  int radius, Color color, int thickness) {
        for (int i = 0; i < thickness; i++) {
            fillRoundedRect(context, x + i, y + i, width - i * 2, height - i * 2, radius, 
                new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / thickness));
        }
    }
    
    private void renderInnerShadow(DrawContext context, int x, int y, int width, int height, int radius) {
        int shadowSize = 8;
        for (int i = 0; i < shadowSize; i++) {
            int alpha = (shadowSize - i) * 3;
            Color shadowColor = new Color(0, 0, 0, alpha);
            
            // Top shadow
            context.fill(x + radius, y + i, x + width - radius, y + i + 1, shadowColor.getRGB());
            // Left shadow
            context.fill(x + i, y + radius, x + i + 1, y + height - radius, shadowColor.getRGB());
        }
    }
    
    private void enableScissor(int x, int y, int width, int height) {
        if (client == null || client.getWindow() == null) return;
        
        double scale = client.getWindow().getScaleFactor();
        int windowHeight = client.getWindow().getHeight();
        
        // Convert to OpenGL coordinates (bottom-left origin)
        int scaledX = (int)(x * scale);
        int scaledY = (int)(windowHeight - (y + height) * scale);
        int scaledWidth = (int)(width * scale);
        int scaledHeight = (int)(height * scale);
        
        // Enable scissor test
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor(scaledX, scaledY, scaledWidth, scaledHeight);
    }
    
    private void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }
    
    private List<Module> getModulesForCategory(Category category) {
        List<Module> result = new ArrayList<>();
        
        for (Module module : ModuleManager.getInstance().getModules()) {
            String moduleName = module.getName();
            
            // Search filter
            if (!searchQuery.isEmpty() && 
                !moduleName.toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }
            
            boolean matches = false;
            switch (category.name) {
                case "Combat":
                    matches = moduleName.equals("Aura") || moduleName.equals("Triggerbot");
                    break;
                case "Movement":
                    matches = moduleName.equals("Sprint") || moduleName.equals("Speed") || 
                             moduleName.equals("Fly") || moduleName.equals("NoFall") || 
                             moduleName.equals("GuiMove") || moduleName.equals("ElytraTarget");
                    break;
                case "Visuals":
                    matches = moduleName.equals("ESP") || moduleName.equals("BlockESP") || 
                             moduleName.equals("Chams") || moduleName.equals("JumpCircles") || 
                             moduleName.equals("ChinaHat") || moduleName.equals("Tracers") || 
                             moduleName.equals("Removals") || moduleName.equals("SwingAnimations");
                    break;
                case "Player":
                    matches = moduleName.equals("AutoArmor") || moduleName.equals("AutoTool");
                    break;
                case "Other":
                    matches = moduleName.equals("Scaffold") || moduleName.equals("Nuker");
                    break;
            }
            
            if (matches) {
                result.add(module);
            }
        }
        
        return result;
    }
    
    private String getModuleDescription(Module module) {
        return module.getDescription();
    }
    
    public boolean shouldPause() {
        return false;
    }
    
    // ==================== SETTINGS PANEL ANIMATION ====================
    private void updateSettingsPanelAnimation() {
        float diff = settingsPanelTargetOffset - settingsPanelOffset;
        settingsPanelOffset += diff * 0.2f;
        
        if (Math.abs(diff) < 0.01f) {
            settingsPanelOffset = settingsPanelTargetOffset;
        }
    }
    
    // ==================== RENDER SETTINGS PANEL ====================
    private void renderSettingsPanel(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (selectedModule == null) return;
        
        // Панель справа от меню (не внутри)
        int panelX = x + guiWidth + 10 + (int)((1.0f - settingsPanelOffset) * -SETTINGS_PANEL_WIDTH);
        int panelY = y;
        int panelHeight = guiHeight;
        
        // Panel background с тенью
        // Тень
        for (int i = 0; i < 3; i++) {
            int offset = (3 - i) * 2;
            int alpha = 10 - (i * 3);
            Color shadowColor = new Color(0, 0, 0, alpha);
            fillRoundedRect(context, panelX - offset, panelY, SETTINGS_PANEL_WIDTH, panelHeight, 
                RADIUS_LARGE, shadowColor);
        }
        
        // Основной фон
        fillRoundedRect(context, panelX, panelY, SETTINGS_PANEL_WIDTH, panelHeight, 
            RADIUS_LARGE, BG_MAIN);
        
        // Header
        int headerY = panelY + 20;
        
        // Close button
        int closeX = panelX + SETTINGS_PANEL_WIDTH - 30;
        int closeY = headerY;
        boolean closeHovered = mouseX >= closeX && mouseX <= closeX + 15 &&
                              mouseY >= closeY && mouseY <= closeY + 15;
        
        context.drawText(textRenderer, "✕", closeX, closeY, 
            closeHovered ? TEXT_PRIMARY.getRGB() : TEXT_SECONDARY.getRGB(), false);
        
        // Module name
        context.drawText(textRenderer, selectedModule.getName(), panelX + 15, headerY, 
            TEXT_PRIMARY.getRGB(), true);
        
        // Separator line
        int lineY = headerY + 25;
        fillRoundedRect(context, panelX + 15, lineY, SETTINGS_PANEL_WIDTH - 30, 1, 0, 
            new Color(TEXT_TERTIARY.getRed(), TEXT_TERTIARY.getGreen(), TEXT_TERTIARY.getBlue(), 30));
        
        // Settings content with scroll
        int contentY = lineY + 15;
        int contentHeight = panelHeight - (contentY - panelY) - 15;
        
        // Enable scissor for clipping
        enableScissor(panelX + 15, contentY, SETTINGS_PANEL_WIDTH - 30, contentHeight);
        
        // Render with scroll offset
        int scrolledY = contentY - (int)settingsScrollOffset;
        renderModuleSettings(context, panelX + 15, scrolledY, SETTINGS_PANEL_WIDTH - 30, mouseX, mouseY);
        
        disableScissor();
        
        // Scrollbar (if needed)
        float maxScroll = getMaxSettingsScroll();
        if (maxScroll > 0) {
            renderSettingsScrollbar(context, panelX + SETTINGS_PANEL_WIDTH - 10, contentY, 4, contentHeight, maxScroll);
        }
    }
    
    // ==================== RENDER SETTINGS SCROLLBAR ====================
    private void renderSettingsScrollbar(DrawContext context, int x, int y, int width, int height, float maxScroll) {
        // Track (very subtle)
        fillRoundedRect(context, x, y, width, height, width / 2, 
            new Color(20, 20, 26, 80));
        
        // Thumb (purple, rounded)
        float thumbHeight = Math.max(40, height * (height / (height + maxScroll)));
        float thumbY = y + (settingsScrollOffset / maxScroll) * (height - thumbHeight);
        
        fillRoundedRect(context, x, (int) thumbY, width, (int) thumbHeight, width / 2, 
            new Color(ACCENT_PRIMARY.getRed(), ACCENT_PRIMARY.getGreen(), ACCENT_PRIMARY.getBlue(), 180));
    }
    
    // ==================== RENDER MODULE SETTINGS ====================
    private void renderModuleSettings(DrawContext context, int x, int y, int width, int mouseX, int mouseY) {
        String moduleName = selectedModule.getName();
        
        if (moduleName.equals("Aura")) {
            renderAuraSettings(context, x, y, width);
        } else if (moduleName.equals("Triggerbot")) {
            renderTriggerbotSettings(context, x, y, width);
        } else if (moduleName.equals("Speed")) {
            renderSpeedSettings(context, x, y, width);
        } else if (moduleName.equals("Removals")) {
            renderRemovalsSettings(context, x, y, width);
        } else if (moduleName.equals("SwingAnimations")) {
            renderSwingAnimationsSettings(context, x, y, width);
        } else if (moduleName.equals("ElytraTarget")) {
            renderElytraTargetSettings(context, x, y, width);
        } else if (moduleName.equals("ESP")) {
            renderESPSettings(context, x, y, width);
        } else if (moduleName.equals("Chams")) {
            renderChamsSettings(context, x, y, width);
        } else {
            context.drawText(textRenderer, "Нет настроек", x, y, TEXT_TERTIARY.getRGB(), false);
        }
    }
    
    private void renderAuraSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.combat.Aura aura = 
            (com.arizon.client.module.modules.combat.Aura) selectedModule;
        
        // Range slider
        context.drawText(textRenderer, "Range", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.format("%.1f", aura.range), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "aura_range", aura.range, 3.0f, 6.0f) + 10;
        
        // PVP Mode dropdown
        context.drawText(textRenderer, "PVP Mode", x, y, TEXT_SECONDARY.getRGB(), false);
        y += 15;
        y = renderDropdown(context, x, y, width, "aura_pvp", aura.pvpMode, 
            new String[]{"Single", "Multi"}) + 10;
        
        // Target Mode dropdown
        context.drawText(textRenderer, "Target Mode", x, y, TEXT_SECONDARY.getRGB(), false);
        y += 15;
        y = renderDropdown(context, x, y, width, "aura_target", aura.targetMode, 
            new String[]{"Closest", "Health", "Distance"}) + 10;
        
        // Attack Mode dropdown
        context.drawText(textRenderer, "Attack Mode", x, y, TEXT_SECONDARY.getRGB(), false);
        y += 15;
        y = renderDropdown(context, x, y, width, "aura_attack", aura.attackMode, 
            new String[]{"Single", "Multi", "HolyWorld"}) + 10;
        
        // Checkboxes
        y = renderCheckbox(context, x, y, width, "aura_players", "Attack Players", aura.attackPlayers);
        y = renderCheckbox(context, x, y, width, "aura_mobs", "Attack Mobs", aura.attackMobs);
        y = renderCheckbox(context, x, y, width, "aura_animals", "Attack Animals", aura.attackAnimals);
        y += 5;
        y = renderCheckbox(context, x, y, width, "aura_targethud", "Show Target HUD", aura.showTargetHUD);
        y = renderCheckbox(context, x, y, width, "aura_crit", "Crit Only", aura.critOnly);
    }
    
    private void renderTriggerbotSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.combat.Triggerbot trigger = 
            (com.arizon.client.module.modules.combat.Triggerbot) selectedModule;
        
        // Mode dropdown
        context.drawText(textRenderer, "Mode", x, y, TEXT_SECONDARY.getRGB(), false);
        y += 15;
        y = renderDropdown(context, x, y, width, "trigger_mode", trigger.mode, 
            new String[]{"Always", "LookingAt"}) + 10;
        
        // Delay slider
        context.drawText(textRenderer, "Delay (ms)", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.valueOf(trigger.delay), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "trigger_delay", trigger.delay, 0, 1000);
    }
    
    private void renderSpeedSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.movement.Speed speed = 
            (com.arizon.client.module.modules.movement.Speed) selectedModule;
        
        // Speed slider
        context.drawText(textRenderer, "Speed", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.format("%.2f", speed.speed), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        renderSlider(context, x, y, width, "speed_value", speed.speed, 0.1f, 3.0f);
    }
    
    private void renderRemovalsSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.render.Removals removals = 
            (com.arizon.client.module.modules.render.Removals) selectedModule;
        
        y = renderCheckbox(context, x, y, width, "rem_hurt", "Remove Hurt Cam", removals.removeHurtCam);
        y = renderCheckbox(context, x, y, width, "rem_fire", "Remove Fire Overlay", removals.removeFireOverlay);
        y = renderCheckbox(context, x, y, width, "rem_water", "Remove Water Overlay", removals.removeWaterOverlay);
        y = renderCheckbox(context, x, y, width, "rem_pumpkin", "Remove Pumpkin Overlay", removals.removePumpkinOverlay);
        y = renderCheckbox(context, x, y, width, "rem_boss", "Remove Boss Bar", removals.removeBossBar);
        y = renderCheckbox(context, x, y, width, "rem_score", "Remove Scoreboard", removals.removeScoreboard);
        y = renderCheckbox(context, x, y, width, "rem_fog", "Remove Fog", removals.removeFog);
        y = renderCheckbox(context, x, y, width, "rem_weather", "Remove Weather", removals.removeWeather);
    }
    
    private void renderSwingAnimationsSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.render.SwingAnimations swing = 
            (com.arizon.client.module.modules.render.SwingAnimations) selectedModule;
        
        // Mode dropdown
        context.drawText(textRenderer, "Animation Mode", x, y, TEXT_SECONDARY.getRGB(), false);
        y += 15;
        y = renderDropdown(context, x, y, width, "swing_mode", swing.mode, 
            new String[]{"1.7", "Smooth", "Spin", "Push"}) + 10;
        
        // Speed slider
        context.drawText(textRenderer, "Speed", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.format("%.2f", swing.speed), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        renderSlider(context, x, y, width, "swing_speed", swing.speed, 0.5f, 2.0f);
    }
    
    private void renderElytraTargetSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.movement.ElytraTarget elytra = 
            (com.arizon.client.module.modules.movement.ElytraTarget) selectedModule;
        
        // Auto Firework checkbox
        y = renderCheckbox(context, x, y, width, "elytra_firework", "Auto Firework", elytra.autoFirework);
        
        // Keep Flying checkbox
        y = renderCheckbox(context, x, y, width, "elytra_keepflying", "Keep Flying", elytra.keepFlying);
        
        // Auto Activate Elytra checkbox
        y = renderCheckbox(context, x, y, width, "elytra_autoactivate", "Auto Activate Elytra", elytra.autoActivateElytra);
        y += 5;
        
        // Speed slider
        context.drawText(textRenderer, "Speed", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.format("%.2f", elytra.speed), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "elytra_speed", elytra.speed, 0.5f, 3.0f) + 10;
        
        // Attack Distance slider
        context.drawText(textRenderer, "Attack Distance", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.format("%.1f", elytra.attackDistance), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "elytra_attackdist", elytra.attackDistance, 1.0f, 5.0f) + 10;
        
        // Search Range slider
        context.drawText(textRenderer, "Search Range", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.format("%.0f", elytra.searchRange), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "elytra_searchrange", elytra.searchRange, 50.0f, 100.0f) + 10;
        
        // Firework Delay slider
        context.drawText(textRenderer, "Firework Delay", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.valueOf(elytra.fireworkDelay), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "elytra_delay", elytra.fireworkDelay, 5, 60);
    }
    
    private void renderESPSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.render.ESP esp = 
            (com.arizon.client.module.modules.render.ESP) selectedModule;
        
        // Mode dropdown
        context.drawText(textRenderer, "Mode", x, y, TEXT_SECONDARY.getRGB(), false);
        y += 15;
        y = renderDropdown(context, x, y, width, "esp_mode", esp.mode, 
            new String[]{"Box", "Outline", "Corner Box"}) + 10;
        
        // Health Bar checkbox
        y = renderCheckbox(context, x, y, width, "esp_healthbar", "Health Bar", esp.showHealthBar);
        
        // Inventory checkbox
        y = renderCheckbox(context, x, y, width, "esp_inventory", "Inventory", esp.showInventory);
        
        // Danger checkbox
        y = renderCheckbox(context, x, y, width, "esp_danger", "Danger", esp.danger);
        y += 5;
        
        // Filters
        y = renderCheckbox(context, x, y, width, "esp_players", "Players", esp.showPlayers);
        y = renderCheckbox(context, x, y, width, "esp_mobs", "Mobs", esp.showMobs);
        y = renderCheckbox(context, x, y, width, "esp_invisible", "Invisible", esp.showInvisible);
        y = renderCheckbox(context, x, y, width, "esp_naked", "Naked", esp.showNaked);
        y += 5;
        
        // Range slider
        context.drawText(textRenderer, "Range", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.valueOf(esp.range), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "esp_range", esp.range, 10, 100);
    }
    
    private void renderChamsSettings(DrawContext context, int x, int y, int width) {
        com.arizon.client.module.modules.render.Chams chams = 
            (com.arizon.client.module.modules.render.Chams) selectedModule;
        
        // Filters
        y = renderCheckbox(context, x, y, width, "chams_players", "Players", chams.showPlayers);
        y = renderCheckbox(context, x, y, width, "chams_mobs", "Mobs", chams.showMobs);
        y = renderCheckbox(context, x, y, width, "chams_invisible", "Invisible", chams.showInvisible);
        y = renderCheckbox(context, x, y, width, "chams_naked", "Naked", chams.showNaked);
        y += 5;
        
        // Range slider
        context.drawText(textRenderer, "Range", x, y, TEXT_SECONDARY.getRGB(), false);
        context.drawText(textRenderer, String.valueOf(chams.range), 
            x + width - 30, y, TEXT_PRIMARY.getRGB(), false);
        y += 15;
        y = renderSlider(context, x, y, width, "chams_range", chams.range, 10, 100);
    }
    
    // ==================== BLUR BACKGROUND ====================
    private void renderBlurredBackground(DrawContext context) {
        // Simple darkening overlay (blur shader would be complex)
        context.fill(0, 0, width, height, new Color(0, 0, 0, 120).getRGB());
    }
    
    // ==================== RENDER INTERACTIVE ELEMENTS ====================
    
    private int renderSlider(DrawContext context, int x, int y, int width, String id, float value, float min, float max) {
        int sliderHeight = 20;
        int trackHeight = 4;
        
        // Track background
        int trackY = y + (sliderHeight - trackHeight) / 2;
        fillRoundedRect(context, x, trackY, width, trackHeight, trackHeight / 2, 
            new Color(40, 40, 50, 255));
        
        // Fill (purple)
        float percent = (value - min) / (max - min);
        int fillWidth = (int)(width * percent);
        fillRoundedRect(context, x, trackY, fillWidth, trackHeight, trackHeight / 2, ACCENT_PRIMARY);
        
        // Thumb (circle)
        int thumbX = x + fillWidth;
        int thumbY = y + sliderHeight / 2;
        int thumbRadius = 8;
        
        fillCircle(context, thumbX, thumbY, thumbRadius, 
            new Color(255, 255, 255, 255));
        fillCircle(context, thumbX, thumbY, thumbRadius - 2, ACCENT_PRIMARY);
        
        return y + sliderHeight;
    }
    
    private int renderDropdown(DrawContext context, int x, int y, int width, String id, String value, String[] options) {
        int dropdownHeight = 28;
        boolean isOpen = id.equals(openDropdown);
        
        // Background
        fillRoundedRect(context, x, y, width, dropdownHeight, RADIUS_SMALL, 
            new Color(30, 30, 38, 255));
        
        // Value text
        context.drawText(textRenderer, value, x + 10, y + 10, TEXT_PRIMARY.getRGB(), false);
        
        // Arrow
        context.drawText(textRenderer, isOpen ? "▲" : "▼", x + width - 20, y + 10, 
            TEXT_SECONDARY.getRGB(), false);
        
        int totalHeight = dropdownHeight;
        
        // Options (if open)
        if (isOpen) {
            int optionY = y + dropdownHeight + 2;
            for (String option : options) {
                if (!option.equals(value)) {
                    int optionHeight = 24;
                    
                    // Option background
                    fillRoundedRect(context, x, optionY, width, optionHeight, RADIUS_SMALL, 
                        new Color(25, 25, 33, 255));
                    
                    // Option text
                    context.drawText(textRenderer, option, x + 10, optionY + 8, 
                        TEXT_SECONDARY.getRGB(), false);
                    
                    optionY += optionHeight + 2;
                    totalHeight += optionHeight + 2;
                }
            }
        }
        
        return y + totalHeight;
    }
    
    private int renderCheckbox(DrawContext context, int x, int y, int width, String id, String label, boolean checked) {
        int checkboxSize = 16;
        int height = 20;
        
        // Checkbox background
        fillRoundedRect(context, x, y + 2, checkboxSize, checkboxSize, 4, 
            checked ? ACCENT_PRIMARY : new Color(40, 40, 50, 255));
        
        // Checkmark
        if (checked) {
            context.drawText(textRenderer, "✓", x + 3, y + 4, TEXT_PRIMARY.getRGB(), false);
        }
        
        // Label
        context.drawText(textRenderer, label, x + checkboxSize + 8, y + 4, 
            TEXT_SECONDARY.getRGB(), false);
        
        return y + height;
    }
    
    // ==================== MOUSE SCROLL ====================
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // Определяем где находится мышь
        int x = (int) guiX;
        int y = (int) guiY;
        
        // Settings panel scroll
        if (settingsPanelOffset > 0.5f) {
            int settingsPanelX = x + guiWidth + 10;
            int settingsPanelY = y;
            int settingsPanelWidth = SETTINGS_PANEL_WIDTH;
            int settingsPanelHeight = guiHeight;
            
            if (mouseX >= settingsPanelX && mouseX <= settingsPanelX + settingsPanelWidth &&
                mouseY >= settingsPanelY && mouseY <= settingsPanelY + settingsPanelHeight) {
                
                // Scroll settings panel
                float scrollAmount = (float) amount * 30.0f;
                settingsScrollTarget -= scrollAmount;
                
                // Clamp
                float maxScroll = getMaxSettingsScroll();
                if (settingsScrollTarget < 0) settingsScrollTarget = 0;
                if (settingsScrollTarget > maxScroll) settingsScrollTarget = maxScroll;
                
                return true;
            }
        }
        
        // Main content scroll
        int contentX = x + SIDEBAR_WIDTH + 20;
        int contentY = y + 15;
        int contentWidth = guiWidth - SIDEBAR_WIDTH - 35;
        int contentHeight = guiHeight - 30;
        
        if (mouseX >= contentX && mouseX <= contentX + contentWidth &&
            mouseY >= contentY && mouseY <= contentY + contentHeight) {
            
            // Scroll main content
            float scrollAmount = (float) amount * 40.0f; // Увеличена скорость
            scrollTarget -= scrollAmount;
            
            // Clamp
            float maxScroll = getMaxScroll();
            if (scrollTarget < 0) scrollTarget = 0;
            if (scrollTarget > maxScroll) scrollTarget = maxScroll;
            
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    // ==================== CATEGORY CLASS ====================
    private static class Category {
        String name;
        String icon;
        String description;
        
        Category(String name, String icon, String description) {
            this.name = name;
            this.icon = icon;
            this.description = description;
        }
    }
}

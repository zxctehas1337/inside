package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;

/**
 * ESP - Shows players through walls with multiple modes
 */
public class ESP extends Module {
    
    // Mode settings
    public String mode = "Box"; // Box, Outline, Corner Box
    public boolean showHealthBar = true;
    public boolean showInventory = true;
    public boolean showArmor = true;
    public boolean showDistance = true;
    public boolean showName = true;
    public boolean danger = true; // Red color for low health enemies
    
    // Visual effects
    public boolean glowEffect = true; // Неоновое свечение
    public boolean pulseAnimation = true; // Пульсация
    public boolean rainbowMode = false; // Радужный режим
    public boolean particles = true; // Частицы вокруг сущности
    
    // Filters
    public boolean showPlayers = true;
    public boolean showMobs = true; // ВКЛЮЧЕНО ПО УМОЛЧАНИЮ
    public boolean showInvisible = true;
    public boolean showNaked = true;
    
    // Range
    public int range = 50;
    
    // Outline Color (для всех режимов)
    public int outlineR = 0;
    public int outlineG = 255;
    public int outlineB = 255;
    public int outlineA = 255;
    
    // Fill Color для Outline и Corner Box
    public int fillOutlineR = 0;
    public int fillOutlineG = 255;
    public int fillOutlineB = 255;
    public int fillOutlineA = 60;
    
    // Fill Color для Box режима
    public int fillBoxR = 0;
    public int fillBoxG = 255;
    public int fillBoxB = 255;
    public int fillBoxA = 60;
    
    public ESP() {
        super("ESP");
    }
    
    @Override
    public String getDescription() {
        return "See players through walls";
    }
    
    @Override
    public boolean hasSettings() {
        return true;
    }
    
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onUpdate() {
        // Rendering handled in WorldRenderContextMixin
    }
}

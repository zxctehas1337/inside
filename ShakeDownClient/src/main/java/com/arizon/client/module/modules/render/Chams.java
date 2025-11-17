package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;

/**
 * Chams - See players through walls with CUSTOM COLORS
 */
public class Chams extends Module {
    
    public int range = 50; // Render distance through walls
    
    // Filters
    public boolean showPlayers = true;
    public boolean showMobs = true; // ВКЛЮЧЕНО ПО УМОЛЧАНИЮ
    public boolean showInvisible = true;
    public boolean showNaked = true;
    
    // Colors (яркие по умолчанию для видимости)
    public int colorR = 0;
    public int colorG = 255;
    public int colorB = 255;
    public int colorA = 200;
    
    public Chams() {
        super("Chams");
    }
    
    @Override
    public String getDescription() {
        return "See players through walls with custom colors";
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
        // Rendering handled in ChamsMixin
    }
}

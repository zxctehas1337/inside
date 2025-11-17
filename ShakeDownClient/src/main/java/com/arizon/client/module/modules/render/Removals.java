package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;

/**
 * Removals - Remove rendering of certain elements
 */
public class Removals extends Module {
    
    public boolean removeHurtCam = false;
    public boolean removeFireOverlay = true;
    public boolean removeWaterOverlay = false;
    public boolean removePumpkinOverlay = true;
    public boolean removeBossBar = false;
    public boolean removeScoreboard = false;
    public boolean removeFog = true;
    public boolean removeWeather = false;
    
    public Removals() {
        super("Removals");
    }
    
    @Override
    public String getDescription() {
        return "Remove unwanted visual elements";
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
        // Handled via mixins
    }
}

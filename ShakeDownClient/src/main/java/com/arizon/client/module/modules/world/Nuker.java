package com.arizon.client.module.modules.world;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class Nuker extends Module {
    
    public int range = 4;
    
    public Nuker() {
        super("Nuker");
    }
    
    @Override
    public String getDescription() {
        return "Automatically breaks blocks around you";
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
        // Break blocks around player
    }
}

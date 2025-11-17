package com.arizon.client.module.modules.world;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class Scaffold extends Module {
    
    public Scaffold() {
        super("Scaffold");
    }
    
    @Override
    public String getDescription() {
        return "Automatically places blocks under you";
    }
    
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onUpdate() {
        // Place blocks under player
    }
}

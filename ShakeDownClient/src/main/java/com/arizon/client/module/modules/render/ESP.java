package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

/**
 * ESP - Shows players through walls
 */
public class ESP extends Module {
    
    public String mode = "Box";
    public int range = 50;
    
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
        // Rendering handled in render event
    }
}

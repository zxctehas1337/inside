package com.arizon.client.module.modules.render;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class BlockESP extends Module {
    
    public int range = 50;
    
    public BlockESP() {
        super("Block ESP");
    }
    
    @Override
    public String getDescription() {
        return "Highlights specific blocks (ores, chests)";
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
    }
}

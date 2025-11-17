package com.arizon.client.module.modules.player;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class AutoTool extends Module {
    
    public AutoTool() {
        super("Auto Tool");
    }
    
    @Override
    public String getDescription() {
        return "Automatically switches to best tool";
    }
    
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onUpdate() {
        // Auto switch to best tool
    }
}

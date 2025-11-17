package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class NoFall extends Module {
    
    public NoFall() {
        super("No Fall");
    }
    
    @Override
    public String getDescription() {
        return "Prevents fall damage";
    }
    
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.fallDistance > 2.0f) {
            // Reset fall distance to prevent damage
            mc.player.fallDistance = 0.0f;
        }
    }
}

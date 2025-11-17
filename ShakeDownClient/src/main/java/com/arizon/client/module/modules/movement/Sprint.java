package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class Sprint extends Module {
    
    public Sprint() {
        super("Sprint");
    }
    
    @Override
    public String getDescription() {
        return "Automatically sprint when moving forward";
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
        if (mc.player != null && mc.player.forwardSpeed > 0) {
            mc.player.setSprinting(true);
        }
    }
}

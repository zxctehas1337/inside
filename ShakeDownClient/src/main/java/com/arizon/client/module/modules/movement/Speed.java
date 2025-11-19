package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class Speed extends Module {
    
    public float speed = 0.2f;
    
    public Speed() {
        super("Speed");
    }
    
    @Override
    public String getDescription() {
        return "Increases your movement speed";
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
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.isOnGround()) {
            mc.player.setVelocity(
                mc.player.getVelocity().x * (1 + speed),
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * (1 + speed)
            );
        }
    }
}

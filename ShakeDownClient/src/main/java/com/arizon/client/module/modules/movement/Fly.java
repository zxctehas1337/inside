package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class Fly extends Module {
    
    public Fly() {
        super("Fly");
    }
    
    @Override
    public String getDescription() {
        return "Allows you to fly in survival mode";
    }
    
    @Override
    public void onEnable() {
        MinecraftClient.getInstance().player.getAbilities().allowFlying = true;
    }
    
    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!mc.player.isCreative() && !mc.player.isSpectator()) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
        }
    }
    
    @Override
    public void onUpdate() {
    }
}

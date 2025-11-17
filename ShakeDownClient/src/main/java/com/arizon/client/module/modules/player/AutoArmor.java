package com.arizon.client.module.modules.player;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

public class AutoArmor extends Module {
    
    public AutoArmor() {
        super("Auto Armor");
    }
    
    @Override
    public String getDescription() {
        return "Automatically equips best armor";
    }
    
    @Override
    public void onEnable() {
    }
    
    @Override
    public void onDisable() {
    }
    
    @Override
    public void onUpdate() {
        // Auto equip best armor
    }
}

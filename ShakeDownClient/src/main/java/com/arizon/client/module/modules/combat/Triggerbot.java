package com.arizon.client.module.modules.combat;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;

/**
 * Triggerbot - Auto attack when crosshair on entity
 */
public class Triggerbot extends Module {
    
    public int delay = 50;
    public String mode = "1.9"; // 1.8 or 1.9
    private long lastAttack = 0;
    
    public Triggerbot() {
        super("Triggerbot");
    }
    
    @Override
    public String getDescription() {
        return "Auto attack when looking at entity";
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
        if (mc.player == null || mc.world == null) return;
        
        // Check if looking at entity
        if (mc.targetedEntity != null) {
            if (mode.equals("1.8")) {
                // 1.8 mode - fast clicking
                if (System.currentTimeMillis() - lastAttack >= delay) {
                    mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
                    mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                    lastAttack = System.currentTimeMillis();
                }
            } else {
                // 1.9 mode - wait for attack cooldown
                if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                    mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
                    mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                    mc.player.resetLastAttackedTicks();
                }
            }
        }
    }
}

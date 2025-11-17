package com.arizon.client.fakeplayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Fake player for PVP practice
 */
public class FakePlayer {
    
    private static OtherClientPlayerEntity fakePlayer;
    private static String currentMode = "none";
    
    public static void spawn(String mode) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        // Remove existing fake player
        remove();
        
        // Create new fake player with Steve model
        com.mojang.authlib.GameProfile profile = new com.mojang.authlib.GameProfile(
            java.util.UUID.randomUUID(), 
            "FakePlayer"
        );
        
        fakePlayer = new OtherClientPlayerEntity(mc.world, profile);
        fakePlayer.copyPositionAndRotation(mc.player);
        fakePlayer.headYaw = mc.player.headYaw;
        fakePlayer.bodyYaw = mc.player.bodyYaw;
        fakePlayer.setPitch(mc.player.getPitch());
        fakePlayer.setYaw(mc.player.getYaw());
        
        // Move slightly in front of player
        double yaw = Math.toRadians(mc.player.getYaw());
        double x = mc.player.getX() - Math.sin(yaw) * 3;
        double z = mc.player.getZ() + Math.cos(yaw) * 3;
        fakePlayer.setPosition(x, mc.player.getY(), z);
        
        // Set health
        fakePlayer.setHealth(20.0f);
        
        // Equip items based on mode
        currentMode = mode;
        switch (mode.toLowerCase()) {
            case "totem":
                fakePlayer.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
                break;
                
            case "netherite":
                fakePlayer.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                fakePlayer.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
                fakePlayer.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
                fakePlayer.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
                fakePlayer.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
                break;
                
            case "totemnetherite":
                fakePlayer.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                fakePlayer.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
                fakePlayer.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
                fakePlayer.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
                fakePlayer.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
                fakePlayer.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
                break;
                
            case "none":
            default:
                // No equipment
                break;
        }
        
        // Add to world (client-side only)
        mc.world.addEntity(fakePlayer.getId(), fakePlayer);
    }
    
    public static void remove() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (fakePlayer != null && mc.world != null) {
            mc.world.removeEntity(fakePlayer.getId(), net.minecraft.entity.Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
            currentMode = "none";
        }
    }
    
    public static OtherClientPlayerEntity get() {
        return fakePlayer;
    }
    
    public static boolean exists() {
        return fakePlayer != null;
    }
    
    public static String getCurrentMode() {
        return currentMode;
    }
    
    public static void damage(float amount) {
        if (fakePlayer != null) {
            float newHealth = fakePlayer.getHealth() - amount;
            fakePlayer.setHealth(Math.max(0, newHealth));
            
            // Visual damage effect
            fakePlayer.hurtTime = 10;
            fakePlayer.maxHurtTime = 10;
        }
    }
}

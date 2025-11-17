package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import com.arizon.client.module.ModuleManager;
import com.arizon.client.module.modules.combat.Aura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

/**
 * ElytraTarget - Automatically flies towards target with firework boost
 * Extends Aura range and auto-activates elytra flight
 */
public class ElytraTarget extends Module {
    
    // Settings
    public boolean autoFirework = true;
    public float speed = 1.5f;
    public float attackDistance = 2.0f; // Distance to start attacking (1-3 blocks)
    public float searchRange = 50.0f; // Extended search range (50-100 blocks) - ПО УМОЛЧАНИЮ 50
    public int fireworkDelay = 20; // ticks between fireworks
    public boolean autoActivateElytra = true; // Auto-activate elytra when falling
    public boolean keepFlying = true; // Автоматически поддерживать полет фейерверками
    
    private int fireworkCooldown = 0;
    private int keepFlyingCooldown = 0;
    private LivingEntity currentTarget = null;
    private boolean wasFlying = false;
    
    public ElytraTarget() {
        super("ElytraTarget");
    }
    
    @Override
    public String getDescription() {
        return "Auto-flies to targets with elytra and fireworks";
    }
    
    @Override
    public boolean hasSettings() {
        return true;
    }
    
    @Override
    public void onEnable() {
        fireworkCooldown = 0;
        keepFlyingCooldown = 0;
        currentTarget = null;
        wasFlying = false;
    }
    
    @Override
    public void onDisable() {
        fireworkCooldown = 0;
        keepFlyingCooldown = 0;
        currentTarget = null;
        wasFlying = false;
    }
    
    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        // Check if player is wearing elytra
        ItemStack chestplate = mc.player.getInventory().getArmorStack(2);
        if (chestplate.getItem() != Items.ELYTRA) return;
        
        // Auto-activate elytra when falling
        if (autoActivateElytra && !mc.player.isFallFlying() && !mc.player.isOnGround() && 
            mc.player.getVelocity().y < -0.5) {
            // Start elytra flight
            mc.player.startFallFlying();
        }
        
        // Get or find target with extended range
        if (currentTarget == null || currentTarget.isRemoved() || currentTarget.getHealth() <= 0) {
            currentTarget = findExtendedTarget(mc);
        }
        
        // Enable Aura if we have a target and it's in Aura range
        Aura aura = (Aura) ModuleManager.getInstance().getModuleByName("Aura");
        if (aura != null && currentTarget != null) {
            double distanceToTarget = mc.player.distanceTo(currentTarget);
            
            // Временно увеличиваем радиус Aura для поиска цели
            float originalRange = aura.range;
            aura.range = searchRange;
            
            // Enable Aura when close to target
            if (distanceToTarget <= originalRange && !aura.isEnabled()) {
                aura.setEnabled(true);
            }
            
            // Update Aura's target if we're close enough
            if (distanceToTarget <= originalRange) {
                aura.currentTarget = currentTarget;
            }
            
            // Восстанавливаем оригинальный радиус для атаки
            aura.range = originalRange;
        }
        
        // No target found
        if (currentTarget == null) return;
        
        // Calculate distance to target
        double distance = mc.player.distanceTo(currentTarget);
        
        // If target is too far, clear it
        if (distance > searchRange) {
            currentTarget = null;
            return;
        }
        
        // Only fly if we're actually flying on elytra
        if (mc.player.isFallFlying()) {
            wasFlying = true;
            
            // Calculate direction to target (ПРЯМО К ЦЕЛИ, НЕ ПО НАПРАВЛЕНИЮ КАМЕРЫ)
            Vec3d playerPos = mc.player.getPos();
            Vec3d targetPos = currentTarget.getPos().add(0, currentTarget.getEyeHeight(currentTarget.getPose()) / 2, 0);
            
            // Направление к цели (независимо от камеры)
            Vec3d directionToTarget = targetPos.subtract(playerPos).normalize();
            
            // Apply velocity towards target with smooth interpolation
            Vec3d velocity = mc.player.getVelocity();
            Vec3d targetVelocity = directionToTarget.multiply(speed);
            
            // Smooth interpolation (80% current + 20% target)
            // ВАЖНО: Используем направление К ЦЕЛИ, а не направление камеры
            Vec3d newVelocity = velocity.multiply(0.8).add(targetVelocity.multiply(0.2));
            mc.player.setVelocity(newVelocity);
            
            // Auto firework boost
            if (autoFirework) {
                // Use fireworks more frequently when close to target
                boolean closeToTarget = distance <= attackDistance;
                int effectiveDelay = closeToTarget ? Math.max(5, fireworkDelay / 2) : fireworkDelay;
                
                if (fireworkCooldown <= 0) {
                    if (useFirework(mc)) {
                        fireworkCooldown = effectiveDelay;
                    }
                } else {
                    fireworkCooldown--;
                }
            }
            
            // Дополнительные фейерверки для поддержания полета
            if (keepFlying) {
                // Если падаем слишком быстро, используем фейерверк
                if (mc.player.getVelocity().y < -0.5 && keepFlyingCooldown <= 0) {
                    if (useFirework(mc)) {
                        keepFlyingCooldown = 10; // Короткая задержка
                    }
                } else if (keepFlyingCooldown > 0) {
                    keepFlyingCooldown--;
                }
            }
            
            // НЕ ПОВОРАЧИВАЕМ ИГРОКА - летим к цели через velocity
            // Игрок может управлять камерой свободно
        } else {
            // Not flying - try to start flying if falling
            if (autoActivateElytra && !mc.player.isOnGround() && mc.player.getVelocity().y < -0.5) {
                mc.player.startFallFlying();
            }
        }
    }
    
    private LivingEntity findExtendedTarget(MinecraftClient mc) {
        List<LivingEntity> entities = new java.util.ArrayList<>();
        
        // Get Aura settings for target filtering
        Aura aura = (Aura) ModuleManager.getInstance().getModuleByName("Aura");
        boolean attackPlayers = aura != null ? aura.attackPlayers : true;
        boolean attackMobs = aura != null ? aura.attackMobs : true;
        boolean attackAnimals = aura != null ? aura.attackAnimals : false;
        
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (entity.isRemoved()) continue;
            
            LivingEntity living = (LivingEntity) entity;
            if (living.getHealth() <= 0) continue;
            
            double distance = mc.player.distanceTo(entity);
            if (distance > searchRange) continue;
            
            // Filter by type
            boolean shouldTarget = false;
            if (entity instanceof PlayerEntity && attackPlayers) shouldTarget = true;
            if (entity instanceof Monster && attackMobs) shouldTarget = true;
            if (entity instanceof AnimalEntity && attackAnimals) shouldTarget = true;
            
            // Check for fake player
            if (com.arizon.client.fakeplayer.FakePlayer.exists() && 
                entity == com.arizon.client.fakeplayer.FakePlayer.get()) {
                shouldTarget = true;
            }
            
            if (shouldTarget) {
                entities.add(living);
            }
        }
        
        if (entities.isEmpty()) return null;
        
        // Sort by distance (closest first)
        entities.sort(Comparator.comparingDouble(e -> mc.player.distanceTo(e)));
        
        return entities.get(0);
    }
    
    // Метод удален - не поворачиваем игрока
    // Летим к цели через velocity, управление остается нормальным
    
    private boolean useFirework(MinecraftClient mc) {
        // Find firework in inventory
        ItemStack firework = null;
        int fireworkSlot = -1;
        
        // Check hotbar first
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.FIREWORK_ROCKET) {
                firework = stack;
                fireworkSlot = i;
                break;
            }
        }
        
        if (firework == null) return false;
        
        // Save current slot
        int previousSlot = mc.player.getInventory().selectedSlot;
        
        // Switch to firework slot
        mc.player.getInventory().selectedSlot = fireworkSlot;
        
        // Use firework
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        
        // Restore previous slot
        mc.player.getInventory().selectedSlot = previousSlot;
        
        return true;
    }
}

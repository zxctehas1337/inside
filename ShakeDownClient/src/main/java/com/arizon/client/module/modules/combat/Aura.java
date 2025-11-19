package com.arizon.client.module.modules.combat;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aura - Auto attack entities in range
 */
public class Aura extends Module {
    
    public float range = 4.2f;
    public String pvpMode = "1.9"; // 1.8 or 1.9
    public boolean attackPlayers = true;
    public boolean attackMobs = true;
    public boolean attackAnimals = false;
    public boolean showTargetHUD = true;
    public String targetMode = "Distance"; // Distance, Health, Angle
    public String attackMode = "Single"; // Single, Multi, HolyWorld, Funtime Snap
    public boolean snapMode = true; // Snap to target - auto-enabled for HolyWorld/Funtime
    public boolean critOnly = false; // Only attack with critical hits
    public boolean tpsSync = false; // Sync with server TPS
    public String antiCheat = "None"; // None, Grim, Vulcan, Grim+Vulcan
    
    private long lastAttack = 0;
    public LivingEntity currentTarget = null;
    private float serverYaw = 0;
    private float serverPitch = 0;
    private float originalYaw = 0;
    private float originalPitch = 0;
    private boolean isSnapping = false;
    private long snapStartTime = 0;
    private static final long SNAP_DURATION = 150; // 150ms snap duration
    
    public Aura() {
        super("Aura");
    }
    
    @Override
    public String getDescription() {
        return "Automatically attacks entities in range";
    }
    
    @Override
    public boolean hasSettings() {
        return true;
    }
    
    @Override
    public void onEnable() {
        currentTarget = null;
    }
    
    @Override
    public void onDisable() {
        currentTarget = null;
    }
    
    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        // Find target
        currentTarget = findTarget();
        
        // Handle snap animation (only when attacking)
        if (isSnapping) {
            long elapsed = System.currentTimeMillis() - snapStartTime;
            if (elapsed >= SNAP_DURATION) {
                // Snap finished - return to normal
                isSnapping = false;
                mc.player.bodyYaw = originalYaw;
                mc.player.headYaw = originalYaw;
            } else {
                // Quick snap to target (only during attack)
                float progress = elapsed / (float)SNAP_DURATION;
                progress = easeOutCubic(progress);
                mc.player.bodyYaw = lerp(originalYaw, serverYaw, progress);
                mc.player.headYaw = lerp(originalYaw, serverYaw, progress);
            }
        }
        
        // Attack target based on PVP mode
        if (currentTarget != null) {
            // Auto-enable snap for HolyWorld and Funtime modes
            if (attackMode.equals("HolyWorld") || attackMode.equals("Funtime Snap")) {
                snapMode = true;
            }
            
            // Start snap ONLY when actually attacking
            boolean shouldAttack = false;
            if (pvpMode.equals("1.8")) {
                shouldAttack = System.currentTimeMillis() - lastAttack >= 50;
            } else {
                shouldAttack = mc.player.getAttackCooldownProgress(0.5f) >= 1.0f;
            }
            
            if (snapMode && !isSnapping && shouldAttack) {
                // Calculate rotation to target
                double deltaX = currentTarget.getX() - mc.player.getX();
                double deltaZ = currentTarget.getZ() - mc.player.getZ();
                double deltaY = currentTarget.getY() + currentTarget.getEyeHeight(currentTarget.getPose()) 
                              - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
                
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                serverYaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0f;
                serverPitch = (float) -(Math.atan2(deltaY, distance) * 180.0 / Math.PI);
                
                // Save original rotation
                originalYaw = mc.player.bodyYaw;
                originalPitch = mc.player.getPitch();
                
                // Start snap animation
                isSnapping = true;
                snapStartTime = System.currentTimeMillis();
            }
            
            // Check if we can crit (only if critOnly is enabled)
            boolean canAttack = true;
            if (critOnly) {
                // Can crit if: falling, not on ground, not in water/lava, not climbing, not riding
                canAttack = mc.player.fallDistance > 0.0f && 
                           !mc.player.isOnGround() && 
                           !mc.player.isTouchingWater() && 
                           !mc.player.isInLava() && 
                           !mc.player.isClimbing() && 
                           !mc.player.hasVehicle() &&
                           mc.player.getVelocity().y < 0; // Must be falling down
            }
            
            // Attack based on mode
            if (canAttack) {
                if (pvpMode.equals("1.8")) {
                    // 1.8 mode - fast clicking (no cooldown)
                    if (System.currentTimeMillis() - lastAttack >= 50) {
                        mc.interactionManager.attackEntity(mc.player, currentTarget);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        lastAttack = System.currentTimeMillis();
                        
                        // Damage fake player
                        if (com.arizon.client.fakeplayer.FakePlayer.exists() && 
                            currentTarget == com.arizon.client.fakeplayer.FakePlayer.get()) {
                            com.arizon.client.fakeplayer.FakePlayer.damage(4.0f);
                        }
                    }
                } else {
                    // 1.9 mode - wait for cooldown
                    if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                        mc.interactionManager.attackEntity(mc.player, currentTarget);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        mc.player.resetLastAttackedTicks();
                        
                        // Damage fake player
                        if (com.arizon.client.fakeplayer.FakePlayer.exists() && 
                            currentTarget == com.arizon.client.fakeplayer.FakePlayer.get()) {
                            com.arizon.client.fakeplayer.FakePlayer.damage(6.0f);
                        }
                    }
                }
            }
        }
    }
    
    private LivingEntity findTarget() {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        List<LivingEntity> entities = new java.util.ArrayList<>();
        
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (entity.isRemoved()) continue;
            
            LivingEntity living = (LivingEntity) entity;
            if (living.getHealth() <= 0) continue;
            if (mc.player.distanceTo(entity) > range) continue;
            if (!shouldAttack(entity)) continue;
            
            entities.add(living);
        }
        
        if (entities.isEmpty()) return null;
        
        // Sort by target mode
        switch (targetMode) {
            case "Health":
                entities.sort(Comparator.comparingDouble(LivingEntity::getHealth));
                break;
            case "Distance":
            default:
                entities.sort(Comparator.comparingDouble(e -> mc.player.distanceTo(e)));
                break;
        }
        
        return entities.get(0);
    }
    
    private boolean shouldAttack(Entity entity) {
        // Check if it's our fake player
        if (com.arizon.client.fakeplayer.FakePlayer.exists() && 
            entity == com.arizon.client.fakeplayer.FakePlayer.get()) {
            return true; // Always attack fake player
        }
        
        if (entity instanceof PlayerEntity && attackPlayers) return true;
        if (entity instanceof Monster && attackMobs) return true;
        if (entity instanceof AnimalEntity && attackAnimals) return true;
        return false;
    }
    
    // Smooth interpolation
    private float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }
    
    // Smooth easing function
    private float easeOutCubic(float t) {
        return 1 - (float)Math.pow(1 - t, 3);
    }
}

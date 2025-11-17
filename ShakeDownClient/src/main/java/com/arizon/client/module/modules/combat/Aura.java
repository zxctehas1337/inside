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
    public String attackMode = "Single"; // Single, Funtime Snap, Neuro
    public boolean attackPlayers = true;
    public boolean attackMobs = true;
    public boolean attackAnimals = false;
    public boolean showTargetHUD = true;
    public String targetMode = "Distance"; // Distance, Health, Angle
    public boolean snapMode = true; // Snap to target
    public boolean critOnly = false; // Only attack with critical hits
    public boolean tpsSync = false; // Sync with server TPS
    
    // Funtime Snap settings
    public float funtimeSnapAngle = 23.5f; // ±23.5° для обхода Funtime AC
    public int funtimeSnapDelay = 50; // Задержка между снапами (ms)
    
    // Neuro settings - МАКСИМАЛЬНО РЕАЛИСТИЧНО
    public float neuroMissChance = 0.0f; // 0% промахов (нейро не промахивается!)
    public int neuroMinDelay = 1; // 1-3 тика микрозадержка
    public int neuroMaxDelay = 3; // Очень мелкие задержки
    public float neuroAimOffset = 0.02f; // Микроскопическое смещение
    public boolean neuroSmoothAim = true; // Плавное наведение
    public float neuroSmoothSpeed = 0.4f; // Скорость наведения (0.1-1.0)
    
    private long lastAttack = 0;
    public LivingEntity currentTarget = null;
    private float serverYaw = 0;
    private float serverPitch = 0;
    private float originalYaw = 0;
    private float originalPitch = 0;
    private boolean isSnapping = false;
    private long snapStartTime = 0;
    private static final long SNAP_DURATION = 150; // 150ms snap duration
    
    // Funtime Snap state
    private boolean funtimeSnapActive = false;
    private long lastFuntimeSnap = 0;
    private float funtimeCurrentAngle = 0;
    
    // Neuro state
    private long neuroNextAttackTime = 0;
    private boolean neuroShouldMiss = false;
    private float neuroAimOffsetX = 0;
    private float neuroAimOffsetY = 0;
    
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
        
        // Attack based on mode
        if (currentTarget != null) {
            switch (attackMode) {
                case "Funtime Snap":
                    updateFuntimeSnap(mc);
                    break;
                case "Neuro":
                    updateNeuro(mc);
                    break;
                default: // Single
                    updateSingle(mc);
                    break;
            }
        }
    }
    
    /**
     * SINGLE MODE - обычная атака
     */
    private void updateSingle(MinecraftClient mc) {
        // Handle snap animation
        if (isSnapping) {
            long elapsed = System.currentTimeMillis() - snapStartTime;
            if (elapsed >= SNAP_DURATION) {
                isSnapping = false;
                mc.player.bodyYaw = originalYaw;
                mc.player.headYaw = originalYaw;
            } else {
                float progress = elapsed / (float)SNAP_DURATION;
                progress = easeOutCubic(progress);
                mc.player.bodyYaw = lerp(originalYaw, serverYaw, progress);
                mc.player.headYaw = lerp(originalYaw, serverYaw, progress);
            }
        }
        
        // Check if should attack
        boolean shouldAttack = false;
        if (pvpMode.equals("1.8")) {
            shouldAttack = System.currentTimeMillis() - lastAttack >= 50;
        } else {
            shouldAttack = mc.player.getAttackCooldownProgress(0.5f) >= 1.0f;
        }
        
        // Start snap
        if (snapMode && !isSnapping && shouldAttack) {
            calculateRotation(mc);
            originalYaw = mc.player.bodyYaw;
            originalPitch = mc.player.getPitch();
            isSnapping = true;
            snapStartTime = System.currentTimeMillis();
        }
        
        // Attack
        if (canAttack(mc) && shouldAttack) {
            performAttack(mc);
        }
    }
    
    /**
     * FUNTIME SNAP MODE - обход Funtime AntiCheat
     * Поворот головы в диапазоне ±23.5° с снапом ударов
     */
    private void updateFuntimeSnap(MinecraftClient mc) {
        long currentTime = System.currentTimeMillis();
        
        // Проверяем можем ли атаковать
        boolean shouldAttack = false;
        if (pvpMode.equals("1.8")) {
            shouldAttack = currentTime - lastAttack >= 50;
        } else {
            shouldAttack = mc.player.getAttackCooldownProgress(0.5f) >= 1.0f;
        }
        
        if (!shouldAttack || !canAttack(mc)) return;
        
        // Проверяем задержку между снапами
        if (currentTime - lastFuntimeSnap < funtimeSnapDelay) return;
        
        // Вычисляем направление к цели
        calculateRotation(mc);
        
        // Вычисляем разницу углов
        float yawDiff = serverYaw - mc.player.getYaw();
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        
        // КЛЮЧЕВОЙ МОМЕНТ: Ограничиваем поворот до ±23.5°
        float clampedYawDiff = Math.max(-funtimeSnapAngle, Math.min(funtimeSnapAngle, yawDiff));
        
        // Применяем ограниченный поворот
        float targetYaw = mc.player.getYaw() + clampedYawDiff;
        
        // Быстрый снап головы (только голова, не тело)
        mc.player.setYaw(targetYaw);
        mc.player.setPitch(serverPitch);
        mc.player.headYaw = targetYaw;
        
        // Атакуем
        performAttack(mc);
        
        // Сохраняем время снапа
        lastFuntimeSnap = currentTime;
        funtimeCurrentAngle = clampedYawDiff;
    }
    
    /**
     * NEURO MODE - МАКСИМАЛЬНО РЕАЛИСТИЧНЫЙ PvP
     * БЕЗ промахов, микрозадержки 1-3 тика, плавное наведение
     */
    private void updateNeuro(MinecraftClient mc) {
        long currentTime = System.currentTimeMillis();
        
        // Проверяем можем ли атаковать
        boolean cooldownReady = false;
        if (pvpMode.equals("1.8")) {
            cooldownReady = currentTime - lastAttack >= 50;
        } else {
            cooldownReady = mc.player.getAttackCooldownProgress(0.5f) >= 1.0f;
        }
        
        if (!cooldownReady || !canAttack(mc)) return;
        
        // Микрозадержка (1-3 тика = 50-150ms)
        int tickDelay = neuroMinDelay + (int) (Math.random() * (neuroMaxDelay - neuroMinDelay + 1));
        int msDelay = tickDelay * 50; // 1 тик = 50ms
        
        if (currentTime < neuroNextAttackTime) return;
        
        // Вычисляем направление к цели
        calculateRotation(mc);
        
        // Микроскопическое смещение (имитация человеческой неточности)
        if (neuroAimOffsetX == 0 && neuroAimOffsetY == 0) {
            // Очень маленькое случайное смещение
            neuroAimOffsetX = (float) (Math.random() - 0.5) * neuroAimOffset * 2;
            neuroAimOffsetY = (float) (Math.random() - 0.5) * neuroAimOffset * 2;
        }
        
        // Целевые углы с микросмещением
        float targetYaw = serverYaw + neuroAimOffsetX;
        float targetPitch = serverPitch + neuroAimOffsetY;
        
        // Текущие углы
        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();
        
        // Вычисляем разницу (с учетом 360°)
        float yawDiff = targetYaw - currentYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        
        float pitchDiff = targetPitch - currentPitch;
        
        // ПЛАВНОЕ наведение (как человек)
        if (neuroSmoothAim) {
            // Скорость зависит от расстояния до цели
            float distance = Math.abs(yawDiff) + Math.abs(pitchDiff);
            float adaptiveSpeed = neuroSmoothSpeed;
            
            // Если далеко - быстрее, если близко - медленнее
            if (distance > 30) {
                adaptiveSpeed = Math.min(0.8f, neuroSmoothSpeed * 1.5f);
            } else if (distance < 5) {
                adaptiveSpeed = Math.max(0.2f, neuroSmoothSpeed * 0.5f);
            }
            
            // Плавное движение
            float newYaw = currentYaw + yawDiff * adaptiveSpeed;
            float newPitch = currentPitch + pitchDiff * adaptiveSpeed;
            
            mc.player.setYaw(newYaw);
            mc.player.setPitch(newPitch);
            mc.player.headYaw = newYaw;
            mc.player.bodyYaw = newYaw;
            
            // Атакуем только если прицел достаточно близко к цели
            float aimError = Math.abs(yawDiff * (1 - adaptiveSpeed)) + Math.abs(pitchDiff * (1 - adaptiveSpeed));
            
            if (aimError < 2.0f) { // Порог точности
                performAttack(mc);
                
                // Устанавливаем следующую микрозадержку
                neuroNextAttackTime = currentTime + msDelay;
                
                // Сбрасываем смещение
                neuroAimOffsetX = 0;
                neuroAimOffsetY = 0;
            }
        } else {
            // Без плавного наведения (мгновенный снап)
            mc.player.setYaw(targetYaw);
            mc.player.setPitch(targetPitch);
            mc.player.headYaw = targetYaw;
            mc.player.bodyYaw = targetYaw;
            
            performAttack(mc);
            
            // Микрозадержка
            neuroNextAttackTime = currentTime + msDelay;
            
            // Сбрасываем смещение
            neuroAimOffsetX = 0;
            neuroAimOffsetY = 0;
        }
    }
    
    /**
     * Вычисляет поворот к цели
     */
    private void calculateRotation(MinecraftClient mc) {
        double deltaX = currentTarget.getX() - mc.player.getX();
        double deltaZ = currentTarget.getZ() - mc.player.getZ();
        double deltaY = currentTarget.getY() + currentTarget.getEyeHeight(currentTarget.getPose()) 
                      - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        serverYaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0f;
        serverPitch = (float) -(Math.atan2(deltaY, distance) * 180.0 / Math.PI);
    }
    
    /**
     * Проверяет можно ли атаковать (крит и т.д.)
     */
    private boolean canAttack(MinecraftClient mc) {
        if (!critOnly) return true;
        
        return mc.player.fallDistance > 0.0f && 
               !mc.player.isOnGround() && 
               !mc.player.isTouchingWater() && 
               !mc.player.isInLava() && 
               !mc.player.isClimbing() && 
               !mc.player.hasVehicle() &&
               mc.player.getVelocity().y < 0;
    }
    
    /**
     * Выполняет атаку
     */
    private void performAttack(MinecraftClient mc) {
        mc.interactionManager.attackEntity(mc.player, currentTarget);
        mc.player.swingHand(Hand.MAIN_HAND);
        
        if (pvpMode.equals("1.9")) {
            mc.player.resetLastAttackedTicks();
        }
        
        lastAttack = System.currentTimeMillis();
        
        // Damage fake player
        if (com.arizon.client.fakeplayer.FakePlayer.exists() && 
            currentTarget == com.arizon.client.fakeplayer.FakePlayer.get()) {
            float damage = pvpMode.equals("1.9") ? 6.0f : 4.0f;
            com.arizon.client.fakeplayer.FakePlayer.damage(damage);
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

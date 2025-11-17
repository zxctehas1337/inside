package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Speed extends Module {
    
    public String mode = "Vanilla"; // Vanilla, Funtime
    public float speed = 0.2f;
    
    // Funtime settings
    private BlockPos trapdoorPos = null;
    private boolean wasJumping = false;
    
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
        trapdoorPos = null;
        wasJumping = false;
    }
    
    @Override
    public void onDisable() {
        // Убираем люк при выключении
        if (trapdoorPos != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world != null && mc.player != null) {
                removeTrapdoor(mc);
            }
        }
    }
    
    @Override
    public void onUpdate() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        
        if (mode.equals("Funtime")) {
            updateFuntime(mc);
        } else {
            // Vanilla mode
            if (mc.player.isOnGround()) {
                mc.player.setVelocity(
                    mc.player.getVelocity().x * (1 + speed),
                    mc.player.getVelocity().y,
                    mc.player.getVelocity().z * (1 + speed)
                );
            }
        }
    }
    
    /**
     * FUNTIME MODE - невидимый люк + автопрыжки
     */
    private void updateFuntime(MinecraftClient mc) {
        boolean isJumping = mc.options.jumpKey.isPressed();
        
        // Если игрок прыгает - убираем люк
        if (isJumping && !wasJumping) {
            removeTrapdoor(mc);
        }
        
        // Если игрок не прыгает и на земле - ставим люк и прыгаем
        if (!isJumping && mc.player.isOnGround()) {
            // Позиция люка (над головой)
            BlockPos playerPos = mc.player.getBlockPos();
            BlockPos targetPos = playerPos.up(2);
            
            // Проверяем что там воздух
            if (mc.world.getBlockState(targetPos).isAir()) {
                // Ставим невидимый люк
                placeTrapdoor(mc, targetPos);
                
                // Автопрыжок БЕЗ ЗАДЕРЖКИ
                mc.player.jump();
            }
        }
        
        wasJumping = isJumping;
    }
    
    /**
     * Ставит невидимый люк (только на клиенте)
     */
    private void placeTrapdoor(MinecraftClient mc, BlockPos pos) {
        if (trapdoorPos != null && !trapdoorPos.equals(pos)) {
            removeTrapdoor(mc);
        }
        
        trapdoorPos = pos;
        
        // Создаем состояние закрытого люка
        BlockState trapdoorState = Blocks.IRON_TRAPDOOR.getDefaultState()
            .with(TrapdoorBlock.FACING, Direction.NORTH)
            .with(TrapdoorBlock.HALF, BlockHalf.TOP)
            .with(TrapdoorBlock.OPEN, false);
        
        // Ставим ТОЛЬКО НА КЛИЕНТЕ (не отправляем на сервер)
        mc.world.setBlockState(pos, trapdoorState, 0);
    }
    
    /**
     * Убирает невидимый люк
     */
    private void removeTrapdoor(MinecraftClient mc) {
        if (trapdoorPos != null) {
            // Убираем ТОЛЬКО НА КЛИЕНТЕ
            mc.world.setBlockState(trapdoorPos, Blocks.AIR.getDefaultState(), 0);
            trapdoorPos = null;
        }
    }
}

package com.arizon.client.module.modules.movement;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * GuiMove - Move while GUI is open
 */
public class GuiMove extends Module {
    
    public String mode = "Vanilla"; // Vanilla, Funtime, Grim, Vulcan, Spooky
    
    public GuiMove() {
        super("GuiMove");
    }
    
    @Override
    public boolean hasSettings() {
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Move while GUI screens are open";
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
        if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen) return;
        
        // Allow movement in GUI based on mode
        switch (mode) {
            case "Vanilla":
                updateMovement(mc, false);
                break;
            case "Funtime":
            case "Grim":
            case "Vulcan":
            case "Spooky":
                updateMovement(mc, true);
                break;
        }
    }
    
    private void updateMovement(MinecraftClient mc, boolean bypass) {
        long handle = mc.getWindow().getHandle();
        
        mc.options.forwardKey.setPressed(InputUtil.isKeyPressed(handle, 
            InputUtil.fromTranslationKey(mc.options.forwardKey.getBoundKeyTranslationKey()).getCode()));
        mc.options.backKey.setPressed(InputUtil.isKeyPressed(handle, 
            InputUtil.fromTranslationKey(mc.options.backKey.getBoundKeyTranslationKey()).getCode()));
        mc.options.leftKey.setPressed(InputUtil.isKeyPressed(handle, 
            InputUtil.fromTranslationKey(mc.options.leftKey.getBoundKeyTranslationKey()).getCode()));
        mc.options.rightKey.setPressed(InputUtil.isKeyPressed(handle, 
            InputUtil.fromTranslationKey(mc.options.rightKey.getBoundKeyTranslationKey()).getCode()));
        mc.options.jumpKey.setPressed(InputUtil.isKeyPressed(handle, 
            InputUtil.fromTranslationKey(mc.options.jumpKey.getBoundKeyTranslationKey()).getCode()));
        mc.options.sprintKey.setPressed(InputUtil.isKeyPressed(handle, 
            InputUtil.fromTranslationKey(mc.options.sprintKey.getBoundKeyTranslationKey()).getCode()));
    }
}

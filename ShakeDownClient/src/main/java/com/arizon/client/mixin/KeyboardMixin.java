package com.arizon.client.mixin;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.gui.screen.UltraPremiumGui;
import com.arizon.client.keybind.KeyBindSystem;
import com.arizon.client.module.Module;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Only handle key press (action == 1)
        if (action != 1) return;
        
        // Open GUI with Right Shift
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            try {
                System.out.println("[Arizon Client] Opening GUI...");
                mc.setScreen(new UltraPremiumGui());
                System.out.println("[Arizon Client] GUI opened successfully!");
            } catch (Exception e) {
                System.err.println("[Arizon Client] ERROR opening GUI: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }
        
        // Check if any module is bound to this key
        for (Module module : ArizonClientMod.getInstance().getModuleManager().getModules()) {
            Integer boundKey = KeyBindSystem.getBind(module.getName());
            if (boundKey != null && boundKey == key) {
                module.toggle();
                return;
            }
        }
    }
}

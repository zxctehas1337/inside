package com.arizon.client.keybind;

import com.arizon.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Keybind system with mouse wheel support
 */
public class KeyBindSystem {
    
    private static final Map<String, Integer> binds = new HashMap<>();
    private static String pendingBindModule = null;
    private static boolean waitingForKey = false;
    
    public static void bind(String moduleName, int keyCode) {
        binds.put(moduleName, keyCode);
    }
    
    public static void unbind(String moduleName) {
        binds.remove(moduleName);
    }
    
    public static Integer getBind(String moduleName) {
        return binds.get(moduleName);
    }
    
    public static Map<String, Integer> getAllBinds() {
        return new HashMap<>(binds);
    }
    
    public static void startBinding(String moduleName) {
        pendingBindModule = moduleName;
        waitingForKey = true;
    }
    
    public static void cancelBinding() {
        pendingBindModule = null;
        waitingForKey = false;
    }
    
    public static boolean isWaitingForKey() {
        return waitingForKey;
    }
    
    public static String getPendingModule() {
        return pendingBindModule;
    }
    
    public static void handleKeyPress(int keyCode) {
        if (waitingForKey && pendingBindModule != null) {
            bind(pendingBindModule, keyCode);
            waitingForKey = false;
            pendingBindModule = null;
            
            // Auto-save keybinds
            save();
        }
    }
    
    public static void save() {
        com.arizon.client.ArizonClientMod.getInstance().getConfigManager().saveKeybinds(binds);
    }
    
    public static void load() {
        Map<String, Integer> loadedBinds = com.arizon.client.ArizonClientMod.getInstance()
            .getConfigManager().loadKeybinds();
        binds.clear();
        binds.putAll(loadedBinds);
    }
    
    public static void renderBindPrompt(DrawContext context, int width, int height) {
        if (!waitingForKey || pendingBindModule == null) return;
        
        // Dim background (35% tint)
        context.fill(0, 0, width, height, 0x59000000);
        
        // Center box
        int boxWidth = 300;
        int boxHeight = 100;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        // Box background with glow
        context.fill(boxX - 2, boxY - 2, boxX + boxWidth + 2, boxY + boxHeight + 2, 
                   com.arizon.client.gui.theme.NeonTheme.GLOW_CYAN);
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 
                   com.arizon.client.gui.theme.NeonTheme.BG_MAIN);
        
        // Top accent line
        context.fill(boxX, boxY, boxX + boxWidth, boxY + 2, 
                   com.arizon.client.gui.theme.NeonTheme.ACCENT_CYAN);
        
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Title
        String title = "Binding: " + pendingBindModule;
        int titleWidth = mc.textRenderer.getWidth(title);
        context.drawText(mc.textRenderer, title, 
                       boxX + (boxWidth - titleWidth) / 2, 
                       boxY + 20, 
                       com.arizon.client.gui.theme.NeonTheme.ACCENT_CYAN, true);
        
        // Instructions
        String instruction = "Press any key or ESC to cancel";
        int instrWidth = mc.textRenderer.getWidth(instruction);
        context.drawText(mc.textRenderer, instruction, 
                       boxX + (boxWidth - instrWidth) / 2, 
                       boxY + 50, 
                       com.arizon.client.gui.theme.NeonTheme.TEXT_SECONDARY, false);
    }
}

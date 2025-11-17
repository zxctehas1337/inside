package com.arizon.client;

import net.minecraft.client.MinecraftClient;

/**
 * Vanilla version of Arizon Client (without Fabric/Mixins)
 * Uses pure ASM patching
 */
public class VanillaClient {
    
    private static VanillaClient instance;
    private static final String VERSION = "1.0.0-PATCHED";
    private static final String NAME = "Arizon Client";
    
    private static boolean initialized = false;
    private static MinecraftClient mc;
    private static long lastKeyCheck = 0;
    private static boolean wasRightShiftPressed = false;
    
    /**
     * Called from patched MinecraftClient constructor
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            System.out.println("========================================");
            System.out.println("ARIZON CLIENT - PATCHED VERSION");
            System.out.println("Version: " + VERSION);
            System.out.println("========================================");
            
            instance = new VanillaClient();
            initialized = true;
            
            // Get Minecraft instance
            mc = MinecraftClient.getInstance();
            
            System.out.println("[ARIZON] Client initialized successfully!");
            System.out.println("[ARIZON] Press RIGHT SHIFT to open menu");
            
        } catch (Exception e) {
            System.err.println("[ARIZON] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Called every tick from patched MinecraftClient.tick()
     */
    public static void onTick() {
        if (!initialized || mc == null) {
            return;
        }
        
        try {
            // Check for RIGHT SHIFT key (every 10 ticks to avoid spam)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastKeyCheck > 100) {
                lastKeyCheck = currentTime;
                checkKeyPress();
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private static void checkKeyPress() {
        try {
            // Check if RIGHT SHIFT is pressed (GLFW key code 344)
            long window = mc.getWindow().getHandle();
            boolean isPressed = org.lwjgl.glfw.GLFW.glfwGetKey(window, 344) == 1;
            
            if (isPressed && !wasRightShiftPressed) {
                // Key just pressed
                toggleGui();
            }
            
            wasRightShiftPressed = isPressed;
            
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private static void toggleGui() {
        try {
            if (mc.currentScreen == null) {
                mc.setScreen(new SimpleGui());
                System.out.println("[ARIZON] GUI opened");
            } else {
                mc.setScreen(null);
                System.out.println("[ARIZON] GUI closed");
            }
        } catch (Exception e) {
            System.err.println("[ARIZON] Failed to toggle GUI: " + e.getMessage());
        }
    }
    
    public static VanillaClient getInstance() {
        return instance;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
}

package com.arizon.client;

import com.arizon.client.config.ConfigManager;
import com.arizon.client.gui.GuiManager;
import com.arizon.client.input.KeyBindManager;

/**
 * Arizon Client - Custom Minecraft Client
 * Main entry point for the client
 * 
 * @version 1.0.0
 * @author Arizon Team
 */
public class ArizonClient {
    
    private static ArizonClient instance;
    private ConfigManager configManager;
    private GuiManager guiManager;
    private KeyBindManager keyBindManager;
    
    private static final String VERSION = "1.0.0";
    private static final String NAME = "ShakeDown Client";
    
    public ArizonClient() {
        instance = this;
    }
    
    /**
     * Initialize the client
     */
    public void init() {
        System.out.println("Initializing " + NAME + " v" + VERSION);
        
        configManager = new ConfigManager();
        configManager.loadConfig();
        
        guiManager = new GuiManager();
        
        // Initialize global hotkeys
        keyBindManager = new KeyBindManager();
        
        System.out.println(NAME + " initialized successfully!");
        System.out.println("Press Right Shift to toggle GUI");
    }
    
    /**
     * Static initialization for patched Minecraft
     * Called from MinecraftClient constructor
     */
    public static void initialize() {
        try {
            ArizonClient client = new ArizonClient();
            client.init();
            System.out.println("[SHAKEDOWN] Client injected and initialized!");
        } catch (Exception e) {
            System.err.println("[SHAKEDOWN] Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Called every game tick
     * Injected into MinecraftClient.tick()
     */
    public static void onTick() {
        try {
            if (instance != null) {
                // Update modules, GUI, etc
                // KeyBindManager works through global key dispatcher, no tick needed
            }
        } catch (Exception e) {
            // Silent fail to not crash game
        }
    }
    
    /**
     * Shutdown the client
     */
    public void shutdown() {
        configManager.saveConfig();
        if (keyBindManager != null) {
            keyBindManager.unregister();
        }
        System.out.println(NAME + " shutdown complete.");
    }
    
    public static ArizonClient getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public GuiManager getGuiManager() {
        return guiManager;
    }
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getName() {
        return NAME;
    }
    
    // Main method for testing
    public static void main(String[] args) {
        ArizonClient client = new ArizonClient();
        client.init();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.shutdown();
        }));
        
        // Keep application running
        System.out.println("Arizon Client is running. Press Right Shift to open GUI.");
        System.out.println("Press Ctrl+C to exit.");
    }
}

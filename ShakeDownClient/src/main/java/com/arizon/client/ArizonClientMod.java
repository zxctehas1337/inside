package com.arizon.client;

import com.arizon.client.config.ConfigManager;
import com.arizon.client.gui.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Arizon Client - Fabric Mod Entry Point
 */
public class ArizonClientMod implements ClientModInitializer {
    
    public static final String MOD_ID = "arizon-client";
    public static final String MOD_NAME = "Arizon Client";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    
    private static ArizonClientMod instance;
    private ConfigManager configManager;
    private HudRenderer hudRenderer;
    private com.arizon.client.module.ModuleManager moduleManager;
    private com.arizon.client.command.CommandManager commandManager;
    
    // Keybindings
    private static KeyBinding openGuiKey;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        
        System.out.println("========================================");
        System.out.println("SHAKEDOWN CLIENT IS LOADING!");
        System.out.println("========================================");
        LOGGER.info("Initializing {} v{}", MOD_NAME, VERSION);
        
        // Initialize built-in optimization engine (ALWAYS ACTIVE)
        com.arizon.client.optimization.OptimizationEngine.initialize();
        
        // Initialize config
        configManager = new ConfigManager();
        configManager.loadConfig();
        
        // Initialize HUD renderer
        hudRenderer = new HudRenderer();
        
        // Initialize module manager
        moduleManager = new com.arizon.client.module.ModuleManager();
        moduleManager.loadModules(); // Load saved module states
        
        // Initialize command manager
        commandManager = new com.arizon.client.command.CommandManager();
        
        // Load keybinds
        com.arizon.client.keybind.KeyBindSystem.load();
        
        // Register keybindings
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.arizon-client.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.arizon-client"
        ));
        
        // Register HUD rendering
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            hudRenderer.render(drawContext, tickDelta);
        });
        
        // Register tick event for keybinding and module updates
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new com.arizon.client.gui.screen.UltraPremiumGui());
                } else {
                    client.setScreen(null);
                }
            }
            
            // Update modules
            if (client.player != null) {
                moduleManager.onUpdate();
            }
        });
        
        // Save modules on world unload
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(c -> {
            moduleManager.saveModules();
            configManager.saveConfig();
            com.arizon.client.keybind.KeyBindSystem.save();
            // Remove fake player
            com.arizon.client.fakeplayer.FakePlayer.remove();
        });
        
        LOGGER.info("{} initialized successfully!", MOD_NAME);
        LOGGER.info("Press RIGHT SHIFT to open GUI");
        LOGGER.info("HUD should be visible in top-left corner");
    }
    
    public static ArizonClientMod getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public HudRenderer getHudRenderer() {
        return hudRenderer;
    }
    
    public com.arizon.client.module.ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public com.arizon.client.command.CommandManager getCommandManager() {
        return commandManager;
    }
}

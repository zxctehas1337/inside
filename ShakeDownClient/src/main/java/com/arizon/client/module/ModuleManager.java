package com.arizon.client.module;

import com.arizon.client.module.modules.combat.*;
import com.arizon.client.module.modules.render.*;
import com.arizon.client.module.modules.movement.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all modules
 */
public class ModuleManager {
    
    private static ModuleManager instance;
    private final List<Module> modules = new ArrayList<>();
    
    public ModuleManager() {
        instance = this;
        initializeModules();
    }
    
    private void initializeModules() {
        // Combat modules
        modules.add(new com.arizon.client.module.modules.combat.Aura());
        modules.add(new Triggerbot());
        
        // Render modules
        modules.add(new ESP());
        modules.add(new BlockESP());
        modules.add(new com.arizon.client.module.modules.render.JumpCircles());
        modules.add(new com.arizon.client.module.modules.render.ChinaHat());
        modules.add(new Tracers());
        modules.add(new com.arizon.client.module.modules.render.Removals());
        modules.add(new com.arizon.client.module.modules.render.SwingAnimations());
        
        // Movement modules
        modules.add(new Sprint());
        modules.add(new Speed());
        modules.add(new Fly());
        modules.add(new NoFall());
        modules.add(new com.arizon.client.module.modules.movement.GuiMove());
        
        // Player modules
        modules.add(new com.arizon.client.module.modules.player.AutoArmor());
        modules.add(new com.arizon.client.module.modules.player.AutoTool());
        
        // World modules
        modules.add(new com.arizon.client.module.modules.world.Scaffold());
        modules.add(new com.arizon.client.module.modules.world.Nuker());
    }
    
    public void onUpdate() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onUpdate();
            }
        }
    }
    
    public void saveModules() {
        // Save module states to config
        com.arizon.client.ArizonClientMod.getInstance().getConfigManager().saveModules(modules);
    }
    
    public void loadModules() {
        // Load module states from config
        com.arizon.client.ArizonClientMod.getInstance().getConfigManager().loadModules(modules);
    }
    
    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    
    public List<Module> getModules() {
        return modules;
    }
    
    public static ModuleManager getInstance() {
        return instance;
    }
}

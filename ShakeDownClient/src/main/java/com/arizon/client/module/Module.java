package com.arizon.client.module;

/**
 * Base module class
 */
public abstract class Module {
    
    protected String name;
    protected boolean enabled;
    
    public Module(String name) {
        this.name = name;
        this.enabled = false;
    }
    
    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            try {
                onEnable();
            } catch (Exception e) {
                System.err.println("Error enabling module " + name + ": " + e.getMessage());
                enabled = false;
            }
        } else {
            try {
                onDisable();
            } catch (Exception e) {
                System.err.println("Error disabling module " + name + ": " + e.getMessage());
            }
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            try {
                onEnable();
            } catch (Exception e) {
                System.err.println("Error enabling module " + name + ": " + e.getMessage());
                this.enabled = false;
            }
        } else {
            try {
                onDisable();
            } catch (Exception e) {
                System.err.println("Error disabling module " + name + ": " + e.getMessage());
            }
        }
    }
    
    protected void sendMessage(String message) {
        // Messages disabled - no spam from modules
    }
    
    public String getDescription() {
        return "No description available";
    }
    
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onUpdate();
    
    public String getName() {
        return name;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean hasSettings() {
        return false; // Override in modules with settings
    }
}

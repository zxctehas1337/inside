package net.minecraft.client;

import net.minecraft.client.gui.screen.Screen;

public class MinecraftClient {
    public static MinecraftClient instance;
    public Screen currentScreen;
    public PlayerStub player;
    
    public static MinecraftClient getInstance() {
        return instance;
    }
    
    public void setScreen(Screen screen) {
        this.currentScreen = screen;
    }
    
    public WindowStub getWindow() {
        return new WindowStub();
    }
    
    public static class WindowStub {
        public long getHandle() {
            return 0L;
        }
    }
    
    public static class PlayerStub {
        public void setSprinting(boolean sprinting) {}
        public void sendMessage(Object message) {}
    }
}

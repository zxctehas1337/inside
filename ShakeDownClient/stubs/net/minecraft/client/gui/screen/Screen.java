package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Screen {
    public int width;
    public int height;
    public Object textRenderer;
    
    public Screen(Text title) {}
    
    protected void init() {}
    
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {}
    
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}
    
    public boolean shouldPause() {
        return true;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    public void close() {}
    
    protected void addDrawableChild(Object widget) {}
}

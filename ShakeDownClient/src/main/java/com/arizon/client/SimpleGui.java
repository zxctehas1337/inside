package com.arizon.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Simple GUI that works without Fabric API
 */
public class SimpleGui extends Screen {
    
    public SimpleGui() {
        super(Text.literal("Arizon Client"));
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Draw title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            "Arizon Client - Patched Version",
            this.width / 2,
            20,
            0xFFFFFF
        );
        
        // Draw info
        context.drawTextWithShadow(
            this.textRenderer,
            "Press ESC to close",
            10,
            this.height - 20,
            0xAAAAAA
        );
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}

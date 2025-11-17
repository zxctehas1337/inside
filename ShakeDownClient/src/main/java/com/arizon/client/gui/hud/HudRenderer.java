package com.arizon.client.gui.hud;

import com.arizon.client.ArizonClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Renders HUD elements on screen
 */
public class HudRenderer {
    
    private final MinecraftClient client;
    
    public HudRenderer() {
        this.client = MinecraftClient.getInstance();
    }
    
    public void render(DrawContext context, float tickDelta) {
        if (client.options.hudHidden) {
            System.out.println("[Arizon Client] HUD hidden by F1");
            return;
        }
        
        MatrixStack matrices = context.getMatrices();
        
        // Render logo
        renderLogo(context, matrices);
        
        // Render FPS
        renderFps(context, matrices);
        
        // Render coordinates
        renderCoordinates(context, matrices);
        
        // Render Target HUD
        renderTargetHUD(context, matrices);
    }
    
    private void renderTargetHUD(DrawContext context, MatrixStack matrices) {
        com.arizon.client.module.Module auraModule = com.arizon.client.ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Aura");
        
        if (auraModule == null || !auraModule.isEnabled()) return;
        
        com.arizon.client.module.modules.combat.Aura aura = 
            (com.arizon.client.module.modules.combat.Aura) auraModule;
        
        if (!aura.showTargetHUD || aura.currentTarget == null) return;
        
        int x = client.getWindow().getScaledWidth() / 2 + 20;
        int y = client.getWindow().getScaledHeight() / 2 - 30;
        int width = 150;
        int height = 50;
        
        // Background
        context.fill(x, y, x + width, y + height, 0xC0000000);
        
        // Border (orange)
        context.fill(x, y, x + width, y + 2, 0xFFFF8C42);
        context.fill(x, y + height - 2, x + width, y + height, 0xFFFF8C42);
        context.fill(x, y, x + 2, y + height, 0xFFFF8C42);
        context.fill(x + width - 2, y, x + width, y + height, 0xFFFF8C42);
        
        // Target name
        String name = aura.currentTarget.getName().getString();
        context.drawText(client.textRenderer, name, x + 10, y + 10, 0xFFFFFFFF, true);
        
        // Health bar
        float health = aura.currentTarget.getHealth();
        float maxHealth = aura.currentTarget.getMaxHealth();
        float healthPercent = health / maxHealth;
        
        int barX = x + 10;
        int barY = y + 25;
        int barWidth = width - 20;
        int barHeight = 8;
        
        // Health bar background
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF404040);
        
        // Health bar fill
        int fillWidth = (int)(barWidth * healthPercent);
        int healthColor = healthPercent > 0.5f ? 0xFF22C55E : 
                         healthPercent > 0.25f ? 0xFFFBBF24 : 0xFFEF4444;
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, healthColor);
        
        // Health text
        String healthText = String.format("%.1f / %.1f", health, maxHealth);
        context.drawText(client.textRenderer, healthText, x + 10, y + 37, 0xFFFFFFFF, false);
    }
    
    private void renderLogo(DrawContext context, MatrixStack matrices) {
        String text = ArizonClientMod.MOD_NAME;
        int x = 10;
        int y = 10;
        
        // Background
        context.fill(x - 2, y - 2, x + client.textRenderer.getWidth(text) + 2, 
                    y + client.textRenderer.fontHeight + 2, 0x80000000);
        
        // Text with gradient effect (purple)
        context.drawText(client.textRenderer, text, x, y, 0xFF7C5BFF, true);
    }
    
    private void renderFps(DrawContext context, MatrixStack matrices) {
        String fps = "FPS: " + client.getCurrentFps();
        int x = 10;
        int y = 25;
        
        context.fill(x - 2, y - 2, x + client.textRenderer.getWidth(fps) + 2,
                    y + client.textRenderer.fontHeight + 2, 0x80000000);
        
        int color = client.getCurrentFps() >= 60 ? 0xFF22C55E : 
                   client.getCurrentFps() >= 30 ? 0xFFFBBF24 : 0xFFEF4444;
        
        context.drawText(client.textRenderer, fps, x, y, color, true);
    }
    
    private void renderCoordinates(DrawContext context, MatrixStack matrices) {
        if (client.player == null) return;
        
        int x = (int) client.player.getX();
        int y = (int) client.player.getY();
        int z = (int) client.player.getZ();
        
        String coords = String.format("XYZ: %d / %d / %d", x, y, z);
        int posX = 10;
        int posY = 40;
        
        context.fill(posX - 2, posY - 2, posX + client.textRenderer.getWidth(coords) + 2,
                    posY + client.textRenderer.fontHeight + 2, 0x80000000);
        
        context.drawText(client.textRenderer, coords, posX, posY, 0xFFFFFFFF, true);
    }
}

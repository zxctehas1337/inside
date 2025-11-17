package com.arizon.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    
    @Unique
    private float arizon$slideAnimation = 0.0f;
    
    @Unique
    private long arizon$lastFrameTime = System.currentTimeMillis();
    
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        arizon$slideAnimation = 0.0f;
        arizon$lastFrameTime = System.currentTimeMillis();
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderStart(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen self = (Screen)(Object)this;
        
        // Skip animation for our custom GUI (it has its own)
        if (self instanceof com.arizon.client.gui.screen.UltraPremiumGui) {
            return;
        }
        
        // Update animation
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - arizon$lastFrameTime) / 1000.0f;
        arizon$lastFrameTime = currentTime;
        
        if (arizon$slideAnimation < 1.0f) {
            arizon$slideAnimation = Math.min(1.0f, arizon$slideAnimation + deltaTime * 4.0f);
        }
        
        // Apply slide animation
        float easedProgress = 1 - (float)Math.pow(1 - arizon$slideAnimation, 3);
        int slideOffset = (int)((1.0f - easedProgress) * self.height);
        
        context.getMatrices().push();
        context.getMatrices().translate(0, slideOffset, 0);
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderEnd(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen self = (Screen)(Object)this;
        
        // Skip for custom GUI
        if (self instanceof com.arizon.client.gui.screen.UltraPremiumGui) {
            return;
        }
        
        context.getMatrices().pop();
    }
}

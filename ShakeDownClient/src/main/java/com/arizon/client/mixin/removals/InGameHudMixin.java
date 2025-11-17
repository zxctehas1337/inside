package com.arizon.client.mixin.removals;

import com.arizon.client.ArizonClientMod;
import com.arizon.client.module.modules.render.Removals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remove HUD elements
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboard(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        Removals removals = (Removals) ArizonClientMod.getInstance()
            .getModuleManager().getModuleByName("Removals");
        
        if (removals != null && removals.isEnabled() && removals.removeScoreboard) {
            ci.cancel();
        }
    }
}

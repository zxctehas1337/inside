package com.arizon.client.gui.animation;

/**
 * Animates between two colors
 */
public class ColorAnimation {
    
    private final Animation animation;
    
    public ColorAnimation(float speed) {
        this.animation = new Animation(speed, Animation.AnimationType.EASE_OUT);
    }
    
    public void update(float delta) {
        animation.update(delta);
    }
    
    public void setTarget(float target) {
        animation.setTarget(target);
    }
    
    public int getColor(int colorFrom, int colorTo) {
        float progress = animation.getValue();
        
        int aFrom = (colorFrom >> 24) & 0xFF;
        int rFrom = (colorFrom >> 16) & 0xFF;
        int gFrom = (colorFrom >> 8) & 0xFF;
        int bFrom = colorFrom & 0xFF;
        
        int aTo = (colorTo >> 24) & 0xFF;
        int rTo = (colorTo >> 16) & 0xFF;
        int gTo = (colorTo >> 8) & 0xFF;
        int bTo = colorTo & 0xFF;
        
        int a = (int) (aFrom + (aTo - aFrom) * progress);
        int r = (int) (rFrom + (rTo - rFrom) * progress);
        int g = (int) (gFrom + (gTo - gFrom) * progress);
        int b = (int) (bFrom + (bTo - bFrom) * progress);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}

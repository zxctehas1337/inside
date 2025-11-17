package com.arizon.client.gui.animation;

/**
 * Smooth animation system
 */
public class Animation {
    
    private float value;
    private float target;
    private final float speed;
    private final AnimationType type;
    
    public Animation(float speed, AnimationType type) {
        this.speed = speed;
        this.type = type;
        this.value = 0;
        this.target = 0;
    }
    
    public void update(float delta) {
        if (value != target) {
            float diff = target - value;
            float change = diff * speed * delta;
            
            if (Math.abs(diff) < 0.01f) {
                value = target;
            } else {
                value += change;
            }
        }
    }
    
    public void setTarget(float target) {
        this.target = Math.max(0, Math.min(1, target));
    }
    
    public float getValue() {
        return applyEasing(value);
    }
    
    public float getRawValue() {
        return value;
    }
    
    public boolean isFinished() {
        return value == target;
    }
    
    private float applyEasing(float t) {
        switch (type) {
            case EASE_IN:
                return t * t;
            case EASE_OUT:
                return t * (2 - t);
            case EASE_IN_OUT:
                return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
            case BOUNCE:
                if (t < 0.5f) {
                    return 8 * t * t * t * t;
                } else {
                    float f = (t - 1);
                    return 1 - 8 * f * f * f * f;
                }
            case LINEAR:
            default:
                return t;
        }
    }
    
    public enum AnimationType {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        BOUNCE
    }
}

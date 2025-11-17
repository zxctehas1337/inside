package com.arizon.client.gui.effects;

import com.arizon.client.gui.theme.NeonTheme;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Particle system for GUI background
 */
public class ParticleSystem {
    
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private int width, height;
    
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        particles.clear();
        
        // Create initial particles
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle(
                random.nextInt(width),
                random.nextInt(height),
                random.nextFloat() * 2 - 1,
                random.nextFloat() * 2 - 1
            ));
        }
    }
    
    public void update(float delta) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(delta);
            
            // Remove particles that are out of bounds
            if (particle.x < -10 || particle.x > width + 10 ||
                particle.y < -10 || particle.y > height + 10) {
                iterator.remove();
            }
        }
        
        // Add new particles
        while (particles.size() < 50) {
            int side = random.nextInt(4);
            int x, y;
            
            switch (side) {
                case 0: // Top
                    x = random.nextInt(width);
                    y = -5;
                    break;
                case 1: // Right
                    x = width + 5;
                    y = random.nextInt(height);
                    break;
                case 2: // Bottom
                    x = random.nextInt(width);
                    y = height + 5;
                    break;
                default: // Left
                    x = -5;
                    y = random.nextInt(height);
                    break;
            }
            
            particles.add(new Particle(x, y,
                random.nextFloat() * 2 - 1,
                random.nextFloat() * 2 - 1));
        }
    }
    
    public void render(DrawContext context) {
        for (Particle particle : particles) {
            particle.render(context);
        }
        
        // Draw connections between nearby particles
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                
                float dx = p2.x - p1.x;
                float dy = p2.y - p1.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                
                if (distance < 100) {
                    float alpha = (1 - distance / 100) * 0.2f;
                    int color = (int)(alpha * 255) << 24 | (NeonTheme.ACCENT_CYAN & 0x00FFFFFF);
                    
                    context.drawHorizontalLine((int)p1.x, (int)p2.x, (int)p1.y, color);
                }
            }
        }
    }
    
    private static class Particle {
        float x, y;
        float vx, vy;
        float size;
        float alpha;
        
        Particle(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx * 20;
            this.vy = vy * 20;
            this.size = 2 + new Random().nextFloat() * 2;
            this.alpha = 0.3f + new Random().nextFloat() * 0.4f;
        }
        
        void update(float delta) {
            x += vx * delta;
            y += vy * delta;
        }
        
        void render(DrawContext context) {
            int color = (int)(alpha * 255) << 24 | (NeonTheme.ACCENT_CYAN & 0x00FFFFFF);
            context.fill((int)x, (int)y, (int)(x + size), (int)(y + size), color);
        }
    }
}

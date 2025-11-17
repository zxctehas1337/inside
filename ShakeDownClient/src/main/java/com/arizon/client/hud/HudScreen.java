package com.arizon.client.hud;

import com.arizon.client.ArizonClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * HUD overlay screen
 */
public class HudScreen extends JFrame {
    
    private HudManager hudManager;
    private JPanel renderPanel;
    
    public HudScreen() {
        this.hudManager = new HudManager();
        initializeFrame();
    }
    
    private void initializeFrame() {
        setTitle("Arizon Client - HUD Preview");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Enable anti-aliasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Dark background
                g2d.setColor(new Color(20, 20, 20));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Render HUD
                hudManager.render(g2d);
                
                // Instructions
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawString("Press 'E' to toggle Edit Mode", 10, getHeight() - 20);
            }
        };
        
        renderPanel.setBackground(new Color(20, 20, 20));
        
        // Mouse listeners for dragging
        renderPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                hudManager.handleMousePressed(e.getX(), e.getY());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                hudManager.handleMouseReleased();
            }
        });
        
        renderPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                hudManager.handleMouseDragged(e.getX(), e.getY());
                renderPanel.repaint();
            }
        });
        
        // Keyboard listener
        renderPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    hudManager.toggleEditMode();
                    renderPanel.repaint();
                }
            }
        });
        
        renderPanel.setFocusable(true);
        renderPanel.requestFocusInWindow();
        
        add(renderPanel);
        
        // Animation timer
        Timer timer = new Timer(16, e -> renderPanel.repaint());
        timer.start();
    }
    
    public HudManager getHudManager() {
        return hudManager;
    }
}

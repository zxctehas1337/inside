package com.arizon.client.input;

import com.arizon.client.ArizonClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Manages keyboard shortcuts
 * Uses system tray for background operation
 */
public class KeyBindManager {
    
    private TrayIcon trayIcon;
    
    public KeyBindManager() {
        initializeTray();
    }
    
    private void initializeTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported. GUI will open automatically.");
            ArizonClient.getInstance().getGuiManager().openGui();
            return;
        }
        
        try {
            SystemTray tray = SystemTray.getSystemTray();
            
            // Create tray icon image (simple colored square)
            Image image = createTrayImage();
            
            PopupMenu popup = new PopupMenu();
            
            MenuItem openItem = new MenuItem("Open GUI (Right Shift)");
            openItem.addActionListener(e -> {
                ArizonClient.getInstance().getGuiManager().openGui();
            });
            
            MenuItem hudItem = new MenuItem("Open HUD Editor");
            hudItem.addActionListener(e -> {
                ArizonClient.getInstance().getGuiManager().openHudEditor();
            });
            
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> {
                System.exit(0);
            });
            
            popup.add(openItem);
            popup.add(hudItem);
            popup.addSeparator();
            popup.add(exitItem);
            
            trayIcon = new TrayIcon(image, "Arizon Client", popup);
            trayIcon.setImageAutoSize(true);
            
            trayIcon.addActionListener(e -> {
                ArizonClient.getInstance().getGuiManager().toggleGui();
            });
            
            tray.add(trayIcon);
            
            System.out.println("Arizon Client running in system tray");
            System.out.println("Click tray icon or use Right Shift in GUI windows to toggle");
            
            // Register global key dispatcher
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new GlobalKeyDispatcher());
            
        } catch (Exception e) {
            System.err.println("Failed to create system tray: " + e.getMessage());
            ArizonClient.getInstance().getGuiManager().openGui();
        }
    }
    
    private Image createTrayImage() {
        int size = 16;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Draw purple square
        g.setColor(new Color(124, 91, 255));
        g.fillRect(0, 0, size, size);
        
        g.dispose();
        return image;
    }
    
    public void unregister() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }
    
    /**
     * Global key dispatcher for Right Shift
     */
    private class GlobalKeyDispatcher implements KeyEventDispatcher {
        private boolean rightShiftPressed = false;
        
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT && 
                e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
                
                if (e.getID() == KeyEvent.KEY_PRESSED && !rightShiftPressed) {
                    rightShiftPressed = true;
                    SwingUtilities.invokeLater(() -> {
                        ArizonClient.getInstance().getGuiManager().toggleGui();
                    });
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    rightShiftPressed = false;
                }
            }
            return false;
        }
    }
}

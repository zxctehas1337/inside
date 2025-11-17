package com.arizon.client.gui;

import com.arizon.client.gui.screens.MainScreen;
import com.arizon.client.hud.HudScreen;

import javax.swing.*;

/**
 * Manages GUI screens
 */
public class GuiManager {
    
    private MainScreen mainScreen;
    private HudScreen hudScreen;
    private boolean isGuiOpen = false;
    
    public GuiManager() {
        initializeGui();
    }
    
    private void initializeGui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mainScreen = new MainScreen();
        hudScreen = new HudScreen();
    }
    
    public void openGui() {
        if (!isGuiOpen) {
            mainScreen.setVisible(true);
            isGuiOpen = true;
        }
    }
    
    public void closeGui() {
        if (isGuiOpen) {
            mainScreen.setVisible(false);
            isGuiOpen = false;
        }
    }
    
    public void toggleGui() {
        if (isGuiOpen) {
            closeGui();
        } else {
            openGui();
        }
    }
    
    public void openHudEditor() {
        hudScreen.setVisible(true);
    }
    
    public boolean isGuiOpen() {
        return isGuiOpen;
    }
    
    public HudScreen getHudScreen() {
        return hudScreen;
    }
}

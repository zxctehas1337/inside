package com.arizon.client.gui.screens;

import com.arizon.client.ArizonClient;
import com.arizon.client.gui.theme.ModernTheme;
import com.arizon.client.gui.tabs.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI screen with modern design
 */
public class MainScreen extends JFrame {
    
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 650;
    
    private JPanel contentPanel;
    private JPanel tabButtonPanel;
    private JPanel displayPanel;
    
    private String currentTab = "Visuals";
    
    public MainScreen() {
        initializeFrame();
        initializeComponents();
    }
    
    private void initializeFrame() {
        setTitle(ArizonClient.getName() + " v" + ArizonClient.getVersion());
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        
        // Rounded corners effect
        setBackground(new Color(0, 0, 0, 0));
        
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void initializeComponents() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(ModernTheme.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header with title and close button
        JPanel headerPanel = createHeader();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tab buttons
        tabButtonPanel = createTabButtons();
        contentPanel.add(tabButtonPanel, BorderLayout.WEST);
        
        // Display area
        displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(ModernTheme.BACKGROUND);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        switchTab("Visuals");
        
        contentPanel.add(displayPanel, BorderLayout.CENTER);
        
        add(contentPanel);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(ArizonClient.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        closeButton.setForeground(ModernTheme.TEXT_SECONDARY);
        closeButton.setBackground(ModernTheme.BACKGROUND);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> setVisible(false));
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createTabButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        panel.setPreferredSize(new Dimension(150, 0));
        
        String[] tabs = {"Visuals", "HUD", "Settings", "Misc", "About"};
        
        for (String tab : tabs) {
            JButton button = createTabButton(tab);
            panel.add(button);
            panel.add(Box.createVerticalStrut(10));
        }
        
        return panel;
    }
    
    private JButton createTabButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setForeground(ModernTheme.TEXT_SECONDARY);
        button.setBackground(ModernTheme.BACKGROUND);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 45));
        
        button.addActionListener(e -> switchTab(text));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!text.equals(currentTab)) {
                    button.setBackground(ModernTheme.HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!text.equals(currentTab)) {
                    button.setBackground(ModernTheme.BACKGROUND);
                }
            }
        });
        
        return button;
    }
    
    private void switchTab(String tabName) {
        currentTab = tabName;
        
        // Update button colors
        Component[] buttons = tabButtonPanel.getComponents();
        for (Component comp : buttons) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equals(tabName)) {
                    btn.setBackground(ModernTheme.ACCENT);
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(ModernTheme.BACKGROUND);
                    btn.setForeground(ModernTheme.TEXT_SECONDARY);
                }
            }
        }
        
        // Switch content
        displayPanel.removeAll();
        
        JPanel newContent = null;
        switch (tabName) {
            case "Visuals":
                newContent = new VisualsTab();
                break;
            case "HUD":
                newContent = new HudTab();
                break;
            case "Settings":
                newContent = new SettingsTab();
                break;
            case "Misc":
                newContent = new MiscTab();
                break;
            case "About":
                newContent = new AboutTab();
                break;
        }
        
        if (newContent != null) {
            displayPanel.add(newContent, BorderLayout.CENTER);
        }
        
        displayPanel.revalidate();
        displayPanel.repaint();
    }
}

package com.arizon.client.gui.tabs;

import com.arizon.client.ArizonClient;
import com.arizon.client.gui.theme.ModernTheme;
import com.arizon.client.gui.components.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

/**
 * About tab - Client information
 */
public class AboutTab extends JPanel {
    
    private static final String GITHUB_URL = "https://github.com/arizon/arizon-client";
    private static final String AUTHOR = "Arizon Team";
    
    public AboutTab() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BACKGROUND);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.BACKGROUND);
        
        // Title
        JLabel title = new JLabel(ArizonClient.getName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(ModernTheme.ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Version
        JLabel versionLabel = new JLabel("Version " + ArizonClient.getVersion());
        versionLabel.setFont(ModernTheme.FONT_BODY);
        versionLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(versionLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Author
        JLabel authorLabel = new JLabel("Created by " + AUTHOR);
        authorLabel.setFont(ModernTheme.FONT_BODY);
        authorLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(authorLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Description
        JTextArea descArea = new JTextArea(
            "Arizon Client is a custom Minecraft client with advanced visual features,\n" +
            "performance optimizations, and a modern user interface.\n\n" +
            "Features:\n" +
            "• Custom time and weather control\n" +
            "• Advanced visual effects\n" +
            "• Customizable HUD system\n" +
            "• Performance optimization\n" +
            "• Modern GUI design\n" +
            "• Configuration management\n\n" +
            "Hotkeys:\n" +
            "• Right Shift - Toggle GUI"
        );
        descArea.setFont(ModernTheme.FONT_BODY);
        descArea.setForeground(ModernTheme.TEXT_SECONDARY);
        descArea.setBackground(ModernTheme.BACKGROUND);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descArea);
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(ModernTheme.BACKGROUND);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton githubButton = new ModernButton("Open GitHub");
        githubButton.addActionListener(e -> openURL(GITHUB_URL));
        buttonPanel.add(githubButton);
        
        ModernButton updateButton = new ModernButton("Check for Updates");
        updateButton.addActionListener(e -> checkUpdates());
        buttonPanel.add(updateButton);
        
        mainPanel.add(buttonPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Could not open browser. Please visit:\n" + url, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkUpdates() {
        JOptionPane.showMessageDialog(this, 
            "You are running the latest version!\n\nVersion: " + ArizonClient.getVersion(), 
            "Update Check", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

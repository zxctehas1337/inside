package com.arizon.client.gui.tabs;

import com.arizon.client.ArizonClient;
import com.arizon.client.gui.theme.ModernTheme;
import com.arizon.client.gui.components.*;
import com.arizon.client.hud.HudElement;
import com.arizon.client.hud.elements.VisualAuraElement;

import javax.swing.*;
import java.awt.*;

/**
 * HUD configuration tab
 */
public class HudTab extends JPanel {
    
    public HudTab() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BACKGROUND);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.BACKGROUND);
        
        // Title
        JLabel title = new JLabel("HUD Settings");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Open HUD Editor button
        ModernButton editorButton = new ModernButton("Open HUD Editor");
        editorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        editorButton.addActionListener(e -> {
            ArizonClient.getInstance().getGuiManager().openHudEditor();
        });
        mainPanel.add(editorButton);
        mainPanel.add(Box.createVerticalStrut(12));
        
        // Description
        JTextArea descArea = new JTextArea(
            "The HUD Editor allows you to customize the position of all HUD elements.\n\n" +
            "Features:\n" +
            "• Drag and drop elements to reposition them\n" +
            "• Press 'E' to toggle Edit Mode\n" +
            "• All positions are saved automatically\n\n" +
            "Available HUD Elements:\n" +
            "• Client Logo\n" +
            "• FPS Counter\n" +
            "• Coordinates Display\n" +
            "• Item Counter\n" +
            "• Armor Display\n" +
            "• Visual Aura Effect (display only)"
        );
        descArea.setFont(ModernTheme.FONT_BODY);
        descArea.setForeground(ModernTheme.TEXT_SECONDARY);
        descArea.setBackground(ModernTheme.BACKGROUND);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descArea);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Visual Aura Settings
        mainPanel.add(createSection("Visual Aura Settings"));
        mainPanel.add(Box.createVerticalStrut(6));
        
        JLabel auraInfo = new JLabel("⚠ Visual effect only - no actual attacks");
        auraInfo.setFont(ModernTheme.FONT_BODY);
        auraInfo.setForeground(ModernTheme.WARNING);
        auraInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(auraInfo);
        mainPanel.add(Box.createVerticalStrut(6));
        
        // Aura radius slider
        JLabel radiusLabel = new JLabel("Display Radius");
        radiusLabel.setFont(ModernTheme.FONT_BODY);
        radiusLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        radiusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(radiusLabel);
        mainPanel.add(Box.createVerticalStrut(4));
        
        ModernSlider radiusSlider = new ModernSlider(20, 70, 35);
        radiusSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        radiusSlider.addChangeListener(e -> {
            float radius = radiusSlider.getValue() / 10.0f;
            VisualAuraElement auraElement = (VisualAuraElement) ArizonClient.getInstance()
                .getGuiManager().getHudScreen().getHudManager().getElementByName("Visual Aura");
            if (auraElement != null) {
                auraElement.setRadius(radius);
            }
        });
        mainPanel.add(radiusSlider);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ModernTheme.BACKGROUND);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JLabel createSection(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernTheme.FONT_HEADING);
        label.setForeground(ModernTheme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}

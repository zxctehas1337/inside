package com.arizon.client.gui.tabs;

import com.arizon.client.ArizonClient;
import com.arizon.client.config.ClientConfig;
import com.arizon.client.gui.theme.ModernTheme;
import com.arizon.client.gui.components.*;

import javax.swing.*;
import java.awt.*;

/**
 * Visuals tab - Time, Weather, Effects
 */
public class VisualsTab extends JPanel {
    
    private ClientConfig config;
    
    public VisualsTab() {
        this.config = ArizonClient.getInstance().getConfigManager().getConfig();
        
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BACKGROUND);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.BACKGROUND);
        
        // Title
        JLabel title = new JLabel("Visual Settings");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Time of Day slider
        mainPanel.add(createSection("Time of Day"));
        ModernSlider timeSlider = new ModernSlider(0, 24, (int)config.timeOfDay);
        timeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeSlider.addChangeListener(e -> {
            config.timeOfDay = timeSlider.getValue();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(timeSlider);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Weather dropdown
        mainPanel.add(createSection("Weather"));
        String[] weatherOptions = {"Clear", "Rain", "Thunder", "Snow"};
        ModernComboBox weatherCombo = new ModernComboBox(weatherOptions);
        weatherCombo.setSelectedItem(config.weather);
        weatherCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        weatherCombo.addActionListener(e -> {
            config.weather = (String) weatherCombo.getSelectedItem();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(weatherCombo);
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Effects section
        mainPanel.add(createSection("Visual Effects"));
        mainPanel.add(Box.createVerticalStrut(10));
        
        ModernCheckBox globalFiltersCheck = new ModernCheckBox("Global Filters", config.globalFilters);
        globalFiltersCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        globalFiltersCheck.addActionListener(e -> {
            config.globalFilters = globalFiltersCheck.isSelected();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(globalFiltersCheck);
        mainPanel.add(Box.createVerticalStrut(10));
        
        ModernCheckBox antiAliasingCheck = new ModernCheckBox("Anti-Aliasing", config.antiAliasing);
        antiAliasingCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        antiAliasingCheck.addActionListener(e -> {
            config.antiAliasing = antiAliasingCheck.isSelected();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(antiAliasingCheck);
        mainPanel.add(Box.createVerticalStrut(10));
        
        ModernCheckBox outlineCheck = new ModernCheckBox("Object Outline", config.objectOutline);
        outlineCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        outlineCheck.addActionListener(e -> {
            config.objectOutline = outlineCheck.isSelected();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(outlineCheck);
        mainPanel.add(Box.createVerticalStrut(10));
        
        ModernCheckBox bloomCheck = new ModernCheckBox("Bloom Effect", config.bloom);
        bloomCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloomCheck.addActionListener(e -> {
            config.bloom = bloomCheck.isSelected();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(bloomCheck);
        mainPanel.add(Box.createVerticalStrut(10));
        
        ModernCheckBox shadowsCheck = new ModernCheckBox("Enhanced Shadows", config.shadows);
        shadowsCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        shadowsCheck.addActionListener(e -> {
            config.shadows = shadowsCheck.isSelected();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(shadowsCheck);
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Render Modules section
        mainPanel.add(createSection("Render Modules"));
        mainPanel.add(Box.createVerticalStrut(10));
        
        // ChinaHat
        ModernCheckBox chinaHatCheck = new ModernCheckBox("China Hat", false);
        chinaHatCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        chinaHatCheck.addActionListener(e -> {
            com.arizon.client.module.Module chinaHat = com.arizon.client.module.ModuleManager.getInstance().getModuleByName("ChinaHat");
            if (chinaHat != null) {
                chinaHat.setEnabled(chinaHatCheck.isSelected());
            }
        });
        mainPanel.add(chinaHatCheck);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // JumpCircles
        ModernCheckBox jumpCirclesCheck = new ModernCheckBox("Jump Circles", false);
        jumpCirclesCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        jumpCirclesCheck.addActionListener(e -> {
            com.arizon.client.module.Module jumpCircles = com.arizon.client.module.ModuleManager.getInstance().getModuleByName("JumpCircles");
            if (jumpCircles != null) {
                jumpCircles.setEnabled(jumpCirclesCheck.isSelected());
            }
        });
        mainPanel.add(jumpCirclesCheck);
        
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

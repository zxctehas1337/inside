package com.arizon.client.gui.tabs;

import com.arizon.client.ArizonClient;
import com.arizon.client.config.ClientConfig;
import com.arizon.client.gui.theme.ModernTheme;
import com.arizon.client.gui.components.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Settings tab - General options
 */
public class SettingsTab extends JPanel {
    
    private ClientConfig config;
    
    public SettingsTab() {
        this.config = ArizonClient.getInstance().getConfigManager().getConfig();
        
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BACKGROUND);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.BACKGROUND);
        
        // Title
        JLabel title = new JLabel("General Settings");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Auto-load
        ModernCheckBox autoLoadCheck = new ModernCheckBox("Auto-load on startup", config.autoLoad);
        autoLoadCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        autoLoadCheck.addActionListener(e -> {
            config.autoLoad = autoLoadCheck.isSelected();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(autoLoadCheck);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // GUI Scale
        mainPanel.add(createSection("GUI Scale"));
        ModernSlider scaleSlider = new ModernSlider(75, 150, (int)(config.guiScale * 100));
        scaleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        scaleSlider.addChangeListener(e -> {
            config.guiScale = scaleSlider.getValue() / 100.0f;
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(scaleSlider);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // FPS Limit
        mainPanel.add(createSection("FPS Limit"));
        ModernSlider fpsSlider = new ModernSlider(30, 240, config.fpsLimit);
        fpsSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        fpsSlider.addChangeListener(e -> {
            config.fpsLimit = fpsSlider.getValue();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(fpsSlider);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // GUI Sensitivity
        mainPanel.add(createSection("GUI Sensitivity"));
        ModernSlider sensitivitySlider = new ModernSlider(50, 200, (int)(config.guiSensitivity * 100));
        sensitivitySlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        sensitivitySlider.addChangeListener(e -> {
            config.guiSensitivity = sensitivitySlider.getValue() / 100.0f;
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        mainPanel.add(sensitivitySlider);
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Config management
        mainPanel.add(createSection("Configuration"));
        mainPanel.add(Box.createVerticalStrut(10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(ModernTheme.BACKGROUND);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton exportButton = new ModernButton("Export Config");
        exportButton.addActionListener(e -> exportConfig());
        buttonPanel.add(exportButton);
        
        ModernButton importButton = new ModernButton("Import Config");
        importButton.addActionListener(e -> importConfig());
        buttonPanel.add(importButton);
        
        mainPanel.add(buttonPanel);
        
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
    
    private void exportConfig() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Configuration");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.endsWith(".json")) {
                    path += ".json";
                }
                ArizonClient.getInstance().getConfigManager().exportConfig(path);
                JOptionPane.showMessageDialog(this, "Configuration exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importConfig() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Configuration");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                ArizonClient.getInstance().getConfigManager().importConfig(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Configuration imported successfully! Please restart the GUI.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to import: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

package com.arizon.client.gui.screens;

import com.arizon.client.ArizonClient;
import com.arizon.client.config.ClientConfig;
import com.arizon.client.gui.theme.ModernTheme;
import com.arizon.client.gui.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Main GUI screen with collapsible categories
 */
public class MainScreen extends JFrame {
    
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    
    private JPanel contentPanel;
    private JPanel tabsPanel;
    private JPanel functionsPanel;
    private ClientConfig config;
    
    private Map<String, Boolean> categoryStates = new HashMap<>();
    
    public MainScreen() {
        this.config = ArizonClient.getInstance().getConfigManager().getConfig();
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
        setBackground(new Color(0, 0, 0, 0));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void initializeComponents() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(ModernTheme.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header
        JPanel headerPanel = createHeader();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabs panel (horizontal)
        tabsPanel = createTabsPanel();
        contentPanel.add(tabsPanel, BorderLayout.CENTER);
        
        add(contentPanel);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel(ArizonClient.getName() + " Menu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        closeButton.setForeground(ModernTheme.TEXT_SECONDARY);
        closeButton.setBackground(ModernTheme.BACKGROUND);
        closeButton.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> setVisible(false));
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createTabsPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.BACKGROUND);
        
        // Tabs row
        JPanel tabsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabsRow.setBackground(ModernTheme.BACKGROUND);
        tabsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] tabs = {"Visuals", "HUD", "Settings", "Misc", "About"};
        for (String tab : tabs) {
            JButton tabBtn = createTabButton(tab);
            tabsRow.add(tabBtn);
        }
        
        mainPanel.add(tabsRow);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Functions panel with scroll
        functionsPanel = new JPanel();
        functionsPanel.setLayout(new BoxLayout(functionsPanel, BoxLayout.Y_AXIS));
        functionsPanel.setBackground(ModernTheme.BACKGROUND);
        
        JScrollPane scrollPane = new JScrollPane(functionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ModernTheme.BACKGROUND);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mainPanel.add(scrollPane);
        
        // Load first tab
        loadTabContent("Visuals");
        
        return mainPanel;
    }
    
    private JButton createTabButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(ModernTheme.TEXT_PRIMARY);
        button.setBackground(ModernTheme.CARD);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> loadTabContent(text));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ModernTheme.HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ModernTheme.CARD);
            }
        });
        
        return button;
    }
    
    private void loadTabContent(String tabName) {
        functionsPanel.removeAll();
        
        switch (tabName) {
            case "Visuals":
                addCategory("Time & Weather", createTimeWeatherPanel());
                addCategory("Visual Effects", createVisualEffectsPanel());
                break;
            case "HUD":
                addCategory("HUD Editor", createHudEditorPanel());
                addCategory("Visual Aura", createVisualAuraPanel());
                break;
            case "Settings":
                addCategory("General Settings", createGeneralSettingsPanel());
                break;
            case "Misc":
                addCategory("Miscellaneous", createMiscPanel());
                break;
            case "About":
                addCategory("About", createAboutPanel());
                break;
        }
        
        functionsPanel.revalidate();
        functionsPanel.repaint();
    }
    
    private void addCategory(String name, JPanel content) {
        if (!categoryStates.containsKey(name)) {
            categoryStates.put(name, true);
        }
        
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(ModernTheme.CARD);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Header with toggle button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.CARD);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(name);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        
        JButton toggleBtn = new JButton(categoryStates.get(name) ? "▼" : "▶");
        toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        toggleBtn.setForeground(ModernTheme.TEXT_SECONDARY);
        toggleBtn.setBackground(ModernTheme.CARD);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        content.setVisible(categoryStates.get(name));
        
        toggleBtn.addActionListener(e -> {
            boolean isExpanded = categoryStates.get(name);
            categoryStates.put(name, !isExpanded);
            toggleBtn.setText(!isExpanded ? "▼" : "▶");
            content.setVisible(!isExpanded);
            functionsPanel.revalidate();
            functionsPanel.repaint();
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(toggleBtn, BorderLayout.EAST);
        
        categoryPanel.add(headerPanel);
        categoryPanel.add(Box.createVerticalStrut(8));
        categoryPanel.add(content);
        
        functionsPanel.add(categoryPanel);
        functionsPanel.add(Box.createVerticalStrut(8));
    }
    
    private JPanel createTimeWeatherPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel("Time of Day");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(4));
        
        ModernSlider timeSlider = new ModernSlider(0, 24, (int)config.timeOfDay);
        timeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeSlider.addChangeListener(e -> {
            config.timeOfDay = timeSlider.getValue();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        panel.add(timeSlider);
        panel.add(Box.createVerticalStrut(8));
        
        JLabel weatherLabel = new JLabel("Weather");
        weatherLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        weatherLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        weatherLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(weatherLabel);
        panel.add(Box.createVerticalStrut(4));
        
        String[] weatherOptions = {"Clear", "Rain", "Thunder", "Snow"};
        ModernComboBox weatherCombo = new ModernComboBox(weatherOptions);
        weatherCombo.setSelectedItem(config.weather);
        weatherCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        weatherCombo.addActionListener(e -> {
            config.weather = (String) weatherCombo.getSelectedItem();
            ArizonClient.getInstance().getConfigManager().saveConfig();
        });
        panel.add(weatherCombo);
        
        return panel;
    }
    
    private JPanel createVisualEffectsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] effects = {
            "Global Filters", "Anti-Aliasing", "Object Outline", 
            "Bloom Effect", "Enhanced Shadows"
        };
        
        for (String effect : effects) {
            boolean value = getEffectValue(effect);
            ModernCheckBox check = new ModernCheckBox(effect, value);
            check.setAlignmentX(Component.LEFT_ALIGNMENT);
            check.addActionListener(e -> {
                setEffectValue(effect, check.isSelected());
                ArizonClient.getInstance().getConfigManager().saveConfig();
            });
            panel.add(check);
            panel.add(Box.createVerticalStrut(4));
        }
        
        return panel;
    }
    
    private JPanel createHudEditorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton editorButton = new ModernButton("Open HUD Editor");
        editorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        editorButton.addActionListener(e -> {
            ArizonClient.getInstance().getGuiManager().openHudEditor();
        });
        panel.add(editorButton);
        
        return panel;
    }
    
    private JPanel createVisualAuraPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel = new JLabel("⚠ Visual effect only");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(ModernTheme.WARNING);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(6));
        
        JLabel radiusLabel = new JLabel("Display Radius");
        radiusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        radiusLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        radiusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(radiusLabel);
        panel.add(Box.createVerticalStrut(4));
        
        ModernSlider radiusSlider = new ModernSlider(20, 70, 35);
        radiusSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        radiusSlider.addChangeListener(e -> {
            // Update aura radius
        });
        panel.add(radiusSlider);
        
        return panel;
    }
    
    private JPanel createGeneralSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Settings coming soon...");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(ModernTheme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        
        return panel;
    }
    
    private JPanel createMiscPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel("Additional features coming soon...");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(ModernTheme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        
        return panel;
    }
    
    private JPanel createAboutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ModernTheme.CARD);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(ArizonClient.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(4));
        
        JLabel versionLabel = new JLabel("Version: " + ArizonClient.getVersion());
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(versionLabel);
        
        return panel;
    }
    
    private boolean getEffectValue(String effect) {
        switch (effect) {
            case "Global Filters": return config.globalFilters;
            case "Anti-Aliasing": return config.antiAliasing;
            case "Object Outline": return config.objectOutline;
            case "Bloom Effect": return config.bloom;
            case "Enhanced Shadows": return config.shadows;
            default: return false;
        }
    }
    
    private void setEffectValue(String effect, boolean value) {
        switch (effect) {
            case "Global Filters": config.globalFilters = value; break;
            case "Anti-Aliasing": config.antiAliasing = value; break;
            case "Object Outline": config.objectOutline = value; break;
            case "Bloom Effect": config.bloom = value; break;
            case "Enhanced Shadows": config.shadows = value; break;
        }
    }
}

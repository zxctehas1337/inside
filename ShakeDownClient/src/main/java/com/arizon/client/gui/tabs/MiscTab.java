package com.arizon.client.gui.tabs;

import com.arizon.client.gui.theme.ModernTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Misc tab - Additional features
 */
public class MiscTab extends JPanel {
    
    public MiscTab() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BACKGROUND);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernTheme.BACKGROUND);
        
        JLabel title = new JLabel("Miscellaneous");
        title.setFont(ModernTheme.FONT_TITLE);
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(20));
        
        JLabel infoLabel = new JLabel("Additional features coming soon...");
        infoLabel.setFont(ModernTheme.FONT_BODY);
        infoLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(infoLabel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
}

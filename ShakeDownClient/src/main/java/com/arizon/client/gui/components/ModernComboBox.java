package com.arizon.client.gui.components;

import com.arizon.client.gui.theme.ModernTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern styled combo box component
 */
public class ModernComboBox extends JComboBox<String> {
    
    public ModernComboBox(String[] items) {
        super(items);
        
        setFont(ModernTheme.FONT_BODY);
        setForeground(ModernTheme.TEXT_PRIMARY);
        setBackground(ModernTheme.SURFACE);
        setFocusable(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        setMaximumSize(new Dimension(300, 35));
        setPreferredSize(new Dimension(300, 35));
    }
}

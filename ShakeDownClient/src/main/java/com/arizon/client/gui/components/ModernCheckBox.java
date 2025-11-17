package com.arizon.client.gui.components;

import com.arizon.client.gui.theme.ModernTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern styled checkbox component
 */
public class ModernCheckBox extends JCheckBox {
    
    public ModernCheckBox(String text, boolean selected) {
        super(text, selected);
        
        setFont(ModernTheme.FONT_BODY);
        setForeground(ModernTheme.TEXT_PRIMARY);
        setBackground(ModernTheme.BACKGROUND);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }
}

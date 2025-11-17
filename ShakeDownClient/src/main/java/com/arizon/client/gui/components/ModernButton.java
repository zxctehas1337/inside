package com.arizon.client.gui.components;

import com.arizon.client.gui.theme.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern styled button component
 */
public class ModernButton extends JButton {
    
    public ModernButton(String text) {
        super(text);
        
        setFont(ModernTheme.FONT_BODY);
        setForeground(Color.WHITE);
        setBackground(ModernTheme.ACCENT);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(ModernTheme.ACCENT_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(ModernTheme.ACCENT);
            }
        });
    }
}

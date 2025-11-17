package com.arizon.client.gui.components;

import com.arizon.client.gui.theme.ModernTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern styled slider component
 */
public class ModernSlider extends JPanel {
    
    private JSlider slider;
    private JLabel valueLabel;
    
    public ModernSlider(int min, int max, int value) {
        setLayout(new BorderLayout(10, 0));
        setBackground(ModernTheme.BACKGROUND);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        slider = new JSlider(min, max, value);
        slider.setBackground(ModernTheme.BACKGROUND);
        slider.setForeground(ModernTheme.ACCENT);
        slider.setFocusable(false);
        
        valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(ModernTheme.FONT_BODY);
        valueLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        valueLabel.setPreferredSize(new Dimension(50, 20));
        
        slider.addChangeListener(e -> {
            valueLabel.setText(String.valueOf(slider.getValue()));
        });
        
        add(slider, BorderLayout.CENTER);
        add(valueLabel, BorderLayout.EAST);
    }
    
    public int getValue() {
        return slider.getValue();
    }
    
    public void setValue(int value) {
        slider.setValue(value);
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener listener) {
        slider.addChangeListener(listener);
    }
}

package net.minecraft.client.gui.widget;

import net.minecraft.text.Text;

public class ButtonWidget {
    public static final Object DEFAULT_NARRATION_SUPPLIER = new Object();
    
    public interface PressAction {
        void onPress(ButtonWidget button);
    }
    
    public ButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, Object narrationSupplier) {}
    
    public void setMessage(Text message) {}
}

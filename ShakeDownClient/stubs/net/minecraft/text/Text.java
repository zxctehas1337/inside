package net.minecraft.text;

public interface Text {
    static Text literal(String text) {
        return new Text() {};
    }
}

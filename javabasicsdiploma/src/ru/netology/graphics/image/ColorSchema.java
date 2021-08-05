package ru.netology.graphics.image;

public class ColorSchema implements TextColorSchema {
    @Override
    public char convert(int color) {
        final char[] C2= {'#', '$', '@', '%', '*', '+', '-', '\''};
        return C2[color/32];
    }
}

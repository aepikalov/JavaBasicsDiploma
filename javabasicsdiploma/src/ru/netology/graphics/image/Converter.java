package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int width;
    private int height;
    private double maxRatio;
    private TextColorSchema schema;
    private int newWidth;
    private int newHeight;

    public Converter() {
        schema = new ColorSchema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {

        BufferedImage img = ImageIO.read(new URL(url));
        maximumRatio(img);
        resizeImage(img);
        char[][] graph = new char[newHeight][newWidth];
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        var bwRaster = bwImg.getRaster();
        for (int h = 0; h < newHeight; h++) {
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                graph[h][w] = c;
            }
        }
        StringBuilder sb = new StringBuilder();
        printText(graph, sb);
        return sb.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema colorSchema) {
        this.schema = colorSchema;
    }

    private void maximumRatio(BufferedImage img) throws BadImageSizeException {
        double ratio;
        if (img.getWidth() / img.getHeight() > img.getHeight() / img.getWidth()) {
            ratio = (double) img.getWidth() / (double) img.getHeight();
        } else {
            ratio = (double) img.getHeight() / (double) img.getWidth();
        } if (ratio > maxRatio && maxRatio != 0) throw new BadImageSizeException(ratio, maxRatio);
    }

    private void printText(char[][] graph, StringBuilder sb) {
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                sb.append(graph[i][j]);
                sb.append(graph[i][j]);
            }
            sb.append("\n");
        }
    }

    private void resizeImage(BufferedImage img) {
        double coeffW = 0;
        double coeffH = 0;
        if (img.getWidth() > width || img.getHeight() > height) {
            if (width != 0) {
                coeffW = img.getWidth() / width;
            } else coeffW = 1;
            if (height != 0) {
                coeffH = img.getHeight() / height;
            } else coeffH = 1;
            if (Math.min(coeffW, coeffH) == coeffH) {
                newWidth = (int) (img.getWidth() / coeffW);
                newHeight = (int) (img.getHeight() / coeffW);
            } else {
                newWidth = (int) (img.getWidth() / coeffH);
                newHeight = (int) (img.getHeight() / coeffH);
            }
        } else {
            newWidth = img.getWidth();
            newHeight = img.getHeight();
        }
    }
}

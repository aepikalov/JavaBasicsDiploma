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

    public Converter() {
        schema = new ColorSchema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        int newWidth = 0;
        int newHeight = 0;
        double ratio = 0;
        double coeffW = 0;
        double coeffH = 0;

        BufferedImage img = ImageIO.read(new URL(url));

        //максимально допустимое соотношение сторон
        if (img.getWidth() / img.getHeight() > img.getHeight() / img.getWidth()) {
            ratio = (double) img.getWidth() / (double) img.getHeight();
        } else {
            ratio = (double) img.getHeight() / (double) img.getWidth();
        }
        //если полученное соотношение больше максимально установленнго то выборс ошибки
        if (ratio > maxRatio && maxRatio != 0) throw new BadImageSizeException(ratio, maxRatio);

        //установка макс. ширины/длинны картинки
        if (img.getWidth() > width || img.getHeight() > height) {
            //коэффициенты сжатия картинки
            if (width != 0) {
                coeffW = img.getWidth() / width;
            } else coeffW = 1;
            if (height != 0) {
                coeffH = img.getHeight() / height;
            } else coeffH = 1;

            if (Math.min(coeffW,coeffH) == coeffH) {
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
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                sb.append(graph[i][j]);
                sb.append(graph[i][j]);
            }
            sb.append("\n");

        }
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
}

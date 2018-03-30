package com.mifan.batch.spider.attachment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>由于RGB空间是线性的并且相互正交，而人眼的视觉系统并不是线性的，RGB空间并不能反映人眼对颜色的感知，相对应的颜色距离也不能很好的反映两个颜色是否相近。</p>
 * <p>http://www.compuphase.com/cmetric.htm</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/7/28
 */
public enum ImageColor {

    RGB_000000("000000", new Color(0, 0, 0)),
    RGB_0000FF("0000FF", new Color(0, 0, 255)),
    RGB_00FF00("00FF00", new Color(0, 255, 0)),
    RGB_00FFFF("00FFFF", new Color(0, 255, 255)),
    RGB_FF0000("FF0000", new Color(255, 0, 0)),
    RGB_FF00FF("FF00FF", new Color(255, 0, 255)),
    RGB_FFFF00("FFFF00", new Color(255, 255, 0)),
    RGB_FFFFFF("FFFFFF", new Color(255, 255, 255));

    private static Map<String, ImageColor> map = new HashMap<>();

    static {
        for (ImageColor e : ImageColor.values()) {
            map.put(e.getName(), e);
        }
    }

    public static ImageColor fromName(String name) {
        return map.get(name);
    }

    /**
     * <p>计算RGB颜色的灰度</p>
     *
     * @param r REG
     * @param g GREEN
     * @param b BLUE
     * @return 二值化/灰度化后的结果
     */
    public static int getGray(int r, int g, int b) {
        return (int) (0.229 * r + 0.587 * g + 0.114 * b);
    }

    /**
     * <p>计算RGB颜色的灰度</p>
     *
     * @param pixel 像素
     * @return 二值化/灰度化后的结果
     */
    public static int getGray(int pixel) {
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;
        return getGray(r, g, b);
    }

    /**
     * <p>通过欧氏距离计算与特定颜色的相似度</p>
     *
     * @param bufferedImage bufferedImage
     * @return result map
     */
    public static Map<String, Double> score(BufferedImage bufferedImage, ColourDistance distance) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // 读取像素
        int len = width * height;
        int[] pixels = new int[len];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

        Map<String, Double> map = new HashMap<>();
        for (ImageColor imageColor : ImageColor.values()) {
            Color color = imageColor.getColor();
            int r2 = color.getRed();
            int g2 = color.getGreen();
            int b2 = color.getBlue();
            double sum = 0;
            for (int pixel : pixels) {
                int r1 = (pixel >> 16) & 0xFF;
                int g1 = (pixel >> 8) & 0xFF;
                int b1 = pixel & 0xFF;
                sum += distance.distance(r1, g1, b1, r2, g2, b2) / len;
            }
            map.put(imageColor.getName(), 1D / (1D + sum));
        }
        return map;
    }

    private final String name;
    private final Color color;

    ImageColor(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}

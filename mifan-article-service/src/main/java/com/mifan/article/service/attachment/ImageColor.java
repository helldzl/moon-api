package com.mifan.article.service.attachment;

import java.awt.*;
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

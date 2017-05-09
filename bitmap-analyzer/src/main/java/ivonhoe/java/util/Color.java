package ivonhoe.java.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/**
 * @author Ivonhoe on 2017/3/24.
 */

public class Color {


    static BufferedImage img565 = new BufferedImage(1, 1, BufferedImage.TYPE_USHORT_565_RGB);

    public static int MakeRGB565(int color) {
        img565.setRGB(0, 0, color);
        DataBuffer buff = img565.getData().getDataBuffer();
        return buff.getElem(0);
    }

    public static int RGB888ToRGB565(int red, int green, int blue) {
        int B = (blue >>> 3) & 0x001F;
        int G = ((green >>> 2) << 5) & 0x07E0;
        int R = ((red >>> 3) << 11) & 0xF800;

        return (R | G | B);
    }

    public static int RGB888ToRGB565(int aPixel) {
        //aPixel <<= 8;
        //System.out.println(Integer.toHexString(aPixel));
        int red = (aPixel >> 16) & 0xFF;
        int green = (aPixel >> 8) & 0xFF;
        int blue = (aPixel) & 0xFF;
        return RGB888ToRGB565(red, green, blue);
    }

    public static int RGB565ToRGB888(int aPixel) {
        int b = (((aPixel) & 0x001F) << 3) & 0xFF;
        int g = (((aPixel) & 0x07E0) >>> 2) & 0xFF;
        int r = (((aPixel) & 0xF800) >>> 8) & 0xFF;
        // return RGBA
        return 0x000000ff | (r << 24) | (g << 16) | (b << 8);
    }

    public static int alpha(int color) {
        return color >>> 24;
    }

    /**
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int rgb(int red, int green, int blue) {
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     *
     * @param alpha Alpha component [0..255] of the color
     * @param red   Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue  Blue component [0..255] of the color
     */
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}

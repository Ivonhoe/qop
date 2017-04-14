package ivonhoe.java.qop.utils;

import java.awt.*;

/**
 * Created by Ivonhoe on 2017/4/12.
 */
public class Utilities {

    // java合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] subByteArray(byte[] array, int start, int end) {
        byte[] result = null;
        try {
            result = new byte[end - start];
        } catch (NegativeArraySizeException e) {
            e.printStackTrace();
        }
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }

    public static Image createImage(byte[] data) {
        return Toolkit.getDefaultToolkit().createImage(data);
    }
}

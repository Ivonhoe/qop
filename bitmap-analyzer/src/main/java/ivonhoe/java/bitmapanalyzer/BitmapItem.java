package ivonhoe.java.bitmapanalyzer;

import java.io.Serializable;

/**
 * @author Ivonhoe on 2017/3/24.
 */

public class BitmapItem implements Serializable {

    private int width;
    private int height;
    private int count = 0;
    private float percent;

    public BitmapItem(int w, int h) {
        width = w;
        height = h;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int increase() {
        return ++count;
    }

    @Override
    public String toString() {
        return "width:" + width + ",height:" + height + ",count:" + count + ",percent:" + percent * 100 + "%";
    }
}

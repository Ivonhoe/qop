/**
 *
 */
package ivonhoe.java.qop.minicomponent;

import ivonhoe.java.qop.device.Banner;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.utils.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static ivonhoe.java.qop.utils.Utilities.byteMerger;
import static ivonhoe.java.qop.utils.Utilities.subByteArray;

public class MiniCap extends MiniComponent implements ScreenSubject {

    private static final int PORT_MINICAP = 1717;

    private Queue<byte[]> dataQueue = new LinkedBlockingQueue<byte[]>();
    private List<AndroidScreenObserver> observers = new ArrayList<>();

    private Banner banner = new Banner();

    private Socket socket;

    public MiniCap(Device device) {
        super(device, PORT_MINICAP);
    }

    @Override
    public void setupComponent() {
        super.setupComponent();

        // TODO push so文件
        String abi = mDevice.getAbi();
        String sdk = mDevice.getSdk();
        File componentFile = new File(DIR_MINICAP_SHARE + "android-" + sdk + File.separator +
                abi + File.separator + MINICAP_SO);

        String remotePath = String.format(REMOTE_PATH, MINICAP_SO);

        // 将minicap的可执行文件和.so文件一起push到设备中
        mDevice.push(componentFile.getAbsolutePath(), remotePath);

        String chmodCommand = String.format(SHELL_COMMAND_CHMOD, remotePath);
        mDevice.executeShellCommand(chmodCommand);
    }

    @Override
    public String getComponentDir() {
        return DIR_MINICAP;
    }

    @Override
    public String getComponentName() {
        return MINICAP;
    }

    @Override
    public String getStartCommand() {
        return String.format(SHELL_COMMAND_MINICAP, getComponentName(), mDevice.getSize(), mDevice.getSize());
    }

    @Override
    public void startComponent() {
        super.startComponent();

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread frame = new Thread(new ImageBinaryFrameCollector());
        frame.start();
        Thread convert = new Thread(new ImageConverter());
        convert.start();
    }

    private BufferedImage createImageFromByte(byte[] binaryData) {
        BufferedImage bufferedImage = null;
        InputStream in = new ByteArrayInputStream(binaryData);
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bufferedImage;
    }

    class ImageBinaryFrameCollector implements Runnable {
        private InputStream stream = null;

        public void run() {
            Logger.d("图片二进制数据收集器已经开启");
            try {
                socket = new Socket("localhost", mPort);
                stream = socket.getInputStream();
                while (true) {
                    byte[] buffer;
                    buffer = new byte[4096];
                    int realLen = stream.read(buffer);
                    if (buffer.length != realLen) {
                        buffer = subByteArray(buffer, 0, realLen);
                    }
                    dataQueue.add(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null && socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            Logger.d("图片二进制数据收集器已关闭");
        }
    }

    class ImageConverter implements Runnable {
        private int readBannerBytes = 0;
        private int bannerLength = 2;
        private int readFrameBytes = 0;
        private int frameBodyLength = 0;
        private byte[] frameBody = new byte[0];

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // TODO Auto-generated method stub
            long start = System.currentTimeMillis();
            while (true) {
                if (dataQueue.isEmpty()) {
                    continue;
                }
                byte[] buffer = dataQueue.poll();
                int len = buffer.length;
                for (int cursor = 0; cursor < len; ) {
                    int byte10 = buffer[cursor] & 0xff;
                    if (readBannerBytes < bannerLength) {
                        cursor = parserBanner(cursor, byte10);
                    } else if (readFrameBytes < 4) {
                        // 第二次的缓冲区中前4位数字和为frame的缓冲区大小
                        frameBodyLength += (byte10 << (readFrameBytes * 8)) >>> 0;
                        cursor += 1;
                        readFrameBytes += 1;
//                        Logger.d("解析图片大小 = " + readFrameBytes);
                    } else {
                        if (len - cursor >= frameBodyLength) {
                            // Logger.d("frameBodyLength = " + frameBodyLength);
                            byte[] subByte = subByteArray(buffer, cursor, cursor + frameBodyLength);
                            frameBody = byteMerger(frameBody, subByte);
                            if ((frameBody[0] != -1) || frameBody[1] != -40) {
                                Logger.d(String.format("Frame body does not start with JPG header"));
                                return;
                            }
                            final byte[] finalBytes = subByteArray(frameBody, 0, frameBody.length);
                            // 转化成bufferImage
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Image image = createImageFromByte(finalBytes);
                                    notifyObservers(image);
                                }
                            }).start();

                            long current = System.currentTimeMillis();
//                            Logger.d("图片已生成,耗时: "
//                                    + TimeUtil.formatElapsedTime(current
//                                    - start));
//                            start = current;
                            cursor += frameBodyLength;
                            restore();
                        } else {
                            // Logger.d("所需数据大小 : " + frameBodyLength);
                            byte[] subByte = subByteArray(buffer, cursor, len);
                            frameBody = byteMerger(frameBody, subByte);
                            frameBodyLength -= (len - cursor);
                            readFrameBytes += (len - cursor);
                            cursor = len;
                        }
                    }
                }
            }
        }

        private void restore() {
            frameBodyLength = 0;
            readFrameBytes = 0;
            frameBody = new byte[0];
        }

        private int parserBanner(int cursor, int byte10) {
            switch (readBannerBytes) {
                case 0:
                    // version
                    banner.setVersion(byte10);
                    break;
                case 1:
                    // length
                    bannerLength = byte10;
                    banner.setLength(byte10);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    // pid
                    int pid = banner.getPid();
                    pid += (byte10 << ((readBannerBytes - 2) * 8)) >>> 0;
                    banner.setPid(pid);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    // real width
                    int realWidth = banner.getReadWidth();
                    //System.out.println("realwidth0" + realWidth);
                    realWidth += (byte10 << ((readBannerBytes - 6) * 8)) >>> 0;
                    //System.out.println("realwidth1" + realWidth);
                    banner.setReadWidth(realWidth);
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                    // real height
                    int realHeight = banner.getReadHeight();
                    realHeight += (byte10 << ((readBannerBytes - 10) * 8)) >>> 0;
                    banner.setReadHeight(realHeight);
                    break;
                case 14:
                case 15:
                case 16:
                case 17:
                    // virtual width
                    int virtualWidth = banner.getVirtualWidth();
                    virtualWidth += (byte10 << ((readBannerBytes - 14) * 8)) >>> 0;
                    banner.setVirtualWidth(virtualWidth);
                    //System.out.println("virtual" + virtualWidth);
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                    // virtual height
                    int virtualHeight = banner.getVirtualHeight();
                    virtualHeight += (byte10 << ((readBannerBytes - 18) * 8)) >>> 0;
                    banner.setVirtualHeight(virtualHeight);
                    //System.out.println("virtualhegith" + virtualHeight);
                    break;
                case 22:
                    // orientation
                    banner.setOrientation(byte10 * 90);
                    break;
                case 23:
                    // quirks
                    banner.setQuirks(byte10);
                    break;
            }

            cursor += 1;
            readBannerBytes += 1;

            if (readBannerBytes == bannerLength) {
                Logger.d(banner.toString());
            }
            return cursor;
        }

    }

    public void registerObserver(AndroidScreenObserver o) {
        // TODO Auto-generated method stub
        observers.add(o);
    }

    public void removeObserver(AndroidScreenObserver o) {
        // TODO Auto-generated method stub
        int index = observers.indexOf(o);
        if (index != -1) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(Image image) {
        for (AndroidScreenObserver observer : observers) {
            observer.frameImageChange(image);
        }
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }
}

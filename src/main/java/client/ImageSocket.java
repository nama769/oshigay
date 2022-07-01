package client;

import communication.Protocol;
import communication.Result;
import util.ZLibUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static communication.Protocol.*;

public class ImageSocket implements Runnable{

    ClientConfig clientConfig;

    public ImageSocket(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public void run() {
        Result result = null;
        System.out.println("images runing");
        try {
            if (clientConfig.getImagesDis() != null) {
                result = Protocol.getResult(clientConfig.getImagesDis());
            }

        } catch (IOException e) {
            clientConfig.setLive(false);
            System.out.println("已下线！！！");
        }
        if (result != null){
            if(result.getType()==TYPE_IMAGE_NOT_FOUND){
                clientConfig.setImageLoading(false);
            }else if(result.getType()==TYPE_RET_IMAGE){
                type_ret_image(result.getData());
                System.out.println(getFormatTime() + " Teacher端接收到最新Image");
            }
        }
    }

    private void type_ret_image(byte[] data) {
        try {
            ByteArrayInputStream bai = new ByteArrayInputStream(ZLibUtils.decompress(data));
            System.out.println(getFormatTime()+"正在解压图像");
            BufferedImage buff = null;
            buff = ImageIO.read(bai);
            ManageFrame.centerPanel.setBufferedImage(buff);//为屏幕监控视图设置BufferedImage
            ManageFrame.centerPanel.repaint();
            bai.close();
            clientConfig.setImageLoading(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getFormatTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

}

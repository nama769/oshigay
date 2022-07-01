package client;

import communication.Protocol;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static communication.Protocol.TYPE_LOAD_IMAGE;

public class ShowImages implements Runnable{

    ClientConfig clientConfig;

    public ShowImages(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public void run() {
        showImageList();
    }

    private void showImageList(){
        clientConfig.setFocusImageType(null);
        for(String i:clientConfig.getImageIDsSearchList()){
            if(clientConfig.getFocusImageType()!=null){
                break;
            }
            while (clientConfig.isImageLoading()){
                try {
                    Thread.sleep(((int)clientConfig.getFrequency())*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Protocol.send(TYPE_LOAD_IMAGE,i.getBytes(StandardCharsets.UTF_8),clientConfig.getDos());
            clientConfig.setImageLoading(true);
            System.out.println(getFormatTime()+"Teacher端正在请求展示imagesID="+i+" 的图片");
//            try {
//                Thread.sleep(((int)clientConfig.getFrequency())*1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    public static String getFormatTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}

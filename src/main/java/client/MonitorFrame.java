package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import communication.Protocol;
import communication.Result;

/**
 * 监控模块，状态显示，托盘化，后台获取进程列表
 */
public class MonitorFrame extends JFrame {
    public MonitorFrame()
    {
        setTitle("考生端");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocation(350, 200);
        setResizable(false);

    }
    public void SendImage() throws IOException {
        DataOutputStream dos = clientConfig.getDos();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        while(true){
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
            BufferedImage tag = new BufferedImage((int) (width * 0.5), (int) (height * 0.5), BufferedImage.TYPE_INT_RGB);
            try{
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageIO.write(bfImage, "png", bao);
                Protocol.send(Protocol.TYPE_GRAPH, bao.toByteArray(), dos);
                bao.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static final int DEFAULT_WIDTH = 450;
    private static final int DEFAULT_HEIGHT = 300;
    private CardLayout card;
    private JPanel myPanel;
    Robot robot;
    ClientConfig clientConfig;
}

package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import communication.Protocol;
import communication.Result;
/**
 * 监控模块，状态显示，托盘化，后台获取进程列表
 */
public class MonitorFrame extends JFrame {
    private JLabel label1;
    private GridBagConstraints constraints;

    public MonitorFrame() {
        setTitle("考生端");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocation(350, 200);
        setResizable(false);
        constraints = new GridBagConstraints();
        myPanel = new JPanel(new GridBagLayout());
        label1 = new JLabel("登录密码", new ImageIcon("02.gif"), SwingConstants.CENTER);
//        constraints.gridx = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        myPanel.add(label1, constraints);
        add(myPanel, BorderLayout.CENTER);
//        label1.setText(UserModel.STATE_LOGIN);


    }

    public void SendImage() throws IOException {
        DataOutputStream dos = clientConfig.getDos();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        while (true) {
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
            BufferedImage tag = new BufferedImage((int) (width * 0.5), (int) (height * 0.5), BufferedImage.TYPE_INT_RGB);
            try {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageIO.write(bfImage, "png", bao);
                Protocol.send(Protocol.TYPE_GRAPH, bao.toByteArray(), dos);
                bao.close();
                Thread.sleep(clientConfig.getFrequency());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 考生端逻辑
     */
    public static void monitorUtil(ClientConfig clientConfig){
        new Thread(new Client(clientConfig)).start();
    }

    private static final int DEFAULT_WIDTH = 450;
    private static final int DEFAULT_HEIGHT = 300;
    private CardLayout card;
    private JPanel myPanel;
    Robot robot;
    ClientConfig clientConfig;
}

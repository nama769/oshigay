package client;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;


import communication.Protocol;
import communication.Result;
import database.DatabaseTool;
import database.UserModel;
import server.HandleClient;
import util.ZLibUtils;

import static communication.Protocol.*;


/**
 * 封装被控端的方法
 */
public class Client implements Runnable {

    Socket socket;

    DataOutputStream dos = null;
    DataInputStream dis = null;

    ClientConfig clientConfig;
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

    int exeNum = 0;

    /**
     * ip
     * mac
     * username
     * role
     * int frequency
     * appList
     */

    int width = (int) screensize.getWidth();
    int height = (int) screensize.getHeight();
    Robot robot;
    static boolean isLive = true;
    JButton button;

    public Client(ClientConfig clientConfig) {
        try {
            robot = new Robot();
            this.clientConfig = clientConfig;
            this.isLive = clientConfig.getIsLive();
            //ip
            //mac

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    /**
     * 连接服务器
     */
    public void conn(String address, int port) {
        try {
            socket = new Socket(address, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            clientConfig.setDis(dis);
            clientConfig.setDos(dos);
            clientConfig.setSocket(socket);
            // dos.writeUTF("client");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕截图并保存
     *
     * @return
     */
    public BufferedImage getScreenShot() {
        BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
        return bfImage;
    }

    public void load() {
        byte[] bytes = "client".getBytes();
        Protocol.send(Protocol.TYPE_LOAD, bytes, dos);
    }

    public void sendImage(BufferedImage buff) {
        if (buff == null)
            return;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(buff, "png", baos);
            if (clientConfig.getDos() != null) {
                Protocol.send(Protocol.TYPE_IMAGE, baos.toByteArray(), dos);
            }
            baos.close();
            System.out.println("send file successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 关闭客户端，释放掉资源
    public void close() {
        //向服务器发送消息
        Protocol.send(Protocol.TYPE_LOGOUT, new String("logout").getBytes(), dos);
        // 关闭资源
        try {
            if (dos != null)
                dos.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片缩放
     *
     * @param bfImage
     * @param scale
     * @return
     */
    public BufferedImage scale(BufferedImage bfImage, double scale) {
        // 截图压缩
        int width = bfImage.getWidth();
        int height = bfImage.getHeight();
        Image image = bfImage.getScaledInstance((int) (width * scale), (int) (height * scale), Image.SCALE_DEFAULT);
        BufferedImage tag = new BufferedImage((int) (width * scale), (int) (height * scale),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tag.createGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图
        g.dispose();
        return tag;
    }

    /**
     * 显示系统托盘
     */
//    public void showSystemTray() {
//        Image image = Toolkit.getDefaultToolkit().getImage("img/icon.png");
//        final TrayIcon trayIcon = new TrayIcon(image);// 创建托盘图标
//        trayIcon.setToolTip("屏幕监控系统\r\n客户端");// 设置提示文字
//        final SystemTray systemTray = SystemTray.getSystemTray();// 获得系统托盘对象
//
//        final PopupMenu popupMenu = new PopupMenu(); // 创建弹出菜单
//        MenuItem item = new MenuItem("退出");
//        item.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                isLive = false;
//                close();
//
//            }
//        });
//        popupMenu.add(item);
//        trayIcon.setPopupMenu(popupMenu);// 为托盘图标加弹出菜单
//        try {
//            systemTray.add(trayIcon);// 为系统托盘加托盘图标
//        } catch (AWTException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 处理服务端返回数据
     */
    public void run() {
        if (clientConfig.getRole() == 0) {
            /**
             * 考生端
             */
            while (isLive) {
                try {
                    sendImage();
                    searchBlackMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            /**
             * 老师端
             */
            while (isLive) {
                if (clientConfig.getFocusImageType() != null&&!clientConfig.isImageLoading()) {
                    Protocol.send(TYPE_GET_IMAGE, clientConfig.getFocusImageType().getBytes(StandardCharsets.UTF_8), clientConfig.getDos());
                    clientConfig.setImageLoading(true);
                }
                try {
                    System.out.println(getFormatTime() + " Teacher端正在选中：" + clientConfig.getFocusImageType());
                    Thread.sleep((int) clientConfig.getFrequency() * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                ManageFrame.centerPanel.setBufferedImage();

            }
        }
    }

    /**
     * 定时发送截图 并且 查询本地程序列表
     * @throws IOException
     */
    public void sendImage() throws IOException {
        DataOutputStream dos = clientConfig.getDos();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        while (isLive) {
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
            BufferedImage tag = new BufferedImage((int) (width * 0.5), (int) (height * 0.5), BufferedImage.TYPE_INT_RGB);
            try {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageIO.write(bfImage, "png", bao);
                byte[] zipBao = ZLibUtils.compress(bao.toByteArray());
                Protocol.send(Protocol.TYPE_GRAPH,zipBao, dos);
                long baoLength = bao.toByteArray().length;
                System.out.println(getFormatTime()+"正在上传压缩截图 压缩前长度："+baoLength+" 压缩后长度："+zipBao.length+" 压缩比："+(1-((double)zipBao.length/baoLength))*100+"%");
                bao.close();
                /**
                 * 查询本地程序列表，判断是否违规
                 */
                searchBlackMenu();
                Thread.sleep(((int) clientConfig.getFrequency()) * 1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleResult() {
        while (isLive) {
            System.out.println("client runing");
            Result result = null;
            try {
                if (clientConfig.getDis() != null) {
                    result = Protocol.getResult(clientConfig.getDis());
                }
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            } catch (IOException e) {
                isLive = false;
                System.out.println("已下线！！！");
            }

            if (result != null)
                handleType(result.getType(), result.getData());
        }
    }

    /**
     * 处理服务端发给客户端的数据，并让客户端进入相应逻辑
     *
     * @param type
     * @param data
     */
    private void handleType(int type, byte[] data) {

        switch (type) {
            case 1:
                break;
            case TYPE_CHANGE:
                type_change(data);
                System.out.println(getFormatTime() + " Student端正在更改监管频率：" + (int) clientConfig.getFrequency());
                break;
            case TYPE_GRAPH:
                GetFrequency(data);
                System.out.println(getFormatTime() + " Student端正在更改监管频率：" + (int) clientConfig.getFrequency());
                break;
            case TYPE_LOGIN_REPAY:
                type_login(data);
                System.out.println(getFormatTime() + " 登录成功");
                break;
            case TYPE_STUDENT_UP:
                type_student_up(data);
                System.out.println(getFormatTime() + " Teacher端接收到新Student：" + new String(data));
                break;
            case TYPE_RET_SELECT_IMAGEID:
                type_get_select_imageid(data);
                System.out.println(getFormatTime() + " Teacher端接收到最新ImageID：" + new String(data));
                break;
            case TYPE_RET_IMAGE:
                type_ret_image(data);
                System.out.println(getFormatTime() + " Teacher端接收到最新Image");
                break;
            case TYPE_RETURN_IMAGE_ID_BY_USERNAME:
                type_return_image_id_by_username(data);
                System.out.println(getFormatTime() + " Teacher端接收到按照Username查询传回的ImageIDs");
                showImageList();
                break;
            case TYPE_RETURN_IMAGE_ID_BY_IP:
                type_return_image_id_by_ip(data);
                System.out.println(getFormatTime() + " Teacher端接收到按照IP查询传回的ImageIDs");
                showImageList();
                break;
            case TYPE_RETURN_IMAGE_ID_BY_MAC:
                type_return_image_id_by_mac(data);
                System.out.println(getFormatTime() + " Teacher端接收到按照MAC查询传回的ImageIDs");
                showImageList();
                break;
            case TYPE_STUDENT_VIOLATE:
                type_student_violate(data);
                System.out.println(getFormatTime() + " Teacher记录了违规学生：" + new String(data));
                break;
            case TYPE_SEND_BLACK_LIST_TO_CLIENT:
                type_send_black_list_to_client(data);
                System.out.println(getFormatTime() + " Student端正在更改黑名单：" + new String(data));
                break;
            case TYPE_STUDENT_DOWN:
                type_student_down(data);
                System.out.println(getFormatTime() + " Teacher记录了下线学生：" + new String(data));
                break;
            default:
                break;
        }


    }

    private void GetFrequency(byte[] data) {
        byte fre = data[0];
        clientConfig.setFrequency(fre);
    }

    private void type_change(byte[] data) {
        byte frequency = data[0];
        if (frequency != clientConfig.getFrequency()) {
            clientConfig.setFrequency(frequency);
        }
    }

    private void type_login(byte[] data) {
        String msg = new String(data);
        if (msg.split("\n")[0].equals("Login successfully!")) {
            clientConfig.setState(UserModel.STATE_LOGIN);
            clientConfig.setLogin(1);
            clientConfig.setRole(Integer.parseInt(msg.split("\n")[1]));
        } else {
            clientConfig.setState(UserModel.STATE_LOGIN_FAIL);
        }
    }

    private void type_student_up(byte[] data) {
        String username = new String(data);
//        clientConfig.;
        ManageFrame.setTreeNode(ManageFrame.addValue(username));
        if (ManageFrame.curKey == null) ManageFrame.curKey = username;
    }

    private void type_get_select_imageid(byte[] data) {
        String imageid = new String(data);
        Protocol.send(TYPE_LOAD_IMAGE, imageid.getBytes(StandardCharsets.UTF_8), dos);
    }

    private void type_ret_image(byte[] data) {
        new Thread(new ImageSocket(clientConfig)).start();
//        try {
//            ByteArrayInputStream bai = new ByteArrayInputStream(ZLibUtils.decompress(data));
//            System.out.println(getFormatTime()+"正在解压图像");
//            BufferedImage buff = null;
//            buff = ImageIO.read(bai);
//            ManageFrame.centerPanel.setBufferedImage(buff);//为屏幕监控视图设置BufferedImage
//            ManageFrame.centerPanel.repaint();
//            bai.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void type_return_image_id_by_username(byte[] data) {
        String imageIDs = new String(data);
        String[] imageID = imageIDs.split("\n");
        clientConfig.setImageIDsSearchList(imageID);
    }

    private void type_return_image_id_by_ip(byte[] data) {
        String imageIDs = new String(data);
        String[] imageID = imageIDs.split("\n");
        clientConfig.setImageIDsSearchList(imageID);
    }

    private void type_return_image_id_by_mac(byte[] data) {
        String imageIDs = new String(data);
        String[] imageID = imageIDs.split("\n");
        clientConfig.setImageIDsSearchList(imageID);
    }

    private void type_student_violate(byte[] data) {
        String violateUsername = new String(data);
        clientConfig.addViolateUsername(violateUsername);
        ManageFrame.model.reload();
    }

    private void type_send_black_list_to_client(byte[] data) {
        String[] blackList = (new String(data)).split(" ");
        clientConfig.setBlackList(blackList);
    }

    private void type_student_down(byte[] data) {
        clientConfig.addDownUsername(new String(data));
        ManageFrame.model.reload();
    }

    public static String getFormatTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }


    public String[] exeMenu() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("tasklist");
        Process p = pb.start();
        BufferedReader out = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()), Charset.forName("GB2312")));
        String[] ostr = new String[1000];
        for (this.exeNum = 0; (out.readLine()) != null; this.exeNum++) {
            ostr[this.exeNum] = out.readLine();
            if (ostr[this.exeNum] != null) {
                ostr[this.exeNum] = ostr[this.exeNum].substring(0, ostr[this.exeNum].indexOf(' '));
            }
        }
        return ostr;
    }

    /**
     * 黑名单检测程序
     * @throws IOException
     */

    public void searchBlackMenu() throws IOException {
        String[] exeList = this.exeMenu();

        String[] blackList = clientConfig.getBlackList();
        for (int i = 0; i < this.exeNum; i++) {
            for (int j = 0; j < blackList.length; j++) {
                if (exeList[i] != null) {
                    if (exeList[i].contentEquals(blackList[j]) && !clientConfig.isIfBlackDetect()) {
                        clientConfig.setIfBlackDetect(true);
                        Protocol.send(TYPE_STUDENT_VIOLATE_SERVER, new byte[]{0x11}, clientConfig.getDos());
                        JOptionPane.showMessageDialog(null, "您的操作已违反考试规定", "此行为已被记录", 1);
                    }
                }
            }
        }
    }

    private void showImageList() {
        new Thread(new ShowImages(clientConfig)).start();
    }

//	public static void main(String[] args) {
//		final Client client = new Client();
//		client.showSystemTray();// 显示托盘
//		client.conn("192.168.247.1",33000);
//		client.load();// 登录
//		client.showSystemTray();// 显示托盘
//		while (client.isLive) {
//			client.sendImage(client.getScreenShot());
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException ev) {
//
//			}
//		}
//	}
}

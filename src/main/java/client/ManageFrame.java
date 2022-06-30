package client;


import com.sun.corba.se.impl.orbutil.graph.NodeData;
import communication.Protocol;

import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import static client.View.model;
import static communication.Protocol.*;

/**
 * 管理模块界面，包含树形考生列表，图像展示，图像查询，指令下发（更改截图频率）
 */
public class ManageFrame {
    public static Map<String, Socket> client=new HashMap<String,Socket>();

    //	public static View view=new View();
    public static String curKey=null;
    public static boolean serverLive=true;
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    private int width;
    private int height;
    public static JTextField frequencyTextField;
    public static JTextField findUsernameTextField;
    public static JTextField findMACTextField;
    public static JTextField findIPTextField;

    public static JTextField findBlackListTextField;
    public static JLabel frequencyLabel;

    //以下必须为静态的，否则在HandleClient里访问不到
    static DefaultTreeModel model;
    static DefaultMutableTreeNode root;
    static DrawPanel centerPanel;
    static List<String> list=new ArrayList<>();

    private ClientConfig clientConfig;

    public ManageFrame(final ClientConfig clientConfig)  {

        width = (int)  screensize.getWidth();
        height =  (int) screensize.getHeight();

        this.clientConfig=clientConfig;

        //得到内容窗格
        JFrame frame=new JFrame("远程屏幕监视系统");
        Container container=frame.getContentPane();


        //左侧
        JPanel leftPanel=new JPanel();
        leftPanel.setBackground(Color.darkGray);
        container.add(leftPanel,BorderLayout.WEST);
        //树
        root=new DefaultMutableTreeNode("所有连接的被控端");
        model=new DefaultTreeModel(root);
        JTree tree=new JTree(model);
        tree.setBackground(Color.darkGray);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree=(JTree) e.getSource();
                DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                if(selectionNode!=null){
                    String nodeName=selectionNode.toString();
                    curKey=nodeName;
                    clientConfig.setFocusImageType(curKey);
                }
            }
        });


        //设置树节点的样式
        DefaultTreeCellRenderer cr=new DefaultTreeCellRenderer();
        cr.setBackgroundNonSelectionColor(Color.darkGray);
        cr.setTextNonSelectionColor(Color.white);
        tree.setCellRenderer(cr);
        tree.setCellRenderer(new MyNodeRenderer());
        JScrollPane jsp=new JScrollPane(tree);
        JScrollBar bar=jsp.getHorizontalScrollBar();
        bar.setBackground(Color.darkGray);
        jsp.setBorder(null);
        leftPanel.add(jsp);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                serverLive=false;
            }

        });
        centerPanel=new DrawPanel();
        container.add(new JScrollPane(centerPanel));
        frame.setSize(width,height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        GridBagConstraints constraints = new GridBagConstraints();
        JPanel myPanel = new JPanel(new GridBagLayout());
        frequencyLabel = new JLabel("截图频率（s）");
//        constraints.gridx = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        myPanel.add(frequencyLabel, constraints);

        frequencyTextField = new JTextField();
        frequencyTextField.setPreferredSize(new Dimension(120, 25));
        myPanel.add(frequencyTextField, constraints);

        JLabel findLabel2 = new JLabel("用户名查找");
        myPanel.add(findLabel2, constraints);

        findUsernameTextField = new JTextField();
        findUsernameTextField.setPreferredSize(new Dimension(120, 25));
        myPanel.add(findUsernameTextField, constraints);

        JLabel findLabel3 = new JLabel("MAC查找");
        myPanel.add(findLabel3, constraints);

        findMACTextField = new JTextField();
        findMACTextField.setPreferredSize(new Dimension(120, 25));
        myPanel.add(findMACTextField, constraints);

        JLabel findLabel4 = new JLabel("IP查找");
        myPanel.add(findLabel4, constraints);

        findIPTextField = new JTextField();
        findIPTextField.setPreferredSize(new Dimension(120, 25));
        myPanel.add(findIPTextField, constraints);


        JLabel findLabel5 = new JLabel("黑名单设置");
        myPanel.add(findLabel5, constraints);

        findBlackListTextField = new JTextField();
        findBlackListTextField.setPreferredSize(new Dimension(120, 25));
        myPanel.add(findBlackListTextField, constraints);


        JButton myButton = new JButton("确定");
        myButton.addActionListener(new
                                           ActionListener()
                                           {
                                               public void actionPerformed(ActionEvent e)
                                               {
                                                   buttonUtil();
                                               }
                                           });
        myPanel.add(myButton);

        container.add(myPanel, BorderLayout.EAST);

    }


    private  void sendUsername(String username) {
        Protocol.send(Protocol.TYPE_FIND_IMAGE_BY_USERNAME,username.getBytes(StandardCharsets.UTF_8),clientConfig.getDos());
    }

    private  void sendIP(String ip) {
        Protocol.send(Protocol.TYPE_FIND_IMAGE_BY_IP,ip.getBytes(StandardCharsets.UTF_8),clientConfig.getDos());
    }

    private  void sendMAC(String mac) {
        Protocol.send(Protocol.TYPE_FIND_IMAGE_BY_MAC,mac.getBytes(StandardCharsets.UTF_8),clientConfig.getDos());
    }

    /**
     * 监考端逻辑
     */
    public static void manageUtil(ClientConfig clientConfig){
        new Thread(new Client(clientConfig)).start();
    }

    private void changeFrequency(String managefrequency){
        clientConfig.setFrequency((byte) Integer.parseInt(managefrequency));
        Protocol.send(TYPE_CHANGE,new byte[]{(byte) Integer.parseInt(managefrequency)}, clientConfig.getDos());
    }

    private void changeBlackList(String blackListString){
        clientConfig.setBlackList(blackListString.split(" "));
        Protocol.send(TYPE_CHANGE_BLACK_LIST,blackListString.getBytes(StandardCharsets.UTF_8), clientConfig.getDos());
    }

    private void showImageList(){
        clientConfig.setFocusImageType(null);
        for(String i:clientConfig.getImageIDsSearchList()){
            Protocol.send(TYPE_LOAD_IMAGE,i.getBytes(StandardCharsets.UTF_8),clientConfig.getDos());
            try {
                Thread.sleep(((int)clientConfig.getFrequency())*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void buttonUtil(){
        String frequency =  frequencyTextField.getText();
        String findUsername =  findUsernameTextField.getText();
        String findMAC =  findMACTextField.getText();
        String findIP = findIPTextField.getText();
        String blackListText =  findBlackListTextField.getText();
        if(!frequency.equals("")){
            /**
             * 更改频率
             */
            changeFrequency(frequency);
        }else if(!findUsername.equals("")){
            /**
             * 按用户名查找
             */

            sendUsername(findUsername);
        }else if(!findMAC.equals("")){
            /**
             * 按MAC查找
             */
            sendMAC(findMAC);
        }else if(!findIP.equals("")){
            /**
             * 按IP查找
             */
            sendIP(findIP);
        }else if(!blackListText.equals("")){
            /**
             * 改变黑名单
             */
            changeBlackList(blackListText);
        }

    }

    /**
     * 添加树节点
     * @param l
     */
    public static void setTreeNode(List<String> l){
        list=l;
        root.removeAllChildren();
        for(int i=0;i<list.size();i++){
            DefaultMutableTreeNode node1=new DefaultMutableTreeNode(list.get(i));
            root.add(node1);

        }
        //DefaultTreeCellRenderer renderer =  tree.getCellRenderer();
        model.reload();
    }
    private class MyNodeRenderer extends DefaultTreeCellRenderer {
        private ClientConfig  clientConfig;

        /**
         * 待实现一个构造方法，包含ClientConfig中的名单
         * @param tree
         * @param value
         * @param selected
         * @param expanded
         * @param leaf
         * @param row
         * @param hasFocus
         * @return
         */
        private ViolateUsernames = ClientConfig.getViolateUsernames();
        private DownUsernames = ClientConfig.getDownUsernames();
        //重写getTreeCellRendererComponent的方法

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            //super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            //setForeground(Color.GREEN);// 设置文字的颜色
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            //获取到当前即将绘制的结点的名称和图标
            NodeData nodeData = (NodeData) node.getUserObject();
            //
            for (String username:
                    ViolateUsernames) {
                if(nodeData.toString().equals(username)){
                    setForeground(Color.RED);
                };
            }
            for (String username:
                    DownUsernames) {
                if(nodeData.toString().equals(username)){
                    setForeground(Color.GRAY);
                };
            }
            //this.setText(nodeData.name);
            return this;
        }
    }


    public static List<String> addValue(String key){
        list.add(key);
        return list;
    }

    public static List<String> removeValue(String key){
        list.remove(key);
        return list;
    }

    public static void clear(){
        list.clear();
    }

}



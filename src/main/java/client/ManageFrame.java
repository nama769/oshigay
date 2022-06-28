package client;


import server.DrawPanel;
import server.Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import static client.View.model;

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

    //以下必须为静态的，否则在HandleClient里访问不到
    static DefaultTreeModel model;
    static DefaultMutableTreeNode root;
    static DrawPanel centerPanel;
    static List<String> list=new ArrayList<>();

    public ManageFrame() {

        width = (int)  screensize.getWidth();
        height =  (int) screensize.getHeight();
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
                String nodeName=selectionNode.toString();
                Server.curKey=nodeName;
            }
        });


        //设置树节点的样式
        DefaultTreeCellRenderer cr=new DefaultTreeCellRenderer();
        cr.setBackgroundNonSelectionColor(Color.darkGray);
        cr.setTextNonSelectionColor(Color.white);
        tree.setCellRenderer(cr);
        JScrollPane jsp=new JScrollPane(tree);
        JScrollBar bar=jsp.getHorizontalScrollBar();
        bar.setBackground(Color.darkGray);
        jsp.setBorder(null);
        leftPanel.add(jsp);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                Server.serverLive=false;
            }

        });
        centerPanel=new DrawPanel();
        container.add(new JScrollPane(centerPanel));
        frame.setSize(width,height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 监考端逻辑
     */
    public static void manageUtil(){
        while(true){

        }

    }




}

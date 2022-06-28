package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

    private static final int DEFAULT_WIDTH = 450;
    private static final int DEFAULT_HEIGHT = 300;
    private CardLayout card;
    private JPanel myPanel;
}

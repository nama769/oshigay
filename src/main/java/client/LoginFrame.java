package client;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * 登录/注册（用户管理模块），注册界面（账户名，密码，角色，【IP,MAC】），登录界面（账户名，密码）
 */
public class LoginFrame extends JFrame {
    public LoginFrame(int width,int height)
    {
        setTitle("登录窗口");
        setResizable(false);
        setLocation(350, 200);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //add main panel to frame

        JPanel mainPanel = new JPanel(new GridBagLayout());
        constraints = new GridBagConstraints();

        label1 = new JLabel("用户帐号", new ImageIcon("01.gif"), SwingConstants.CENTER);
        constraints.weightx = 100;
        constraints.weighty = 100;
        constraints.gridx = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        mainPanel.add(label1, constraints);

        myTextField = new JTextField();
        myTextField.setPreferredSize(new Dimension(120, 25));
        constraints.gridx = 0;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        mainPanel.add(myTextField, constraints);

        label2 = new JLabel("登录密码", new ImageIcon("02.gif"), SwingConstants.CENTER);
        constraints.gridx = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        mainPanel.add(label2, constraints);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(120, 25));
        constraints.gridx = 1;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        mainPanel.add(passwordField, constraints);

        label3 = new JLabel("用户类型", new ImageIcon("03.gif"), SwingConstants.CENTER);
        constraints.gridx = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        mainPanel.add(label3, constraints);

        userCombo = new JComboBox();
        userCombo.setPreferredSize(new Dimension(120, 25));
        userCombo.setEditable(false);
        userCombo.addItem("学生");
        userCombo.addItem("教师");
        userCombo.setSelectedItem("学生");
        selectedItem = (String)userCombo.getSelectedItem();
        constraints.gridx = 2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        mainPanel.add(userCombo, constraints);

        //get selected item, so we can decide to show which frame
        userCombo.addActionListener(new
                                            ActionListener()
                                            {
                                                public void actionPerformed(ActionEvent e)
                                                {
                                                    selectedItem = (String)userCombo.getSelectedItem();     //方法别忘了加括号
                                                }
                                            });
        add(mainPanel, BorderLayout.CENTER);

        //add button panel to the frame

        JPanel buttonPanel = new JPanel();
        //add login button
        JButton loginButton = new JButton("确定");
        loginButton.addActionListener(new
                                              ActionListener()
                                              {
                                                  public void actionPerformed(ActionEvent e)
                                                  {
                                                      loginDispose();

                                                      login=1;

                                                      if(login == 1)
                                                      {
                                                          if(selectedItem.equals("学生"))
                                                          {
                                                              JFrame f = new MonitorFrame();
                                                              f.setVisible(true);
                                                              dispose();
                                                              try {
                                                                  ((MonitorFrame) f).SendImage();
                                                              } catch (IOException ex) {
                                                                  throw new RuntimeException(ex);
                                                              }
                                                          }
                                                          else if(selectedItem.equals("教师"))
                                                          {
                                                              ManageFrame f = new ManageFrame();
                                                              dispose();
                                                          }
                                                      }
                                                  }
                                              });
        buttonPanel.add(loginButton);

        //add cancel button
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(new
                                               ActionListener()
                                               {
                                                   public void actionPerformed(ActionEvent e)
                                                   {
                                                       registerUtil();
                                                       System.exit(0);
                                                   }
                                               });
        buttonPanel.add(cancelButton);


        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loginDispose()
    {
        // String url = "jdbc:odbc:java"; //数据源名字为java
//        String url = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=学生选课管理系统";
////        String url = "jdbc:mysql://127.0.0.1:3306/学生选课管理系统?serverTimezone=UTC";
//        String username = "test";
//        String password = "testtest";
//        //加载驱动程序以连接数据库
//        try
//        {
////             Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
////            Class.forName("com.mysql.cj.jdbc.Driver");
//            loginConnection = DriverManager.getConnection( url, username, password );
//        }
//        //捕获加载驱动程序异常
//        catch ( ClassNotFoundException cnfex )
//        {
//            JOptionPane.showMessageDialog (LoginFrame.this, cnfex,
//                    "学生选课管理系统", JOptionPane.WARNING_MESSAGE );
//            System.exit( 1 );     // terminate program
//        }
//        //捕获连接数据库异常
//        catch ( SQLException sqlex )
//        {
//            //sqlex.printStackTrace();
//            JOptionPane.showMessageDialog (LoginFrame.this, "无法连接到SQL SERVER ，\n请确认SQL SERVER是否运行\n或数据源设置是否正确！ ",
//                    "学生选课管理系统", JOptionPane.WARNING_MESSAGE );
//            System.exit( 1 );  // terminate program
//        }

//        try
//        {
            String loginQuery;
            String loginUserName =  myTextField.getText();
            String loginPassword = new String(passwordField.getPassword());
//            if(myTextField.getText().equals( "" ))
//            {
//                JOptionPane.showMessageDialog( LoginFrame.this, "用户名必须为字母、数字和、汉字\n及其组合，不允许为空格键。",
//                        "登陆", JOptionPane.WARNING_MESSAGE );
//                //setTitle( "无记录显示" );
//                return;
//            }
//            if(selectedItem.equals("教师"))
//                loginQuery = "SELECT * FROM 教师表 WHERE(登陆帐号='" + loginUserName  + "' AND 登陆密码 ='" + loginPassword + "')";
//            else if(selectedItem.equals("管理员"))
//                loginQuery = "SELECT * FROM 管理员 WHERE(用户名='" + loginUserName + "' AND 密码 ='" + loginPassword + "')";
//            else //(selectedItem.equals("学生"))
//                loginQuery = "SELECT * FROM 学生基本信息表 WHERE(学号='" + loginUserName + "' AND 密码 ='" + loginPassword + "')";
//            loginStatement = loginConnection.createStatement();
//            System.out.println(loginQuery);  // XD
//            loginResultSet = loginStatement.executeQuery( loginQuery );
//            boolean Records = loginResultSet.next();
//            if ( ! Records )
//            {
//                JOptionPane.showMessageDialog(LoginFrame.this, "没有此用户或密码错误" );
//                return;
//            }
//            else
//            {
//                login = 1 ;
//            }
//            loginConnection.close();
//        }
//        catch(SQLException sqlex)
//        {
//            //sqlex.printStackTrace();
//            JOptionPane.showMessageDialog (LoginFrame.this, sqlex,
//                    "学生选课管理系统", JOptionPane.WARNING_MESSAGE );
//        }

    }


    private void registerUtil(){

    }

    private static final int DEFAULT_WIDTH  = 300;
    private static final int DEFAULT_HEIGHT = 200;
    private GridBagConstraints constraints;
    private JLabel label1, label2, label3;
    private JPasswordField passwordField;
    private JComboBox userCombo;
    private String selectedItem;
    private int login = 0;
    private Connection loginConnection;
    private Statement loginStatement;
    private ResultSet loginResultSet;

    public static JTextField myTextField;           //声明登陆名为全局变量!!!!!
}

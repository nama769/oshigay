package client;

import communication.Protocol;

import communication.Protocol;

import communication.Protocol;
import database.UserModel;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import static communication.Protocol.*;

/**
 * 登录/注册（用户管理模块），注册界面（账户名，密码，角色，【IP,MAC】），登录界面（账户名，密码）
 */
public class LoginFrame extends JFrame {
    public LoginFrame(int width, int height, final ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
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
                                            ActionListener() {
                                                public void actionPerformed(ActionEvent e) {
                                                    selectedItem = (String) userCombo.getSelectedItem();     //方法别忘了加括号
                                                }
                                            });
        add(mainPanel, BorderLayout.CENTER);

        //add button panel to the frame

        JPanel buttonPanel = new JPanel();
        //add login button
        JButton loginButton = new JButton("确定");
        loginButton.addActionListener(new
                                              ActionListener() {
                                                  public void actionPerformed(ActionEvent e) {
                                                      loginUtil();

//                                                      clientConfig.setLogin(1);

                                                      if(clientConfig.getLogin() == 1) {
                                                          /**
                                                           * 登录成功
                                                           */
                                                          if(clientConfig.getRole()==0) {
                                                              /**
                                                               * 考生
                                                               */
                                                              JFrame f = new MonitorFrame(clientConfig);
                                                              f.setVisible(true);
                                                              dispose();
                                                              MonitorFrame.monitorUtil(clientConfig);
                                                          }
                                                          else if(clientConfig.getRole()==1)
                                                          {
                                                              /**
                                                               * 老师
                                                               */
                                                              ManageFrame f = new ManageFrame(clientConfig);
                                                              dispose();
                                                              ManageFrame.manageUtil(clientConfig);
                                                          }
                                                      }
                                                  }
                                              });
        buttonPanel.add(loginButton);

        //add cancel button
        JButton cancelButton = new JButton("注册");
        cancelButton.addActionListener(new
                                               ActionListener() {

                                                   /**
                                                    * 注册逻辑
                                                    * @param e
                                                    */
                                                   public void actionPerformed(ActionEvent e) {
                                                       try {
                                                           registerUtil();
                                                       } catch (Exception ex) {
                                                           ex.printStackTrace();
                                                       }
                                                   }
                                               });
        buttonPanel.add(cancelButton);


        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 登录逻辑
     */
    private void loginUtil()  {
        String loginQuery;
        String loginUserName = myTextField.getText();
        String loginPassword = new String(passwordField.getPassword());
        int loginUserNameL = loginUserName.length();
        String loginUserNameLen = String.valueOf(loginUserNameL);
        int loginPasswordL = loginPassword.length();
        String loginPasswordLen = String.valueOf(loginPasswordL);
        String dataS = loginUserNameLen + loginUserName + loginPasswordLen + loginPassword;
        Protocol.send(TYPE_LOGIN, dataS.getBytes(), clientConfig.getDos());
        clientConfig.setState(UserModel.STATE_LOGINING);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (clientConfig.getState().equals(UserModel.STATE_LOGINING)){
            /**
             * wati server 处理登录请求
              */
        }

    }


    private void registerUtil() throws Exception {
        /**
         * 获取Username Password
         */
        String loginUserName = myTextField.getText();
        String loginPassword = new String(passwordField.getPassword());
        int loginUserNameTempLength = loginUserName.length();
        String loginUsernameLen = String.valueOf(loginUserNameTempLength);
        int loginPasswordTempLength = loginPassword.length();
        String loginPasswordLen = String.valueOf(loginPasswordTempLength);
        /**
         * 获取MAC地址，形如00-00-00-00-00-00
         */
        byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        String MACaddr = sb.toString();
        /**
         * 或取身份信息
         */
        String role = "";
        if (selectedItem.equals("学生")) {
            role = "0";
        } else if (selectedItem.equals("教师")) {
            role = "1";
        }
        String RegisterData = loginUsernameLen + loginUserName + loginPasswordLen + loginPassword + MACaddr + role;
        Protocol.send(Protocol.TYPE_REGISTER, RegisterData.getBytes(), clientConfig.getDos());
    }

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 200;
    private GridBagConstraints constraints;
    private JLabel label1, label2, label3;
    private JPasswordField passwordField;
    private JComboBox userCombo;
    private String selectedItem;
    private ClientConfig clientConfig;


    private Connection loginConnection;
    private Statement loginStatement;
    private ResultSet loginResultSet;

    public static JTextField myTextField;           //声明登陆名为全局变量!!!!!
}

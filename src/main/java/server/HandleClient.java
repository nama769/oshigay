package server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import communication.Protocol;
import communication.Result;
import database.DatabaseTool;
import database.UserModel;
import database.ImageModel;
import java.util.UUID;
import java.sql.*;

import static communication.Protocol.*;

/**
 * 信息处理模块，负责所有服务端的处理逻辑
 */
public class HandleClient implements Runnable{
	private Socket socket;
	private DataInputStream dis=null;
	private DataOutputStream dos=null;
	private String key=null;
	private boolean isLive=true;

	private DatabaseTool databaseTool;



	private ClientConfig clientConfig;
	private UserModel userModel;

	public HandleClient(Socket socket,ClientConfig clientConfig){
		this.clientConfig = clientConfig;
		this.socket=socket;
		databaseTool = new DatabaseTool();
		try {
			this.dis=new DataInputStream(socket.getInputStream());
			this.dos=new DataOutputStream(socket.getOutputStream());
//			Server.view=new View();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 每有一个客户端连接到服务端时，都会自动调用此方法。用于处理客户端发送的数据。
	 */
	public void run() {
		while(isLive){
			Result result = null;
			result = Protocol.getResult(dis);
			
			if(result!=null)
			handleType(result.getType(),result.getData());
		}
	}

	/**
	 * 用户注册 老郭
	 * 用户登录 老杨
	 * 图像保存 老蒋
	 * 图像查询 老刘
	 * 频率更改 老大
	 *
	 *
	 *
	 *
	 *
	 * @param type
	 * @param data
	 */
	//处理类型type的消息
	private void handleType(int type,byte[] data) {
		System.out.println(type);
		try{
			switch (type) {
//			case 1:
//				if(Server.curKey!=key) break;
//				ByteArrayInputStream bai=new ByteArrayInputStream(data);
//				BufferedImage buff=ImageIO.read(bai);
////				Server.view.centerPanel.setBufferedImage(buff);//为屏幕监控视图设置BufferedImage
////				Server.view.centerPanel.repaint();
//				bai.close();
//				break;
//			case 2:
//				String msg=new String(data);
//				if(msg.equals("client")) {
//					key=socket.getInetAddress().getHostAddress();
//					Server.client.put(key, socket);
////					Server.view.setTreeNode(Server.view.addValue(key));
//					if(Server.curKey==null) Server.curKey=key;
//				}
//				break;
//			case 3:
////				Server.view.setTreeNode(Server.view.removeValue(key));
//				Server.client.remove(key);
////				Server.view.centerPanel.setBufferedImage(null);
////				Server.view.centerPanel.repaint();
//				Server.curKey=null;
//				isLive=false;
//				break;
				/**
				 * 21-40
				 * 21用户注册，data格式为:字段长度+字段值，顺序为Username,Passwd,MAC,Role
				 */
			case TYPE_GRAPH:
				type_graph(data);
				break;
			case TYPE_LOGIN:
				type_login(data);
				break;
			case TYPE_REGISTER:
				type_register(data);
				break;
            case TYPE_CHANGE:
				type_change(data);
				break;
			case TYPE_FIND_IMAGE_BY_USERNAME:
				type_find_image_by_username(data);
				break;
			case TYPE_FIND_IMAGE_BY_IP:
				type_find_image_by_ip(data);
				break;
			case TYPE_FIND_IMAGE_BY_MAC:
				type_find_image_by_mac(data);
				break;
			default:
				break;
			}
		}catch(IOException exception){
			try {
				if(key!=null&&key.indexOf("client")!=-1) Server.client.remove(key);
				if(socket!=null) socket.close();
				exception.printStackTrace();
			} catch(IOException ez){
				ez.printStackTrace();
			}
		}
	}



	private void type_graph(byte[] data) throws IOException {
		ByteArrayInputStream ba=new ByteArrayInputStream(data);
		BufferedImage buf=ImageIO.read(ba);
		String imageUuid = UUID.randomUUID().toString().replace("-", "");
		long  imageTime = System.currentTimeMillis();
		ImageModel imagemodel=new ImageModel(imageUuid, imageTime,userModel.getID());
		databaseTool.addImage(imagemodel);
		File outputFile = new File(".\\images\\",imageUuid+".jpg");
		ImageIO.write(buf, "jpg", outputFile);
		byte fre[]={0x16};
		fre[0]=this.clientConfig.getFrequency();
		Protocol.send(TYPE_GRAPH,fre,dos);
	}

	private void type_login(byte[] data){
		String message_41 = new String(data);
		int UsernameLen_41 = Integer.parseInt(message_41.substring(0,1));
		int UsernameEIndex = 1 + UsernameLen_41;
		String Username = message_41.substring(1,UsernameEIndex);
		int PasswordLen = Integer.parseInt(message_41.substring(UsernameEIndex,UsernameEIndex+1));
		int PasswordBIndex = UsernameEIndex + 1;
		int PasswordEIndex = PasswordBIndex + PasswordLen;
		String Password_41 = message_41.substring(PasswordBIndex,PasswordEIndex);
		userModel=databaseTool.findUser(Username,Password_41);
		if(userModel==null){
			sendMessage("Sorry,please try again");
		}
		else {
			sendMessage("Login successfully!");
		}
	}

	private void type_register(byte[] data){
		String message = new String(data);
		String[] userMessage  = message.split("\n");
		/**
		 * 获取Username
		 */
		int UsernameLen_21 = Integer.parseInt(message.substring(0,1));
		int UsernameEndIndex = 1 + UsernameLen_21;
		String Username_21 = message.substring(1,UsernameEndIndex);
		int PasswordLen_21 = Integer.parseInt(message.substring(UsernameEndIndex,UsernameEndIndex+1));
		/**
		 * 获取Password
		 */
		int PasswordBeginIndex = UsernameEndIndex + 1;
		int PasswordEndIndex = PasswordBeginIndex + PasswordLen_21;
		String Password_21 = message.substring(PasswordBeginIndex,PasswordEndIndex);
//				int IPLen = Integer.parseInt((message.substring(PasswordEndIndex,PasswordEndIndex+1)));
//				int IPBeginIndex = PasswordEndIndex+1;
//				int IPEndIndex = IPBeginIndex + IPLen;
		key=socket.getInetAddress().getHostAddress();
		/**
		 * 获取MAC地址
		 */
		int MACLen = Integer.parseInt(message.substring(PasswordEndIndex,PasswordEndIndex+1));
		int MACBeginIndex = PasswordEndIndex + 1;
		int MACEndIndex = MACBeginIndex + MACLen;
		String MAC = message.substring(MACBeginIndex,MACEndIndex);
		/**
		 * 获取Role信息
		 */
		int Role = Integer.parseInt(message.substring(MACEndIndex,MACEndIndex+1));
		/**
		 * UUID gen
		 */
		String uuid = UUID.randomUUID().toString().replace("-", "");
		/**
		 * userModel传入database模块
		 */
		UserModel userModel = new UserModel(uuid,Username_21,Password_21,key,Role,MAC,UserModel.STATE_NO_LOGIN);
		boolean RegisterResult = databaseTool.addUser(userModel);
		String ReturnMsg = "";
		if(RegisterResult==true){
			ReturnMsg = "Register Succeed";
		}
		else{
			ReturnMsg = "Register Failed";
		}
		Protocol.send(TYPE_REGISTER,ReturnMsg.getBytes(),dos);
	}

	private void type_change(byte[] data){
		byte frequency=data[0];
		if(frequency!=clientConfig.getFrequency()){
			clientConfig.setFrequency(frequency);
		}
	}

	private void type_find_image_by_username(byte[] data){
		String message = new String(data);
		String s;
		s=databaseTool.findID_Username(message);
		databaseTool.findImageID(s);
	}

	private void type_find_image_by_ip(byte[] data){
		String message = new String(data);
		String i;
		i=databaseTool.findID_IP(message);
		databaseTool.findImageID(i);
	}
	private void type_find_image_by_mac(byte[] data){
		String message = new String(data);
		String m;
		m=databaseTool.findID_MAC(message);
		databaseTool.findImageID(m);
	}

	private void sendMessage(String message){
		Protocol.send(Protocol.TYPE_RETURN_MESSAGE,message.getBytes(StandardCharsets.UTF_8),dos);
	}

	/**
	 * 图片缩放
	 * @param bfImage
	 * @param scale
	 * @return
	 */
	public BufferedImage scale(BufferedImage bfImage,double scale){
		//截图压缩
		int width=bfImage.getWidth();
		int height=bfImage.getHeight();
		Image image = bfImage.getScaledInstance((int)(width * scale), (int)(height * scale), Image.SCALE_DEFAULT);  
        BufferedImage tag = new BufferedImage((int)(width * scale), (int)(height *scale), BufferedImage.TYPE_INT_RGB);     
        Graphics2D g = tag.createGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图   
        g.dispose();
		return tag;
	}
}

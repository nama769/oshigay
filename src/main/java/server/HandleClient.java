package server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class HandleClient implements Runnable {
	private Socket socket;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String key = null;
	private boolean isLive = true;

	private DatabaseTool databaseTool;


	private ClientConfig clientConfig;
	private UserModel userModel;

	public HandleClient(Socket socket, ClientConfig clientConfig, DatabaseTool databaseTool) {
		this.clientConfig = clientConfig;
		this.socket = socket;
		this.databaseTool = databaseTool;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
//			Server.view=new View();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 每有一个客户端连接到服务端时，都会自动调用此方法。用于处理客户端发送的数据。
	 */
	public void run() {
		while (isLive) {
			Result result = null;
			try {
				result = Protocol.getResult(dis);
//				try {
//					Thread.sleep(1500);
//				} catch (InterruptedException e) {
//					throw new RuntimeException(e);
//				}
			} catch (IOException e) {
				/**
				 * 掉线处理函数
				 */
				clientConfig.addDownClient(userModel);
				System.out.println(userModel.getUsername() + " 已下线！");
				isLive = false;
			}

			if (result != null)
				handleType(result.getType(), result.getData());
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
	private void handleType(int type, byte[] data) {
		System.out.println(type);
		try {
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
					System.out.println(getFormatTime() + " Server端保存了来自 " + userModel.getUsername() + " 的图片：" + type_graph(data));
					break;
				case TYPE_LOGIN:
					type_login(data);
					if (userModel != null) {
						System.out.println(getFormatTime() + " Server端接受了来自 " + userModel.getUsername() + " 的登录请求");
					} else {
						System.out.println(getFormatTime() + " Server端拒绝了来自 " + socket.getInetAddress().getHostAddress() + " 的非法登录请求");
					}
					break;
				case TYPE_REGISTER:
					type_register(data);
					System.out.println(getFormatTime() + " Server端处理了来自 " + socket.getInetAddress().getHostAddress() + " 的注册请求");
					break;
				case TYPE_CHANGE:
					type_change(data);
					System.out.println(getFormatTime() + " Server端更改了截图频率为 " + (int) clientConfig.getFrequency());
					break;
				case TYPE_FIND_IMAGE_BY_USERNAME:
					type_find_image_by_username(data);
					System.out.println(getFormatTime() + " Server端收到了图片查询请求 查询参数：username=" + new String(data));
					break;
				case TYPE_FIND_IMAGE_BY_IP:
					type_find_image_by_ip(data);
					System.out.println(getFormatTime() + " Server端收到了图片查询请求 查询参数：ip=" + new String(data));
					break;
				case TYPE_FIND_IMAGE_BY_MAC:
					type_find_image_by_mac(data);
					System.out.println(getFormatTime() + " Server端收到了图片查询请求 查询参数：MAC=" + new String(data));
					break;
				case TYPE_GET_IMAGE:
					System.out.println(getFormatTime() + " Server端收到了对于 " + new String(data) + " 的最新图片查询请求：" + type_get_image(data));
					break;
				case TYPE_LOAD_IMAGE:
					type_load_image(data);
					System.out.println(getFormatTime() + " Server端收到了对于 " + new String(data) + " 的图片传输请求");
					break;
				case TYPE_STUDENT_VIOLATE_SERVER:
					if(!clientConfig.getViolateClient().contains(userModel)){
						type_student_violate_server(userModel.getUsername().getBytes(StandardCharsets.UTF_8));
						clientConfig.getViolateClient().add(userModel);
						System.out.println(getFormatTime() + " Server向老师端发送了 " + userModel.getUsername() + " 的违规记录");
					}
					break;
				case TYPE_CHANGE_BLACK_LIST:
					type_change_black_list(data);
					System.out.println(getFormatTime() + " Server端收到了新的黑名单列表： " + new String(data));
					break;
				default:
					break;
			}
		} catch (IOException exception) {
			try {
				if (key != null && key.indexOf("client") != -1) Server.client.remove(key);
				if (socket != null) socket.close();
				exception.printStackTrace();
			} catch (IOException ez) {
				ez.printStackTrace();
			}
		}
	}


	private String type_graph(byte[] data) throws IOException {
		ByteArrayInputStream ba = new ByteArrayInputStream(data);
		BufferedImage buf = ImageIO.read(ba);
		String imageUuid = UUID.randomUUID().toString().replace("-", "");
		long imageTime = System.currentTimeMillis();
		ImageModel imagemodel = new ImageModel(imageUuid, imageTime, userModel.getID());
		databaseTool.addImage(imagemodel);
		File outputFile = new File(clientConfig.getImageSavePath(), imageUuid + ".jpg");
		ImageIO.write(buf, "jpg", outputFile);
		byte fre[] = {0x16};
		fre[0] = this.clientConfig.getFrequency();
		Protocol.send(TYPE_GRAPH, fre, dos);
		/**
		 * 从clientConfig获取黑名单并返回给客户端
		 */
		Protocol.send(TYPE_SEND_BLACK_LIST_TO_CLIENT,clientConfig.getBlackListString().getBytes(StandardCharsets.UTF_8),dos);
		clientConfig.setUserNewImage(userModel.getUsername(), imagemodel.getID());
		System.out.println(clientConfig.getUserImageMap());
		return imagemodel.getID();
	}

	private void type_login(byte[] data) {
		String message_41 = new String(data);
		int UsernameLen_41 = Integer.parseInt(message_41.substring(0, 1));
		int UsernameEIndex = 1 + UsernameLen_41;
		String Username = message_41.substring(1, UsernameEIndex);
		int PasswordLen = Integer.parseInt(message_41.substring(UsernameEIndex, UsernameEIndex + 1));
		int PasswordBIndex = UsernameEIndex + 1;
		int PasswordEIndex = PasswordBIndex + PasswordLen;
		String Password_41 = message_41.substring(PasswordBIndex, PasswordEIndex);
		userModel = databaseTool.findUser(Username, Password_41);
		if (userModel == null) {
			sendMessage(TYPE_LOGIN_REPAY, "Sorry,please try again");
		} else {
			sendMessage(TYPE_LOGIN_REPAY, "Login successfully!\n" + userModel.getRole());
			clientConfig.addClient(userModel);
			if (userModel.getRole() == 1) {
				clientConfig.setDosTeacher(dos);
			} else {
				if (clientConfig.getDosTeacher() != null) {
					Protocol.send(TYPE_STUDENT_UP, userModel.getUsername().getBytes(StandardCharsets.UTF_8), clientConfig.getDosTeacher());
				}
			}
		}
	}

	private void type_register(byte[] data) {
		String message = new String(data);
		String[] userMessage = message.split("\n");
		/**
		 * 获取Username
		 */
		int UsernameLen_21 = Integer.parseInt(message.substring(0, 1));
		int UsernameEndIndex = 1 + UsernameLen_21;
		String Username_21 = message.substring(1, UsernameEndIndex);
		int PasswordLen_21 = Integer.parseInt(message.substring(UsernameEndIndex, UsernameEndIndex + 1));
		/**
		 * 获取Password
		 */
		int PasswordBeginIndex = UsernameEndIndex + 1;
		int PasswordEndIndex = PasswordBeginIndex + PasswordLen_21;
		String Password_21 = message.substring(PasswordBeginIndex, PasswordEndIndex);
//				int IPLen = Integer.parseInt((message.substring(PasswordEndIndex,PasswordEndIndex+1)));
//				int IPBeginIndex = PasswordEndIndex+1;
//				int IPEndIndex = IPBeginIndex + IPLen;
		key = socket.getInetAddress().getHostAddress();
		/**
		 * 获取MAC地址
		 */
		int MACLen = 17;
		int MACBeginIndex = PasswordEndIndex;
		int MACEndIndex = MACBeginIndex + MACLen;
		String MAC = message.substring(MACBeginIndex, MACEndIndex);
		/**
		 * 获取Role信息
		 */
		int Role = Integer.parseInt(message.substring(MACEndIndex, MACEndIndex + 1));
		/**
		 * UUID gen
		 */
		String uuid = UUID.randomUUID().toString().replace("-", "");
		/**
		 * userModel传入database模块
		 */
		UserModel userModel = new UserModel(uuid, Username_21, Password_21, key, Role, MAC, UserModel.STATE_NO_LOGIN);
		boolean RegisterResult = databaseTool.addUser(userModel);
		String ReturnMsg = "";
		if (RegisterResult == true) {
			ReturnMsg = "Register Succeed";
		} else {
			ReturnMsg = "Register Failed";
		}
		Protocol.send(TYPE_REGISTER, ReturnMsg.getBytes(), dos);
	}

	private void type_change(byte[] data) {
		byte frequency = data[0];
		if (frequency != clientConfig.getFrequency()) {
			clientConfig.setFrequency(frequency);
		}
	}

	private void type_find_image_by_username(byte[] data) {
		String message = new String(data);
		String s;
		String []imageIds;
		String result="";
		s=databaseTool.findID_Username(message);
		imageIds=databaseTool.findImageID(s);
		for(String i:imageIds){
			result+=(i+"\n");
		}
		Protocol.send(Protocol.TYPE_RETURN_IMAGE_ID_BY_USERNAME,result.getBytes(StandardCharsets.UTF_8),dos);
	}

	private void type_find_image_by_ip(byte[] data) {
		String message = new String(data);
		String i;
		String []imageIds;
		String result="";
		i=databaseTool.findID_IP(message);
		imageIds=databaseTool.findImageID(i);
		for(String j:imageIds){
			result+=(j+"\n");
		}
		Protocol.send(Protocol.TYPE_RETURN_IMAGE_ID_BY_IP,result.getBytes(StandardCharsets.UTF_8),dos);
	}

	private void type_find_image_by_mac(byte[] data) {
		String message = new String(data);
		String m;
		String []imageIds;
		String result="";
		m=databaseTool.findID_MAC(message);
		imageIds=databaseTool.findImageID(m);
		for(String i:imageIds){
			result+=(i+"\n");
		}
		Protocol.send(Protocol.TYPE_RETURN_IMAGE_ID_BY_MAC,result.getBytes(StandardCharsets.UTF_8),dos);
	}

	private String type_get_image(byte[] data) {
		String username = new String(data);
		Protocol.send(TYPE_RET_SELECT_IMAGEID, clientConfig.getUserImageMap().get(username).getBytes(StandardCharsets.UTF_8), dos);
		return clientConfig.getUserImageMap().get(username);
	}

	private void type_load_image(byte[] data) {
		String imageid = new String(data);
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			File image = new File(clientConfig.getImageSavePath() + imageid + ".jpg");
			BufferedImage bfImage = (BufferedImage) ImageIO.read(image);
			ImageIO.write(bfImage, "png", bao);
			Protocol.send(TYPE_RET_IMAGE, bao.toByteArray(), dos);
			bao.close();
		} catch (IOException e) {
			System.out.println(getFormatTime()+"加载图像失败："+imageid);
		}

	}

	private void type_student_violate_server(byte[] data) {
		String violateUsername = new String(data);
		Protocol.send(TYPE_STUDENT_VIOLATE,violateUsername.getBytes(StandardCharsets.UTF_8),clientConfig.getDosTeacher());
	}

	private void type_change_black_list(byte[] data) {
		String blackList = new String(data);
		clientConfig.setAppBlackList(blackList.split(" "));
	}


	private void sendMessage(int type, String message) {
		Protocol.send(type, message.getBytes(StandardCharsets.UTF_8), dos);
	}

	/**
	 * 图片缩放
	 * @param bfImage
	 * @param scale
	 * @return
	 */
	public BufferedImage scale(BufferedImage bfImage, double scale) {
		//截图压缩
		int width = bfImage.getWidth();
		int height = bfImage.getHeight();
		Image image = bfImage.getScaledInstance((int) (width * scale), (int) (height * scale), Image.SCALE_DEFAULT);
		BufferedImage tag = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = tag.createGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图   
        g.dispose();
		return tag;
	}

	public static String getFormatTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		java.util.Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
}

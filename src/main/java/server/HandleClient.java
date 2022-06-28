package server;
import java.util.UUID;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultMutableTreeNode;

import communication.Protocol;
import communication.Result;
import database.DatabaseTool;
import database.UserModel;

/**
 * 信息处理模块，负责所有服务端的处理逻辑
 */
public class HandleClient implements Runnable{
	private Socket socket;
	private DataInputStream dis=null;
	private DataOutputStream dos=null;
	private String key=null;
	private boolean isLive=true;

	private long imageTime= Long.parseLong(null);

	private DatabaseTool databaseTool;

	private ClientConfig clientConfig;
	private  byte[]  another;

	String imageUuid;


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
			case 1:
				if(Server.curKey!=key) break;
				ByteArrayInputStream bai=new ByteArrayInputStream(data);
				BufferedImage buff=ImageIO.read(bai);
				Server.view.centerPanel.setBufferedImage(buff);//为屏幕监控视图设置BufferedImage
				Server.view.centerPanel.repaint();
				bai.close();

				break;
			case 2:
				String msg=new String(data);
				if(msg.equals("client")) {
					key=socket.getInetAddress().getHostAddress();
					Server.client.put(key, socket);
					Server.view.setTreeNode(Server.view.addValue(key));
					if(Server.curKey==null) Server.curKey=key;
				}
				break;
			case 61:
				ByteArrayInputStream gra=new ByteArrayInputStream(data);
				BufferedImage buf=ImageIO.read(gra);
				imageTime=System.currentTimeMillis();
				this.imageUuid= UUID.randomUUID().toString().replace("-", "");
				databaseTool.addImage(imageUuid,imageTime,usermodel.ID);
				File outFile = new File(".\\images\\",imageUuid+".png");
				ImageIO.write(buf, "png", outFile);
				another[0]=this.clientConfig.getFrequency();
				Protocol.send(Protocol.TYPE_GRAPH,another,this.dos);
//				Server.view.setTreeNode(Server.view.removeValue(key));
//				Server.client.remove(key);
//				Server.view.centerPanel.setBufferedImage(null);
//				Server.view.centerPanel.repaint();
//				Server.curKey=null;
//				isLive=false;
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

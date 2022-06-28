package server;

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

/**
 * 信息处理模块，负责所有服务端的处理逻辑
 */
public class HandleClient implements Runnable{
	private Socket socket;
	private DataInputStream dis=null;
	private DataOutputStream dos=null;
	private String key=null;
	private boolean isLive=true;
	public HandleClient(Socket socket){
		this.socket=socket;
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
			case 3:
				Server.view.setTreeNode(Server.view.removeValue(key));
				Server.client.remove(key);
				Server.view.centerPanel.setBufferedImage(null);
				Server.view.centerPanel.repaint();
				Server.curKey=null;
				isLive=false;
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

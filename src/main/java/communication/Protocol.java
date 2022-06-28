package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 封装通信时的数据格式
 * 通过扩展
 *TYPE_xxx来指明发送的不同的情况
 * 负责数据的发送与接收，负责发送前加密，接收后解密（密钥采用硬编码，加解密算法使用RC4）。
 * 解密后的数据封装到Result类。
 */
public class Protocol {

	/**
	 * 此位置可以任意扩展扩展。但要求不能有冲突（值不能相同，且为0-255）
	 */
	public static int TYPE_IMAGE=1;//图片
	public static int TYPE_LOAD=2;//登录
	public static int TYPE_LOGOUT=3;//退出
	public static int TYPE_REGISTER=21;//注册
	public static int TYpe_REGISTER=22;
	public static int TYPE_GRAPH=61;//图片
	/**
	 * 老张 1-20
	 * 老郭 21-40
	 * 老杨 41-60
	 * 老蒋 61 -80
	 * 老刘 81 -100
	 * 老大 101 -120
	 *
	 */


	public static int TYPE_RETURN_MESSAGE=5;//消息回应
    public static int TYPE_CHANGE=101;//频率更改
	/**
	 * 发送函数，
	 * @param type 类型
	 * @param bytes 传输的数据
	 * @param dos 输入连接流
	 */
	public static void send(int type,byte[] bytes,DataOutputStream dos){
		int totalLen=1+4+bytes.length;
			try {
				dos.writeByte(type);
				dos.writeInt(totalLen);
				dos.write(bytes);
				dos.flush();
			} catch (IOException e) {
				System.exit(0);
			}
			
	}

	/**
	 * 接收函数，
	 * @param dis 输出流连接
	 * @return 返回封装好的Result(包含类型，数据)
	 */
	public static Result getResult(DataInputStream dis){
			
			try {
				byte type = dis.readByte();
				int totalLen=dis.readInt();
				byte[] bytes=new byte[totalLen-4-1];
				dis.readFully(bytes);
				return new Result(type&0xFF,totalLen,bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	}
}
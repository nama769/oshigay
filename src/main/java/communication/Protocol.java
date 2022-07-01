package communication;

import util.RC4Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 封装通信时的数据格式
 * 通过扩展
 * TYPE_xxx来指明发送的不同的情况
 * 负责数据的发送与接收，负责发送前加密，接收后解密（密钥采用硬编码，加解密算法使用RC4）。
 * 解密后的数据封装到Result类。
 */
public class Protocol {

    /**
     * 此位置可以任意扩展扩展。但要求不能有冲突（值不能相同，且为0-255）
     */
    public static final int TYPE_IMAGE = 1;//图片
    public static final int TYPE_LOAD = 2;//登录
    public static final int TYPE_LOGOUT = 3;//退出
    public static final int TYPE_REGISTER = 21;//注册
    public static final int TYpe_REGISTER = 22;
    public static final int TYPE_GRAPH = 61;//图片

    public static final int TYPE_GET_IMAGE = 4;//老师端向服务端请求图片
    public static final int TYPE_STUDENT_UP = 6;//服务端向老师报告有新同学上线
    public static final int TYPE_RET_SELECT_IMAGEID = 7;//向老师返回他想请求的最新imageid
    public static final int TYPE_LOAD_IMAGE = 8;//通过imageid返回图像
    public static final int TYPE_RET_IMAGE = 9;//返回图像数据
    /**
     * 老张 1-20
     * 老郭 21-40
     * 老杨 41-60
     * 老蒋 61 -80
     * 老刘 81 -100
     * 老大 101 -120
     */
	public static final int TYPE_STUDENT_VIOLATE_SERVER = 10;//向服务端报告同学违规
	public static final int TYPE_STUDENT_DOWN_SERVER = 11;//向服务端报告同学下线

	public static final int TYPE_STUDENT_VIOLATE = 12;//服务端向老师报告同学违规
	public static final int TYPE_STUDENT_DOWN = 13;//服务端向老师报告有同学下线

	public static final int TYPE_CHANGE_BLACK_LIST = 14;//老师端向学生端更新黑名单
	public static final int TYPE_SEND_BLACK_LIST_TO_CLIENT = 15;//老师端向学生端更新黑名单
	/**
	 * 老张 1-20
	 * 老郭 21-40
	 * 老杨 41-60
	 * 老蒋 61 -80
	 * 老刘 81 -100
	 * 老大 101 -120
	 *
	 */


    public static final int TYPE_RETURN_MESSAGE = 5;//消息回应
    public static final int TYPE_CHANGE = 101;//频率更改

    public static final int TYPE_LOGIN = 41;//登录
    public static final int TYPE_LOGIN_REPAY = 42;//登录

    public static final int TYPE_FIND_IMAGE_BY_USERNAME = 81;//通过username查找imagesid
    public static final int TYPE_FIND_IMAGE_BY_IP = 82;//通过ip查找imagesid
    public static final int TYPE_FIND_IMAGE_BY_MAC = 83;//通过mac查找imagesid

    private static final String RC4KEY = "abcdefg";

    /**
     * 发送函数，
     *
     * @param type  类型
     * @param bytes 传输的数据
     * @param dos   输入连接流
     */
    public static void send(int type, byte[] bytes, DataOutputStream dos) {
        byte[] enBytes = RC4Util.RC4Base(bytes,RC4KEY);
        int totalLen = 1 + 4 + enBytes.length;
        try {
            dos.writeByte(type);
            dos.writeInt(totalLen);
            dos.write(enBytes);
            dos.flush();
        } catch (IOException e) {
            System.exit(0);
        }

    }

    public static final int TYPE_RETURN_IMAGE_ID_BY_USERNAME = 84;

    public static final int TYPE_RETURN_IMAGE_ID_BY_IP = 85;

    public static final int TYPE_RETURN_IMAGE_ID_BY_MAC = 86;

    //RC4加解密算法
//    public static String HloveyRC4(String aInput, String aKey) {
//        int[] iS = new int[256];
//        byte[] iK = new byte[256];
//        for (int i = 0; i < 256; i++)
//            iS[i] = i;
//        int j = 1;
//        for (short i = 0; i < 256; i++) {
//            iK[i] = (byte) aKey.charAt((i % aKey.length()));
//        }
//        j = 0;
//        for (int i = 0; i < 255; i++) {
//            j = (j + iS[i] + iK[i]) % 256;
//            int temp = iS[i];
//            iS[i] = iS[j];
//            iS[j] = temp;
//        }
//        int i = 0;
//        j = 0;
//        char[] iInputChar = aInput.toCharArray();
//        char[] iOutputChar = new char[iInputChar.length];
//        for (short x = 0; x < iInputChar.length; x++) {
//            i = (i + 1) % 256;
//            j = (j + iS[i]) % 256;
//            int temp = iS[i];
//            iS[i] = iS[j];
//            iS[j] = temp;
//            int t = (iS[i] + (iS[j] % 256)) % 256;
//            int iY = iS[t];
//            char iCY = (char) iY;
//            iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
//        }
//        return new String(iOutputChar);
//    }

    /**
     * 发送函数，
     *
     * @param type  类型
     * @param bytes 传输的数据
     * @param dos   输入连接流
     */
//    public static void send(int type, byte[] bytes, DataOutputStream dos) {
//        String mingwen = new String(bytes);
//        String miwen = HloveyRC4(mingwen, "abcdefg");
//        byte[] bytes1 = miwen.getBytes();
//        int totalLen = 1 + 4 + bytes1.length;
//        try {
//            dos.writeByte(type);
//            dos.writeInt(totalLen);
//            dos.write(bytes1);
//            dos.flush();
//        } catch (IOException e) {
//            System.exit(0);
//        }
//
//    }

    /**
     * 接收函数，
     *
     * @param dis 输出流连接
     * @return 返回封装好的Result(包含类型 ， 数据)
     */
    public static Result getResult(DataInputStream dis) throws IOException {
        byte type = dis.readByte();
        int totalLen = dis.readInt();
        byte[] bytes = new byte[totalLen - 4 - 1];
        dis.readFully(bytes);
        byte[] deBytes = RC4Util.RC4Base(bytes,RC4KEY);
        return new Result(type & 0xFF, totalLen, deBytes);
    }

    /**
     * 接收函数，
     *
     * @param dis 输出流连接
     * @return 返回封装好的Result(包含类型 ， 数据)
     */
//    public static Result getResult(DataInputStream dis) throws IOException {
//        byte type = dis.readByte();
//        int totalLen = dis.readInt();
//        byte[] bytes = new byte[totalLen - 4 - 1];
//        dis.readFully(bytes);
//        String miwen = new String(bytes);
//        String mingwen = HloveyRC4(miwen, "abcdefg");
//        bytes = mingwen.getBytes();
//        return new Result(type & 0xFF, totalLen, bytes);
//    }
}
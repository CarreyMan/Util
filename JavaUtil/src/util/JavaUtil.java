package util;

import com.rerloan.basic.exception.TranFailException;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class JavaUtil {

    private static final String STR_FORMAT = "000000";
    /**
	 * 生成验证码
	 */
    public static String identifyCode(){
        String str = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
        String str2[] = str.split(",");//将字符串以,分割
        Random rand = new Random();//创建Random类的对象rand
        int index = 0;
        String randStr = "";//创建内容为空字符串对象randStr
        randStr = "";//清空字符串对象randStr中的值
        for (int i=0; i<4; ++i){
            index = rand.nextInt(str2.length-1);//在0到str2.length-1生成一个伪随机数赋值给index
            randStr += str2[index];//将对应索引的数组与randStr的变量值相连接
        }
        return randStr;
    }

    public static String haoAddOne(String liuShuiHao){
        Integer intHao = Integer.parseInt(liuShuiHao);
        intHao++;
        DecimalFormat df = new DecimalFormat(STR_FORMAT);
        return df.format(intHao);
    }

    /*
     * 6、字符串转换成日期
     */
    public static Date StringToDate(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 7、日期转换成字符串
     *
     * @param date 日期对象
     * @param pattern 转换成字符串格式 例如："yyyyMMddHHmmss"
     * @return
     * @throws TranFailException
     */
    public static String DateToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /*
     * 4、创建文件路径,如果文件不存在就创建，如果存在就返回true
     */
    public static boolean createDirectory(String path) {
        File wf = new File(path);
        return wf.exists() ? true : wf.mkdirs();
    }

    /**
     * 隐藏身份证号
     * @param certNo
     * @return
     */
    public static String certNoHide(String certNo){
        if(StringUtil.isBlank(certNo)){
            return "";
        }
        return certNo.replaceAll("(\\d{12})(\\w{0,6})","$1******");
    }

    /**
     * 隐藏手机号
     * @param phone
     * @return
     */
    public static String phoneHide(String phone){
        if(StringUtil.isBlank(phone)){
            return "";
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    }

    /**
     * inputStream转byte[]
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 去掉右侧的0 如果没有小数点直接返回
     *
     * @param str
     * @param rpx
     * @return
     */
    public static String trimUtil(String str, char rpx) {
        if(StringUtil.isBlank(str)){
            return "";
        }
        if(!str.contains(".")){
            return str;
        }
        int len = str.length();
        int st = 0;
        char[] val = str.toCharArray();
		/*
		 * while ((st < len) && (val[st] == rpx)) { st++; }
		 */
        while ((st < len) && (val[len - 1] == rpx)) {
            len--;
        }
        String strVal = ((st > 0) || (len < str.length())) ? str.substring(st,
                len) : str;
        if (strVal.lastIndexOf(".") == strVal.length() - 1)
            strVal = strVal + "00";
        return strVal;

    }

    public static void close(Closeable closeable) throws TranFailException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                return;
            }
        }
    }

    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static byte[] getBytes(InputStream fis){
        byte[] buffer = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    /*
	 * 5、数字转大写
	 */
    private static final String UNIT = "万仟佰拾亿仟佰拾万仟佰拾元角分";
    private static final String DIGIT = "零壹贰叁肆伍陆柒捌玖";
    private static final double MAX_VALUE = 9999999999999.99D;

    public static String digitUppercase(double v) {
        if (v < 0 || v > MAX_VALUE) {
            return "参数非法!";
        }
        long l = Math.round(v * 100);
        if (l == 0) {
            return "零元整";
        }
        String strValue = l + "";
        // i用来控制数
        int i = 0;
        // j用来控制单位
        int j = UNIT.length() - strValue.length();
        String rs = "";
        boolean isZero = false;
        for (; i < strValue.length(); i++, j++) {
            char ch = strValue.charAt(i);
            if (ch == '0') {
                isZero = true;
                if (UNIT.charAt(j) == '亿' || UNIT.charAt(j) == '万'
                        || UNIT.charAt(j) == '元') {
                    rs = rs + UNIT.charAt(j);
                    isZero = false;
                }
            } else {
                if (isZero) {
                    rs = rs + "零";
                    isZero = false;
                }
                rs = rs + DIGIT.charAt(ch - '0') + UNIT.charAt(j);
            }
        }
        if (!rs.endsWith("分")) {
            rs = rs + "整";
        }
        rs = rs.replaceAll("亿万", "亿");
        return rs;
    }


}

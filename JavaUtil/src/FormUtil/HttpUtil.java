package FormUtil;


import com.rerloan.basic.dic.UtilErrorMsg;
import com.rerloan.basic.exception.TranFailException;
import com.xboot.web.util.constant.XBootWebErrorCode;
import net.sf.json.JSONObject;
import util.JsonUtil;
import util.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static String JSON_START = "{";  //JSON数据格式开头
    private static String RESPONSE_CODE = "resultCode";  //响应报文码
    private static String SUCCESS_CODE = "0000";  //响应报文码

    /**http响应信息
     * @param data
     * @param response
     */
    public static void httpResponse(Object data, HttpServletResponse response){
        PrintWriter writer = null;
        try {
            /*
             * 1、返回响应报文
             */
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
            String encode = "UTF-8";
            String req = JsonUtil.toJson(data);
            writer.print(new String(req.getBytes(encode), encode));
            writer.flush();
        } catch (Exception e) {
            LogUtil.handerEx(UtilErrorMsg.PUB_RESPONSE_PARAM, e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /*
     * 17、封装request成map对象 参数名作为key，参数值作为value
     */
    public static Map<String, Object> getMap(HttpServletRequest request) throws TranFailException {
        Map<String, Object> bm = new HashMap<String, Object>();
        try {
            Map<String, String[]> tmp = request.getParameterMap();
            if (tmp != null) {
                for (String key : tmp.keySet()) {
                    key = key.trim();
                    String[] values = tmp.get(key);
                    Object obj = values.length == 1 ? values[0].trim() : values;
                    bm.put(key, obj == null ? "" : obj);
                }
            }
            int currentPage =
                    ((bm.get("currentPage") == null || "".equals(bm.get("currentPage"))) ? 1 : Integer.parseInt(bm.get("currentPage").toString())); // 如果当前页为空则赋值1
            bm.put("currentPage", currentPage);
        } catch (Exception e) {
            throw LogUtil.handerEx(UtilErrorMsg.ERROR_REQUEST_TO_MAP, LogUtil.EMPTY, e);
        }
        return bm;
    }

    /**
     * 读取request请求，并返回请求报文
     * @param request
     * @return String 请求报文
     * @throws TranFailException
     */
    public static String getRequestString(HttpServletRequest request) throws TranFailException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        String str = null;
        try{
            br = request.getReader();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            LogUtil.info("请求报文读流完毕");
            str = new String(sb.toString().getBytes(), "UTF-8");
        }catch(Exception e){
            throw LogUtil.handerEx(UtilErrorMsg.PUB_REQUEST_PARAM, LogUtil.EMPTY,e);
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw LogUtil.handerEx(UtilErrorMsg.PUB_REQUEST_COLSE,LogUtil.ERROR,e);
                }
            }
        }
        return str;
    }

    /**
     * 处理响应报文
     *
     * @param result ： 响应内容
     */
    public static JSONObject dealResponse(String result) throws TranFailException {
        JSONObject resultJson;
        try {
            if (result.startsWith(JSON_START) && result.contains(RESPONSE_CODE)) { // 是否以标准返回为开头
                resultJson = JSONObject.fromObject(result);
                String errorCode = resultJson.getString(RESPONSE_CODE);
                if (!(SUCCESS_CODE.equals(errorCode))) { // 是否成功
                    throw LogUtil.handerEx(XBootWebErrorCode.PUB_RETCODE_EXCEPTION, LogUtil.EMPTY);
                }
            } else {
                throw LogUtil.handerEx(XBootWebErrorCode.PUB_DATA_FORMAT, LogUtil.EMPTY);
            }
            resultJson.remove("resultCode");
            resultJson.remove("resultMsg");
        } catch (Exception e) {
            throw LogUtil.handerEx(XBootWebErrorCode.PUB_RESPONSE_PARAM, "返回报文" + result + "写入流失败", e);
        }
        return resultJson;
    }
}

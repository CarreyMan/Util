package util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.rerloan.basic.exception.OssErrorMsg;
import com.rerloan.basic.exception.TranFailException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;


public class OssUtil{

    private static final Logger LOG = LoggerFactory.getLogger(OssUtil.class);
    private static final String accessKeyId = "LTAI1uctydtmEAjN"; // 阿里云账号生成的key
    private static final String accessKeySecret = "rPN0KbOJQaBLnEAgXEPgjp89csQRuU"; // 阿里云账号生成的secret
    private static final String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static final String HTTP = "http://";
    private static final String POINT=".oss-cn-beijing.aliyuncs.com";
    private static final String SEPARATOR = "/";
    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public static String getUrl(String bucketName,String key) throws Exception{
        try {
            StringBuffer stringBuffer=new StringBuffer();
            String url=stringBuffer.append(HTTP).append(bucketName).append(POINT).append(SEPARATOR).append(key).toString();
            if (url != null) {
                return url.toString();
            }
        } catch (Exception e) {
            LOG.error("获取URL失败,bucket:{} key:{} 错误信息:{}", bucketName, key, e.getMessage());
            throw e;
        }
        return null;
    }

    public static void deleteObject(String bucketName, String key) throws Exception {
        OSSClient ossClient = null;
        try {
            ossClient = creatClient();
            ossClient.deleteObject(bucketName,key);
        } catch (Exception e) {
            LogUtil.error("删除图片失败");
            throw e;
        }
    }

    /**
     * 创建OSS初始化对象
     * 
     * @return
     * @throws Exception
     */
    public static OSSClient creatClient() throws TranFailException {
        OSSClient client = null;
        try {
            client = new OSSClient(endpoint, accessKeyId, accessKeySecret); // 初始化一个OSSClient
        } catch (Exception e) {
            throw LogUtil.handerEx(OssErrorMsg.ERROR_OSS_PUB, LogUtil.EMPTY, e);
        }
        return client;
    }

    public static String putObject(String bucketName, InputStream in) {
        String key = null;
        try {
            String inStr=in.toString();
            key = DigestUtils.md5Hex(inStr);// 文件的MD5值
        } catch (Exception e) {}
        if(key == null) return null;
        return putObject(bucketName, key, in);
    }
    /**
     * OSS上传文件 备注： (1)每个系统对应一个bucket[类似于文件目录] (2)上传文件的MD5值作为key
     * 
     * @param bucketName：bucketName
     * @param in：文件流
     * @return 文件的md5值+文件扩展名
     * @throws Exception
     */

    public static String putObject(String bucketName, String key, InputStream in) {
        InputStream fileContent = null; // 文件内容流
        OSSClient client = null;
        try {
            /*
             * 1、初始化client
             */
            client = creatClient();
            client.putObject(bucketName, key, in);// 上传Object.
        } catch (Exception e) {
            LOG.error("上传OSS失败,bucket:{} key:{} 错误信息:{}", bucketName, key, e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(fileContent);
            client.shutdown();
        }
        return key;
    }

    
    /**
     * OSS下载文件
     * 
     * @param bucketName：文件bucketName
     * @param key:上传时设置的文件标识【文件的MD5值】
     * @throws Exception
     */

    public static void getObject(String bucketName, String key,OutputStream out) throws Exception {
        InputStream fileStream = null;
        OSSClient client = null;
        try {
            /*
             * 1、初始化client
             */
            client = creatClient();
            if( bucketName == null ){
            	return;
            }
            /*
             * 2、下载存储文件流
             */
            OSSObject ossObject = client.getObject(bucketName, key);
            if( ossObject == null ){
            	return;
            }
            byte[] buf = new byte[1024];
            fileStream = ossObject.getObjectContent();
            int len = 0; 
            while( (len = fileStream.read(buf)) > 0 ) //切忌这后面不能加 分号 ”;“  
            {  
                out.write(buf, 0, len);//向客户端输出，实际是把数据存放在response中，然后web服务器再去response中读取  
            } 
        } catch (Exception e) {
        	e.printStackTrace();
            throw LogUtil.handerEx(OssErrorMsg.ERROR_OSS_GET, LogUtil.EMPTY, e);
        } finally {
        	client.shutdown();
            IOUtils.closeQuietly(fileStream);
        }
    }

    public static String  getKey(byte[] in) throws TranFailException {
        String key=null;
        try {
            key = DigestUtils.md5Hex(in);
        }catch (Exception e){}
        if(StringUtil.isBlank(key)){
            throw LogUtil.handerEx(OssErrorMsg.ERROR_OSS_PUT, LogUtil.EMPTY);
        }
        return key;
    }
}
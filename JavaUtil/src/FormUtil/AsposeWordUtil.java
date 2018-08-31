package FormUtil;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.rerloan.basic.exception.TranFailException;
import com.xboot.web.util.constant.Suffix;
import com.xboot.web.util.constant.XBootWebErrorCode;
import com.xboot.web.vo.TemplateUpVo;
import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JavaUtil;
import util.JsonUtil;
import util.LogUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AsposeWordUtil {

    // 默认没有license，会有水印文字
    private static boolean isLicense = false;
    // 初始化日志
    private static final Logger logger = LoggerFactory.getLogger(AsposeWordUtil.class);

    public static boolean getIsLicense() {
        return isLicense;
    }

    /**
     * 初始化License
     */
    static {
        InputStream is = AsposeWordUtil.class.getClassLoader().getResourceAsStream("license.xml");
        License aposeLic = new License();
        try {
            aposeLic.setLicense(is);
            isLicense = true;
            LogUtil.info("破解成功");
        } catch (Exception e) {
            logger.error("AsposeWord破解失败", e);
        }
    }

    /**
     * word转pdf
     * @param wordpath    word路径
     * @param pdffilename 生成pdf文件名
     * @return
     * @throws TranFailException
     */
    public static String word2pdf(String wordpath, String pdffilename) throws TranFailException {
        File outputFile = null;
        File inputFile = null;
        InputStream fileInput = null;
        FileOutputStream fileOS = null;
        // 验证License
        if (!isLicense) {
            return "";
        }
        try {
            inputFile = new File(wordpath);
            fileInput = new FileInputStream(inputFile);

            long old = System.currentTimeMillis();

            Document doc = new Document(fileInput);
            String pdffullpath = wordpath.substring(0, wordpath.lastIndexOf("/") + 1) + pdffilename + Suffix.T_SUFFIX_PDF.getValue();
            outputFile = new File(pdffullpath);
            fileOS = new FileOutputStream(outputFile);

            doc.save(fileOS, SaveFormat.PDF);

            long now = System.currentTimeMillis();

            logger.info("共耗时：" + ((now - old) / 1000.0) + "秒\n\n" + "文件保存在:" + outputFile.getPath());
        } catch (Exception e) {
            throw LogUtil.handerEx(XBootWebErrorCode.ERROR_WORD_TO_PDF, e);
        } finally {
            //关闭流
            JavaUtil.close(fileInput);
            JavaUtil.close(fileOS);

            //如果已经转换成功，则删除word
            if (outputFile.exists() && !outputFile.isDirectory()) {
                boolean delete = inputFile.delete();
                LogUtil.info("删除文件:" + inputFile.getPath() + "。结果是:" + delete);
            }
        }
        return outputFile.getPath();
    }

    /**
     * word转xml
     * @param wordPath    word路径
     * @param xmlPath     word路径，即模版路径
     * @param xmlFileName 生成xml文件名
     * @return
     * @throws TranFailException
     */
    public static String word2xml(String wordPath, String xmlPath, String xmlFileName) throws TranFailException {
        File outputFile = null;
        FileOutputStream fileOS = null;
        // 验证License
        if (!isLicense) {
            return "";
        }
        try {
            long old = System.currentTimeMillis();

            Document doc = new Document(wordPath);
            String xmlFullPath = xmlPath + "/" + xmlFileName + Suffix.T_SUFFIX_XML.getValue();
            outputFile = new File(xmlFullPath);
            fileOS = new FileOutputStream(outputFile);

            doc.save(fileOS, SaveFormat.WORD_ML);
            long now = System.currentTimeMillis();

            logger.info("wordpath共耗时：" + ((now - old) / 1000.0) + "秒\n\n" + "文件保存在:" + outputFile.getPath());
        } catch (Exception e) {
            throw LogUtil.handerEx(XBootWebErrorCode.ERROR_WORD_TO_XML, e);
        } finally {
            //关闭流
            JavaUtil.close(fileOS);
        }
        return outputFile.getPath();
    }


    /**
     * word转xml
     * @param in
     * @param xmlFileName
     * @return
     * @throws TranFailException
     */
    public static TemplateUpVo word2xml(InputStream in, String xmlFileName) throws TranFailException {

        LogUtil.info("word转xml，xmlFileName："+xmlFileName);
        TemplateUpVo templateUpVo = new TemplateUpVo();

        File outputFile = null;
        File xmlFile = null;
        FileOutputStream fileOS = null;
        InputStream xmlIn = null;
        // 验证License
        if (!isLicense) {
            return templateUpVo;
        }
        LogUtil.info("验证License成功");
        try {
            long old = System.currentTimeMillis();
            Document doc = new Document(in);
            String xmlFullPath = xmlFileName + Suffix.T_SUFFIX_XML.getValue();
            outputFile = new File(xmlFullPath);
            fileOS = new FileOutputStream(outputFile);
            doc.save(fileOS, SaveFormat.WORD_ML);
            long now = System.currentTimeMillis();
            logger.info("in共耗时：" + ((now - old) / 1000.0) + "秒，" + "文件保存在:" + outputFile.getPath());

            xmlFile = new File(xmlFullPath);
            xmlIn = new FileInputStream(xmlFile);
            templateUpVo.setInputStream(xmlIn);

            String fileStr = getXmlContent(xmlFile);

            List<String> list = WordUtil.getXml$(fileStr);
            LogUtil.info("获取$,list:"+ JsonUtil.toJson(list));
            HashSet h = new HashSet(list);
            list.clear();
            list.addAll(h);

            templateUpVo.setList(list);
            LogUtil.info("xbootweb:"+JsonUtil.toJson(templateUpVo));
        } catch (Exception e) {
            throw LogUtil.handerEx(XBootWebErrorCode.ERROR_WORD_TO_XML, e);
        } finally {
            //关闭流
            JavaUtil.close(fileOS);
            try {
                FileUtils.forceDelete(outputFile);
                FileUtils.forceDelete(xmlFile);
            } catch (IOException e) {

            }
        }
        return templateUpVo;
    }

    /**
     * word转xml
     * @param in
     * @param docFileName
     * @throws TranFailException
     */
    public static TemplateUpVo docx2doc(InputStream in, String docFileName) throws TranFailException {

        TemplateUpVo templateUpVo = new TemplateUpVo();

        File outputFile = null;
        File xmlFile = null;
        FileOutputStream fileOS = null;
        InputStream xmlIn = null;
        // 验证License
        if (!isLicense) {
            return templateUpVo;
        }
        try {
            long old = System.currentTimeMillis();
            Document doc = new Document(in);
            String docFullPath = docFileName + Suffix.T_SUFFIX_DOCX.getValue();
            outputFile = new File(docFullPath);
            fileOS = new FileOutputStream(outputFile);
            doc.save(fileOS, SaveFormat.WORD_ML);
            long now = System.currentTimeMillis();
            logger.info("in共耗时：" + ((now - old) / 1000.0) + "秒，" + "文件保存在:" + outputFile.getPath());

            xmlFile = new File(docFullPath);
            xmlIn = new FileInputStream(xmlFile);
            templateUpVo.setInputStream(xmlIn);
            templateUpVo.setList(new ArrayList<>());
            LogUtil.info("docx2doc:"+JsonUtil.toJson(templateUpVo));
        } catch (Exception e) {
            throw LogUtil.handerEx(XBootWebErrorCode.ERROR_WORD_TO_XML, e);
        } finally {
            //关闭流
            JavaUtil.close(fileOS);
            try {
                FileUtils.forceDelete(outputFile);
                FileUtils.forceDelete(xmlFile);
            } catch (IOException e) {

            }
        }
        return templateUpVo;
    }


    private static String getXmlContent(File xmlFile) {

        String fileStr = "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(xmlFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            //将file文件内容转成字符串
            BufferedReader bf = new BufferedReader(isr);

            String content = "";
            StringBuilder sb = new StringBuilder();
            while (content != null) {
                content = bf.readLine();
                if (content == null) {
                    break;
                }
                sb.append(content.trim());
            }
            bf.close();
            fileStr = sb.toString();
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } catch (IOException e) {

        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {

            }
        }
        return fileStr;
    }

    public static void main(String[] args) {

        File file = new File("C:\\yonghui.wu\\data.doc");
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {

        }
        System.out.println("==============dataxmlin开始===================");
        TemplateUpVo templateUpVo = word2xml(in,"dataxmlin");
        System.out.println(JsonUtil.toJson(templateUpVo));
        System.out.println("==============dataxmlin结束===================");

        System.out.println("==============dataxmlpath开始===================");
        word2xml("C:\\yonghui.wu\\data.doc","C:\\yonghui.wu","dataxmlpath");
        System.out.println("==============dataxmlpath结束===================");

        try {
            File fileNew = new File("C:\\yonghui.wu\\dataxmlpathNew.xml");
            OutputStream ooo=new FileOutputStream(fileNew);
            IOUtil.copyCompletely(in,ooo);
        } catch (IOException e) {

        } finally {

        }

    }
}

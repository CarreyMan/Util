package FormUtil;

import com.rerloan.basic.exception.TranFailException;
import com.xboot.web.util.constant.Suffix;
import org.apache.commons.io.FileUtils;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.JavaUtil;
import util.StringUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WordUtil {
	
	public static final String ERROR_FILE_NAME = "printError.txt";


	/**
	 *
	 * @param inputStream word内容
	 * @param map 填充用变量
	 * @param fileName 文件名包括后缀名
	 * @param print 是套打 还是普通预览
	 * @return
	 */
	public static InputStream createWord(InputStream inputStream, Map<String,Object> map, String fileName, boolean print) throws TranFailException,IOException{

		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setNamespaceAware(true);
		OutputStreamWriter writer = null;
        InputStream in = null;
		File file = null;
		try{


			DocumentBuilder db=dbf.newDocumentBuilder();

			byte[] bytes = JavaUtil.getBytes(inputStream);

			Document xmlDoc=db.parse(new ByteArrayInputStream(bytes));
//            Document xmldoc=db.parse(xmlPath);

			Element root = xmlDoc.getDocumentElement();


			Map<Node,Node> nodeColors = null;
			if(print){
				nodeColors = whitenWords(root, xmlDoc);
			}
			NodeList nodes = selectNodeList("//w:r//w:t", root);
			for(int i=0; i<nodes.getLength(); i++){
				handleNode(nodes.item(i), map, nodeColors, print, xmlDoc);
			}

			//寻找前后对应的关系

			//保存
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer former = factory.newTransformer();
			writer = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
			former.transform(new DOMSource(xmlDoc), new StreamResult(writer));

			file= new File(fileName);
            in = new FileInputStream(file);
			return in;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally {
			if(writer != null){
				writer.close();
			}
//			FileUtils.forceDelete(file);
		}

	}

	/**
	 * 
	 * @param xmlContent word内容
	 * @param map 填充用变量
	 * @param fileName 文件名包括后缀名
	 * @param print 是套打 还是普通预览
	 * @param filePath 生成文件路径
	 * @return
	 */
    public static String createWord(String xmlContent, Map<String,Object> map, String fileName, boolean print, String filePath) throws TranFailException,IOException{
    	DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setNamespaceAware(true);
		OutputStreamWriter writer = null;
       	
        try{


            DocumentBuilder db=dbf.newDocumentBuilder();
            Document xmlDoc=db.parse(new ByteArrayInputStream(xmlContent.getBytes()));
//            Document xmldoc=db.parse(xmlPath);
            
            Element root = xmlDoc.getDocumentElement();
            
            
            Map<Node,Node> nodeColors = null;
            if(print){
            	nodeColors = whitenWords(root, xmlDoc);
            }
            
            
            NodeList nodes = selectNodeList("//w:r//w:t", root);

            for(int i=0; i<nodes.getLength(); i++){
            	handleNode(nodes.item(i), map, nodeColors, print, xmlDoc);
            }
            
            //寻找前后对应的关系
            
            //保存
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer former = factory.newTransformer();
            fileName = formatPath(fileName, print, filePath);
			writer = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
            former.transform(new DOMSource(xmlDoc), new StreamResult(writer));
            return fileName;
        }catch(TranFailException e){
        	if(!createErrorFile(e.errorMsg, formatPath(ERROR_FILE_NAME, print, filePath))){
        		throw e;
        	}
        	
            return formatPath(ERROR_FILE_NAME, print, filePath);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
			if(writer != null){
				writer.close();
			}
		}
	}



	public static List<String> getXml$(String xmlContent) throws TranFailException,IOException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setNamespaceAware(true);
		OutputStreamWriter writer = null;

		List<String> templateList = new ArrayList<>();

		try{
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document xmlDoc=db.parse(new ByteArrayInputStream(xmlContent.getBytes()));
			Element root = xmlDoc.getDocumentElement();
			NodeList nodes = selectNodeList("//w:r//w:t", root);
			for(int i=0; i<nodes.getLength(); i++){
				Node item = nodes.item(i);
				NodeList followNodes = selectNodeList("following-sibling::w:r/w:t", item.getParentNode());
				List<String> fol = new ArrayList<String>();
				fol.add("");
				for(int j=0; j<followNodes.getLength(); j++){
					fol.add(followNodes.item(j).getTextContent());
				}
				List<String> pre = new ArrayList<String>();
				String name = checkNode(item, pre, fol);

				if(StringUtil.isNotBlank(name)){
					name = "${"+name+"}";
					templateList.add(name);
				}
			}
		}catch(TranFailException e){
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(writer != null){
				writer.close();
			}
		}
		return templateList;
	}
    
    public static boolean validate2003WordXml(String xmlContent){
		return xmlContent.indexOf("http://schemas.microsoft.com/office/word/2003/wordml")!=-1;
	}
    
    public static Map<Node,Node> whitenWords(Element root, Document xmlDoc){
    	Map<Node,Node> nodeColors = new HashMap<Node,Node>(1000);
    	NodeList sonOfTcBorders = selectNodeList("//w:tcPr/w:tcBorders/* | //w:tblBorders/*", root);
        for(int i = 0; i < sonOfTcBorders.getLength();i++){
        	Node son = sonOfTcBorders.item(i);
        	((Element)son).setAttribute("w:color", "FFFFFF");
        	
        }
        
        NodeList sonOfpBorders = selectNodeListWX("//wx:pBdrGroup/wx:borders/*", root);
        for(int i = 0; i < sonOfpBorders.getLength();i++){
        	Node son = sonOfpBorders.item(i);
        	((Element)son).setAttribute("wx:color", "FFFFFF");
        	
        }
        
//        NodeList wts = selectNodeListWX("//wx:t", root);
        for(int i = 0; i < sonOfpBorders.getLength();i++){
        	Node son = sonOfpBorders.item(i);
        	((Element)son).setAttribute("wx:color", "FFFFFF");
        	
        }
        
        NodeList wus = selectNodeList("//w:rPr/w:u", root);{
        	for (int i = 0 ; i<wus.getLength();i++){
            	Node wu = wus.item(i);
            	((Element)wu).setAttribute("w:val", "");
            }
        }
        
        NodeList rprs = selectNodeList("//w:rPr", root);
        for (int i = 0 ; i<rprs.getLength();i++){
        	Node rpr = rprs.item(i);
        	Element color = xmlDoc.createElement("w:color");
            color.setAttribute("w:val", "FFFFFF");
        	rpr.appendChild(color);
        	nodeColors.put(rpr.getParentNode(), color);
        }
        return nodeColors;
    }

    public static String whitenWords(String path,String filepath,String filename) throws TranFailException,IOException{

        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setNamespaceAware(true);
        OutputStreamWriter writer = null;
        try{
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document xmlDoc=db.parse(new ByteArrayInputStream(JavaUtil.getBytes(path)));

            Map<Node,Node> nodecolors = new HashMap<Node,Node>(1000);
            Element root = xmlDoc.getDocumentElement();

            NodeList sonOftcBorders = selectNodeList("//w:tcPr/w:tcBorders/* | //w:tblBorders/*", root);
            for(int i = 0; i < sonOftcBorders.getLength();i++){
                Node son = sonOftcBorders.item(i);
                ((Element)son).setAttribute("w:color", "FFFFFF");

            }

            NodeList sonOfpBorders = selectNodeListWX("//wx:pBdrGroup/wx:borders/*", root);
            for(int i = 0; i < sonOfpBorders.getLength();i++){
                Node son = sonOfpBorders.item(i);
                ((Element)son).setAttribute("wx:color", "FFFFFF");

            }

            NodeList wts = selectNodeListWX("//wx:t", root);
            for(int i = 0; i < sonOfpBorders.getLength();i++){
                Node son = sonOfpBorders.item(i);
                ((Element)son).setAttribute("wx:color", "FFFFFF");

            }

            NodeList wus = selectNodeList("//w:rPr/w:u", root);{
                for (int i = 0 ; i<wus.getLength();i++){
                    Node wu = wus.item(i);
                    ((Element)wu).setAttribute("w:val", "");
                }
            }

            NodeList rprs = selectNodeList("//w:rPr", root);
            for (int i = 0 ; i<rprs.getLength();i++){
                Node rpr = rprs.item(i);
                Element color = xmlDoc.createElement("w:color");
                color.setAttribute("w:val", "FFFFFF");
                rpr.appendChild(color);
                nodecolors.put(rpr.getParentNode(), color);
            }

            //保存
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer former = factory.newTransformer();
            String returnFilePath = formatPath(filename+ Suffix.T_SUFFIX_DOC.getValue(), filepath);
            writer = new OutputStreamWriter(new FileOutputStream(returnFilePath),"UTF-8");
            former.transform(new DOMSource(xmlDoc), new StreamResult(writer));
            return returnFilePath;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }
    
    public static void handleNode(Node node, Map<String,Object> map, Map<Node,Node> nodecolors, boolean print,Document xmldoc) throws TranFailException{

		if(!preCheckNode(node)){
			return ;
		}
		NodeList preNodes = null;//selectNodeList("preceding-sibling::w:r/w:t", node.getParentNode());
		List<String> pre = new ArrayList<String>();

		NodeList followNodes = selectNodeList("following-sibling::w:r/w:t", node.getParentNode());
		List<String> fol = new ArrayList<String>();
		fol.add("");
		for(int i=0; i<followNodes.getLength(); i++){
			fol.add(followNodes.item(i).getTextContent());
		}

		//是否存在变量 存在获得全量名称
		String name = checkNode(node, pre, fol);
		if(name == null){
			return;
		}
		if(preNodes!=null){
			for(int i=preNodes.getLength()-1; i>-1; i--){
				pre.add(preNodes.item(i).getTextContent());
			}
		}
		System.out.println("name:"+name+"---value:"+ObjectUtil.getValueByName(map, name));

		backfill(preNodes, followNodes, node,  pre,  fol, ObjectUtil.getValueByName(map, name), nodecolors, print, xmldoc);
	}

    
    private static boolean preCheckNode(Node node){
    	String content = node.getTextContent();
    	Pattern pattern = Pattern.compile("\\$\\{(\\S+)\\}");
    	Matcher matcher = pattern.matcher(content);
    	if (matcher.find())
    	{
    		return true;
    	}else{
    		pattern = Pattern.compile("(\\$)");
        	matcher = pattern.matcher(content);
        	if (matcher.find())
        	{
        		return true;
        	}
    	}
    	return false;
    }
    
    private static String checkNode(Node node, List<String> pre, List<String> fol){
    	String content = node.getTextContent();
    	String name = null;
    	Pattern pattern = Pattern.compile("\\$\\{(\\S+)\\}");
    	Matcher matcher = pattern.matcher(content);
    	int type = 0; //1: $ 2:{ 3:字符 4:} 5:pass 6:miss
    	if (matcher.find())
    	{
    		int index = content.indexOf(matcher.group(1));
    		content = content.replace(matcher.group(0), matcher.group(0).replaceAll("\\S{1}", " "));
    		pre.add(0,content.substring(0,index));
    		fol.add(0,content.substring(index));
    		fol.remove(1);
    		type = 5;
    		name = matcher.group(1);
    	}else{
    		pattern = Pattern.compile("(\\$)");
        	matcher = pattern.matcher(content);
        	if (matcher.find())
        	{
        		int index = content.indexOf(matcher.group(1));
        		content = content.replace(matcher.group(0), matcher.group(0).replaceAll("\\S{1}", " "));
        		pre.add(content.substring(0,index+1));
        		if(index==content.length()-1){
        			fol.add(0,"");
        		}else{
        			fol.add(0,content.substring(index+1));
        		}
        		fol.remove(1);
        		type = 2;
        	}else{
        		type = 6;
        	}
    	}
    	
    	if(type == 2){
        	name = findBlankFollowingMatch(fol,type,"");
        }
    	return name;
    }
    private static void backfill(NodeList preNodes,NodeList followNodes,Node node, List<String> pre, List<String> fol, String value, Map<Node,Node> nodecolors,  boolean print, Document xmldoc){
    	DataNode followHead = findBlankFollowing(fol, print);
        DataNode head = findBlankPreceding(pre,followHead, print);
        value = value==null ? "":value;
        //调整长度
    	int total = followHead.total+head.total;
        int count = (value.length()+value.getBytes().length)/2;
        int prex = (total - count)/2;
        int sufx = total - count-prex;
        String preStr = "";
        String sufxStr = "";
        if(prex > 0){
        	preStr = String.format("%"+prex+"s", "");
        }
        if(prex > 0){
        	sufxStr = String.format("%"+sufx+"s", "");
        }
        
        String target =   value + preStr + sufxStr;
    	
    	
    	DataNode next = head;
    	//往data中填充value;
        while(next!=null){
        	int num = getNums(target, next.count);
        	
        	if(num!=0 && next.next!=null){
        		
        		if(next.position<0){
        			next.reststr = next.reststr+target.substring(0,num);
        		}else{
        			next.reststr = target.substring(0,num) + next.reststr;
        		}
        		target = target.substring(num);
        	}
        	if(next.next==null){
        		next.reststr=target+next.reststr;
        	}
        	
        	next = next.next;
        }
        //往node中填充value
        next = head;
        while(next!=null){
        	Node temp = null;
        	if(next.position<-1){
        		temp = preNodes.item(preNodes.getLength()+1+next.position);
        		temp.setTextContent(next.reststr);
        	}
        	if(next.position>1){
        		temp = followNodes.item(next.position-2);
        		temp.setTextContent(next.reststr);
        	}
        	if(next.position == -1){
        		temp = node;
        		node.setTextContent(next.reststr+next.next.reststr);
        		next = next.next;
        	}
        	if(print){
        		whitenNodeText(temp, nodecolors, xmldoc);
        	}
        	next = next.next;
        }
        
    }
    
    
    private static int getNums(String value, int count){
    	int nums = 0;
    	for(int i = 0; i<value.length();i++){
    		
    		if(value.charAt(i)-0 > 128){
    			nums +=2;
    		}else{
    			nums +=1;
    		}
    		if(nums > count){
    			return i;
    		}
    		
    	}
    	if(count==1&&value.length()!=0){
    		return 1;
    	}
    	return 0;
    }
    
    
    public static NodeList selectNodeList(String express, Node source) {
    	SimpleNamespaceContext snc = new SimpleNamespaceContext();
    	snc.bindNamespaceUri("w", "http://schemas.microsoft.com/office/word/2003/wordml");
    	NodeList result=null;
        XPathFactory xpathFactory=XPathFactory.newInstance();
        XPath xpath=xpathFactory.newXPath();
        xpath.setNamespaceContext(snc);
        try {
            result=(NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static NodeList selectNodeListWX(String express, Node source) {
    	SimpleNamespaceContext snc = new SimpleNamespaceContext();
    	snc.bindNamespaceUri("wx", "http://schemas.microsoft.com/office/word/2003/auxHint");
    	NodeList result=null;
        XPathFactory xpathFactory=XPathFactory.newInstance();
        XPath xpath=xpathFactory.newXPath();
        xpath.setNamespaceContext(snc);
        try {
            result=(NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static void whitenNodeText(Node node, Map<Node,Node> nodecolors, Document xmldoc){
//    	if(nodecolors==null || true) return;
//    	Node rpr = selectNode("w:rPr",node.getParentNode());
//    	Node u = selectNode("w:rPr/w:u",node.getParentNode());
//    	if(u!=null){
//    		u.getParentNode().removeChild(u);
//    	}
    	Node color = nodecolors.get(node);
    	
    	if(color!=null){
    		((Element)color).setAttribute("w:val", "000000");
    		return;
    	}
    	//此处处理 不存在 rprrpr的问题
    	Node rpr = xmldoc.createElement("w:rPr");
    	color = xmldoc.createElement("w:color");
    	((Element)color).setAttribute("w:val", "000000");
    	rpr.appendChild(color);
    	node.getParentNode().appendChild(rpr);
    	node.getParentNode().removeChild(node);
    	rpr.getParentNode().appendChild(node);
    }
    
    public static Node selectNode(String express, Node source) {
    	SimpleNamespaceContext snc = new SimpleNamespaceContext();
    	snc.bindNamespaceUri("w", "http://schemas.microsoft.com/office/word/2003/wordml");
    	Node result=null;
        XPathFactory xpathFactory=XPathFactory.newInstance();
        XPath xpath=xpathFactory.newXPath();
        xpath.setNamespaceContext(snc);
        try {
            result=(Node) xpath.evaluate(express, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    
    
    
    
    
    public static DataNode findBlankPreceding(List<String> list, DataNode followHead, boolean print){
    	Pattern pattern = Pattern.compile("(\\s{1,})$");
    	DataNode head = null;
    	DataNode current = null;
    	DataNode temp = null;
    	int total = 0;
    	for(String str:list){
    		Matcher matcher = pattern.matcher(str);
        	String matcherStr = null;
        	if(!matcher.find() && !str.equals("")){
        		break;
        	}
        		matcherStr = str.equals("") ? "": matcher.group(0);
        		temp = current;
        		current = new DataNode();
        		if(temp!=null){
        			current.position = temp.position-1;
        			current.next = head;
        		}else{
        			current.position = -1;
        			current.next=followHead;
        		}
        		current.count = matcherStr.length();
        		total += current.count;
        		current.all = matcherStr.length()==str.length();
        		current.reststr = str.replace(matcherStr, "");
        		if(print && !current.reststr.equals("")){
        			current.reststr = current.reststr.replaceAll("\\S{1}", " ");
        		}
        		head = current;
        		if(!current.all){
        			break;
        		}
        	
    	}
    	head.total = total;
    	return head;
    }
    
    public static String findBlankFollowingMatch(List<String> list, int type, String name){
    	StringBuilder sb = new StringBuilder(name);
    	for(int i = 0; i<list.size(); i++){
    		String str = list.get(i);
    		int j = 0;
    		for(j=0 ; j<str.length();j++){
    			if(type == 2){
    				if(str.charAt(j)=='{'){
    					type = 3;
    				}else {
    					return null;
    				}
    			}else if(type == 3){
					if(str.charAt(j)!='}' && str.charAt(j)!='$'){
						sb.append(str.charAt(j));
						type = 4;
					}else{
						return null;
					}
    			}else if(type == 4){
    				if(str.charAt(j)!='}'){
						sb.append(str.charAt(j));
					}else if(str.charAt(j)=='}'){
						type = 5;
						j++;
						break;
					}
    			}
    		}
    		if(j == str.length()){
    			str = str.substring(0,j).replaceAll("\\S{1}", " ");
    		}else{
    			str = str.substring(0,j).replaceAll("\\S{1}", " ")+str.substring(j);
    		}
    		list.add(i, str);
    		list.remove(i+1);
    		if(type == 5){
    			return sb.toString().trim();
    		}
    		
		}
    	return null;
    }
    
    public static DataNode findBlankFollowing(List<String> list , boolean print){
    	
    	DataNode head = null;
    	DataNode current = null;
    	DataNode temp = null;
    	int total = 0;
    	for(String str:list){
    		Pattern pattern = Pattern.compile("^(\\s{1,})");
    		Matcher matcher = pattern.matcher(str);
        	String matcherStr = null;
        	if(!matcher.find() && !str.equals("")){
        		break;
        	}
        		matcherStr = str.equals("") ? "": matcher.group(0);
        		if(head==null){
        			head = current = new DataNode();
        			current.position = 1;
        		}else{
        			temp = current;
        			current = current.next = new DataNode();
        			current.position = temp.position+1; 
        		}
        		current.count = matcherStr.length();
        		total += current.count;
        		current.all = matcherStr.length()==str.length();
        		current.reststr = str.replaceFirst(matcherStr, "");
        		if(print && !current.reststr.equals("")){
        			current.reststr = current.reststr.replaceAll("\\S{1}", " ");
        		}
        		if(!current.all){
        			break;
        		}
    	}
    	head.total = total;
    	return head;
    }
    public static String formatPath(String fileName, boolean print, String filePath){
    	File f=null;
    	try {
			f = new File(filePath);
			f.mkdirs();
			filePath = filePath.endsWith("/") ? filePath : filePath+"/";
		}catch (Exception e){

		}finally {
			try {
				FileUtils.forceDelete(f);
			} catch (IOException e) {

			}
		}
    	return filePath+fileName;
    }

    public static String formatPath(String fileName, String filePath){
        File f = new File(filePath);
        try {
			f.mkdirs();
			filePath = filePath.endsWith("/") ? filePath : filePath+"/";

		}catch (Exception e){

		}finally {
			try {
				FileUtils.forceDelete(f);
			} catch (IOException e) {

			}
		}
		return filePath+fileName;
    }
    
    public static class DataNode{
    	private boolean all = false;//是否全部匹配空格
    	private String reststr = "";//记忆匹配剩余的字符串，后期用于放置填充的value值
    	private DataNode next;
    	private int total = 0;		//总共的字符数 head节点有效
    	private int count = 0;  	// 本节点拥有的字符数
    	private int position = 0;	// ++1往前  --1 往后  1和-1 属于发现$位置node的前后半段
    	
    }
    // 压缩
    public static byte[] compress(String str) throws IOException {
      if (str == null || str.length() == 0) {
        return null;
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(out);
      gzip.write(str.getBytes("UTF-8"));
      gzip.close();
      return out.toByteArray();
    }
   
    //解压缩
    public static String uncompress(byte[] data) throws IOException {
      if (data == null || data.length == 0) {
        return "";
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      GZIPInputStream gunzip = new GZIPInputStream(in);
      byte[] buffer = new byte[256];
      int n;
      while ((n = gunzip.read(buffer)) >= 0) {
        out.write(buffer, 0, n);
      }
      // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
      return out.toString("UTF-8");
    }
//
//    public static void main(String args[]){
//    	Map<String,Object> map = new HashMap<>();
//    	map.put("bussNo","FY20161211的事实上事实上事实上是否908sdfds");
//    	try {
//
//    	    String fileName = "datadocin.doc";
//    	    String filePath = "C:\\yonghui.wu";
//            String xmlFullPath = filePath +"/"+ "dataxmlin" + Suffix.T_SUFFIX_XML.getValue();
//            String docFullPath = filePath +"/"+ fileName + Suffix.T_SUFFIX_DOC.getValue();
//            File file = new File(xmlFullPath);
//
//            InputStream inputStream = new FileInputStream(file);
//            InputStream word = WordUtil.createWord(inputStream, map, fileName, false, filePath);
//
//            File file1 = new File("C:\\yonghui.wu\\wwww.doc");
//            OutputStream outputStream = new FileOutputStream(file1);
//
//            outputStream.write(JavaUtil.getBytes(word));
//
//
//        } catch (TranFailException | IOException e) {
//			e.printStackTrace();
//		}
//
//    }
    
   /**
    * 
    * @param content
    * @param print
    * @param filePath
    * @return
    * @throws TranFailException
    * @throws IOException
    */
    public static String createWord(String content, boolean print, String filePath) throws TranFailException,IOException{
    	if(createErrorFile(content, formatPath(ERROR_FILE_NAME, print, filePath))){
    		return formatPath(ERROR_FILE_NAME, print, filePath);
    	}
        return null;
	}
    
    
    @SuppressWarnings("finally")
	private static boolean createErrorFile(String error, String path){
    	FileWriter fileWritter = null;
    	String info = "\n下载失败，因为模版参数配置不正确，请根据wiki地址进行参数比对修改,并重新上传至模版管理！！！\n参考wiki \nhttp://wiki.i.beebank.com/pages/viewpage.action?pageId=16818283   \nhttp://wiki.i.beebank.com/pages/viewpage.action?pageId=16817964 ";

		File file =new File(path);
		try{
			if(!file.exists()){
			 file.createNewFile();
			}
			
			fileWritter = new FileWriter(path,true);
			fileWritter.write(error);
			fileWritter.write(info);
			fileWritter.flush();
			fileWritter.close();
    	}catch(IOException e){
    	}finally{
    		if(fileWritter!=null){
    			try {
    				fileWritter.close();
					FileUtils.forceDelete(file);
				} catch (IOException e) {
				}
			}
			return true;
    	}
    }
    
}
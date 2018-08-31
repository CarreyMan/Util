package FormUtil;

import com.rerloan.basic.dic.UtilErrorMsg;
import com.rerloan.basic.exception.TranFailException;
import jxl.Workbook;
import jxl.format.*;
import jxl.write.*;
import util.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/*
 * 下载EXCEL
 */
public class DownObjectExcelUtil {
//	public static void main(String args[]) throws WriteException, IOException {
//		CreateExcelParam createExcelParam=new CreateExcelParam();
//		SheetParam sheetParam1=new SheetParam();
//		sheetParam1.setSheetName("测试1");
//
//		List<Map<String,Object>> sheetList1=new ArrayList<Map<String,Object>>();
//		for(int i=0;i<10;i++){
//			Map<String,Object> map=new HashMap<>();
//			map.put("a","我"+i);
//			map.put("b","额"+i);
//			map.put("c","人"+i);
//			sheetList1.add(map);
//		}
//		sheetParam1.setSheetList(sheetList1);
//		sheetParam1.setSheetTitles(new String[]{"我","额","人"});
//		sheetParam1.setListField(new String[]{"a","b","c"});
//        sheetParam1.setWidths(new int[]{10,20,30});
//        sheetParam1.setOtherParamTop(new ArrayList<String[]>() {{
//            add(new String[]{"A","B","C","c","a"});
//            add(new String[]{"D","E","F"});
//            add(new String[]{"R"});
//        }});
//        List<SheetParam> list=new ArrayList<>();
//        list.add(sheetParam1);
//        createExcelParam.setSheetParamList(list);
//        try {
//            downMultiTitleExcel(createExcelParam,"测试");
//        } catch (WriteException e) {
//            throw e;
//        } catch (IOException e) {
//            throw e;
//        }
//    }

	public  void downMultiTitleExcel(HttpServletResponse response,
									CreateExcelParam createExcelParam,
									String filename) throws WriteException, IOException, TranFailException {
		OutputStream os =null;
		WritableWorkbook wbook = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.reset();// 清空输出流 //给下载的文件命名

			response.setContentType("application/octet-stream;charset=gbk");
			response.addHeader("Content-Disposition", "attachment;filename="+new String(filename.getBytes("utf-8"),"ISO8859-1"));

            os = response.getOutputStream(); // 取得输出流;

			wbook = Workbook.createWorkbook(os); // 建立excel文件
			int charNormal = 10;
			WritableFont oneFont = new WritableFont(WritableFont.createFont("宋体"), charNormal); // 字体
			WritableCellFormat normalFormat = new WritableCellFormat(oneFont);
			normalFormat.setAlignment(Alignment.CENTRE);
			normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			normalFormat.setWrap(true);// 是否换行
			WritableFont wfont = new WritableFont(WritableFont.ARIAL, 16,WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat wcfFC = new WritableCellFormat(wfont);
			wcfFC.setBackground(jxl.format.Colour.LIGHT_BLUE);
			wcfFC.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcfFC.setAlignment(Alignment.CENTRE);
            wcfFC.setBorder(Border.ALL, BorderLineStyle.THIN);
            //表头字体
			WritableFont titleFont = new WritableFont(WritableFont.ARIAL, charNormal, WritableFont.BOLD); // 字体
			//表头标题
			WritableCellFormat titleWcf = new WritableCellFormat(titleFont);
			//把水平对齐方式指定为居中
			titleWcf.setAlignment(Alignment.CENTRE);
			//把垂直对齐方式指定为居中
			titleWcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			//添加边框：
			titleWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			//添加背景色：
			titleWcf.setBackground(jxl.format.Colour.YELLOW);




			WritableCellFormat contentWcf = new WritableCellFormat(oneFont);
			//把垂直对齐方式指定为居中
			contentWcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			contentWcf.setWrap(true);// 是否换行
            contentWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			//人名币格式
			NumberFormat nf = new NumberFormat("¥#,##0.00####");//设置数字格式
			WritableCellFormat wcfMoney = new WritableCellFormat(nf); //设置表单格式
			//把垂直对齐方式指定为居中
			wcfMoney.setVerticalAlignment(VerticalAlignment.CENTRE);
			//把水平对齐方式指定为左对齐
			wcfMoney.setAlignment(Alignment.LEFT);

			//日期格式
			DateFormat df = new DateFormat("yyyy/MM/dd"); //添加时间
			WritableCellFormat wcfDF = new WritableCellFormat(df);
			//把垂直对齐方式指定为居中
			wcfDF.setVerticalAlignment(VerticalAlignment.CENTRE);
			//把水平对齐方式指定为左对齐
			wcfDF.setAlignment(Alignment.LEFT);

			List<SheetParam> sheetParamList=createExcelParam.getSheetParamList();

			for(int i=0;i<sheetParamList.size();i++){
				SheetParam sheetParam=sheetParamList.get(i);

				String sheetName = sheetParam.getSheetName();
				List<String[]> otherParamTop =sheetParam.getOtherParamTop();
				int[] widths = sheetParam.getWidths();
				/**
				 * 1.sheet页名称
				 */
				WritableSheet wsheet = wbook.createSheet(sheetName, i); // sheet名称
				/**
			 	* 2.设置excel每列的宽度
			 	*/
				if(widths!=null&&widths.length>0){
					for (int w = 0; w < widths.length; w++) {
						wsheet.setColumnView(w, widths[w]);
					}
				}
				int row=0;//当前行数
				/**
				 * 3.表头上方内容，以传进来的为准，不拼接参数，可以为空
				 */
				if(otherParamTop!=null&&otherParamTop.size()>0){
					for(int oIndex=0;oIndex<otherParamTop.size();oIndex++){
						String[] topRowParam=otherParamTop.get(oIndex);
						for(int topRowParamIndex=0;topRowParamIndex<topRowParam.length;topRowParamIndex++){
							wsheet.addCell(new Label(topRowParamIndex,row,topRowParam[topRowParamIndex],contentWcf));
						}
						row++;
					}
				}
				/**
				 * 4.遍历设置table
				 */
				List<SheetTable> tableList=sheetParam.getTableList();
				if(tableList!=null&&tableList.size()>0){
					for(int tab=0;tab<tableList.size();tab++){
						SheetTable sheetTable=tableList.get(tab);
						String tableName=sheetTable.getTableName();
						List<?> list =sheetTable.getTableList();
						String[] tableTitle = sheetTable.getTableTitles();
						String[] tableField=sheetTable.getTableField();
						//4.1表名称
						if(StringUtil.isNotBlank(tableName)){
							int colNum=tableTitle.length;
							wsheet.addCell(new Label(0, row, tableName,wcfFC));
							wsheet.mergeCells(0,row,colNum-1,row);
							row++;
						}
						//4.2生成表头
						for (int t = 0; t < tableTitle.length; t++) {
							wsheet.addCell(new Label(t, row, tableTitle[t],titleWcf));
						}
						row++;

						//4.3拼接表中数据
						for(int d=0;d<list.size();d++){
							//每行内容
							Map<String,Object> rowData= JsonUtil.string2Object(JsonUtil.toJson(list.get(d)),Map.class);
							//根据传的每行字段获取数据
							for(int f=0;f<tableField.length;f++){
								Object o=rowData.get(tableField[f]);
								//添加到cell
								wsheet.addCell(new Label(f,row,o==null?"":o.toString(),contentWcf));
							}
							row++;
						}
					}

				}
			}
			wbook.write(); // 写入文件
			os.flush();
		} catch (Exception e) {
			throw LogUtil.handerEx(UtilErrorMsg.ERROR_EXCEL_OUT, LogUtil.ERROR, e);
		} finally {
			if (wbook != null) {
				wbook.close();
			}
			if (os != null) {
				os.close(); // 关闭流
			}
		}
	}

}

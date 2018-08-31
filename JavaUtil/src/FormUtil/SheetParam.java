package FormUtil;

import java.util.List;

public class SheetParam {
    private String sheetName;
    //表格列宽度
    private int[] widths;
    private List<SheetTable> tableList;
    private List<String[]> otherParamTop;
    private List<String[]> otherParamBottom;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<String[]> getOtherParamTop() {
        return otherParamTop;
    }

    public void setOtherParamTop(List<String[]> otherParamTop) {
        this.otherParamTop = otherParamTop;
    }

    public List<SheetTable> getTableList() {
        return tableList;
    }

    public void setTableList(List<SheetTable> tableList) {
        this.tableList = tableList;
    }

    public List<String[]> getOtherParamBottom() {
        return otherParamBottom;
    }

    public void setOtherParamBottom(List<String[]> otherParamBottom) {
        this.otherParamBottom = otherParamBottom;
    }

    public int[] getWidths() {
        return widths;
    }

    public void setWidths(int[] widths) {
        this.widths = widths;
    }
}
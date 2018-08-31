package FormUtil;

import java.util.List;

/**
 * Description:
 * Author: guoliang
 * Create Date Time: 2018/2/2 11:17.
 */
public class SheetTable {
    //表名称
    private String tableName;
    //表头
    private String[] tableTitles;
    //表数据
    private List<?> tableList;
    //数据与表头对应关系
    private String[] tableField;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getTableTitles() {
        return tableTitles;
    }

    public void setTableTitles(String[] tableTitles) {
        this.tableTitles = tableTitles;
    }

    public List<?> getTableList() {
        return tableList;
    }

    public void setTableList(List<?> tableList) {
        this.tableList = tableList;
    }

    public String[] getTableField() {
        return tableField;
    }

    public void setTableField(String[] tableField) {
        this.tableField = tableField;
    }

    public boolean isAccess(){
        return this.tableTitles!=null&&this.tableTitles!=null&&this.tableField.length==this.tableTitles.length;
    }
}
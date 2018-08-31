package util;

import java.util.Map;

public class PageUtil {
    /**
     * 默认为第一页.
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页记录数(15).
     */
    public static final int DEFAULT_NUM_PER_PAGE = 10;

    /**
     * 最大每页记录数(100).
     */
    public static final int MAX_PAGE_SIZE = 100;
    /**
     * 处理分页数据
     * @param totalNum
     * @param paramMap
     */
    public static void dealPageParam(Long totalNum, Map<String,Object> paramMap){
        Object numPerPageParam0=paramMap.get("numPerPage");
        if(numPerPageParam0==null){
            numPerPageParam0=DEFAULT_NUM_PER_PAGE;
        }
        Object currentPageParam0=paramMap.get("currentPage");
        if(currentPageParam0==null){
            currentPageParam0=DEFAULT_PAGE_NUM;
        }
        int numPerPageParam=Integer.valueOf(numPerPageParam0.toString());//页面传入的每页记录数
        int currentPageParam=Integer.valueOf(currentPageParam0.toString());//页面传入的当前页数
        // 校验当前页数
        int currentPage = checkCurrentPage(totalNum.intValue(), numPerPageParam, currentPageParam);
        // 校验页面输入的每页记录数numPerPage是否合法
        int numPerPage = checkNumPerPage(numPerPageParam); // 校验每页记录数

        // 根据页面传来的分页参数构造SQL分页参数
        paramMap.put("offset", (currentPage - 1) * numPerPage);//从第几条记录开始
        paramMap.put("pageSize", numPerPageParam);//本次查询条数
        paramMap.put("currentPage",currentPage);
        paramMap.put("numPerPage",numPerPage);
        paramMap.put("totalCount",totalNum);
    }
    /**
     * 校验当前页数currentPage.<br/>
     * 1、先根据总记录数totalCount和每页记录数numPerPage，计算出总页数totalPage.<br/>
     * 2、判断页面提交过来的当前页数currentPage是否大于总页数totalPage，大于则返回totalPage.<br/>
     * 3、判断currentPage是否小于1，小于则返回1.<br/>
     * 4、其它则直接返回currentPage .
     *
     * @param totalCount
     *            要分页的总记录数 .
     * @param numPerPage
     *            每页记录数大小 .
     * @param currentPage
     *            输入的当前页数 .
     * @return currentPage .
     */
    public static int checkCurrentPage(int totalCount, int numPerPage,
                                       int currentPage) {
        int totalPage = countTotalPage(totalCount, numPerPage); // 最大页数
        if (currentPage > totalPage) {
//            // 如果页面提交过来的页数大于总页数，则将当前页设为总页数
//            // 此时要求totalPage要大于获等于1
//            if (totalPage < 1) {
//                return 1;
//            }
            return totalPage;
        } else if (currentPage < 1) {
            return 1; // 当前页不能小于1（避免页面输入不正确值）
        } else {
            return currentPage;
        }
    }
    /**
     * 计算总页数 .
     *
     * @param totalCount
     *            总记录数.
     * @param numPerPage
     *            每页记录数.
     * @return totalPage 总页数.
     */
    public static int countTotalPage(int totalCount, int numPerPage) {
        if (totalCount % numPerPage == 0) {
            // 刚好整除
            return totalCount / numPerPage;
        } else {
            // 不能整除则总页数为：商 + 1
            return totalCount / numPerPage + 1;
        }
    }
    /**
     * 校验页面输入的每页记录数numPerPage是否合法 .<br/>
     * 1、当页面输入的每页记录数numPerPage大于允许的最大每页记录数MAX_PAGE_SIZE时，返回MAX_PAGE_SIZE.
     * 2、如果numPerPage小于1，则返回默认的每页记录数DEFAULT_PAGE_SIZE.
     *
     * @param numPerPage
     *            页面输入的每页记录数 .
     * @return checkNumPerPage .
     */
    public static int checkNumPerPage(int numPerPage) {
        if (numPerPage > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        } else if (numPerPage < 1) {
            return DEFAULT_NUM_PER_PAGE;
        } else {
            return numPerPage;
        }
    }

    public static void setPageReturnInfo(Map<String,Object> returnMap,Map<String,Object> paramMap){
        returnMap.put("currentPage",paramMap.getOrDefault("currentPage",DEFAULT_PAGE_NUM));
        returnMap.put("numPerPage",paramMap.getOrDefault("numPerPage",DEFAULT_NUM_PER_PAGE));
        returnMap.put("totalCount",paramMap.getOrDefault("totalCount",0));
    }
}
package util;


import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static ResponseEntity<Map> returnResultSuccess(Object object){

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("data",object);
        resultMap.put("resultCode","0000");
        resultMap.put("resultMsg","success");

        return ResponseEntity.ok(resultMap);
    }

    public static ResponseEntity<Map> returnResultFial(String resultCode, String resultMsg){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("resultCode",resultCode);
        resultMap.put("resultMsg",resultMsg);
        LogUtil.info("returnResultFial，参数：",resultMap);
        return ResponseEntity.ok(resultMap);
    }
}

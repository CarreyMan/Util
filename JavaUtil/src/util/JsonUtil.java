package util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static volatile ObjectMapper objectMapper = new ObjectMapper();

    static {
        //允许出现特殊字符和转义符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, false) ;
        //允许出现单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
//        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
//        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES);
//        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    /**
     * Object => String
     */
    public static <T> String toJson(T src) {
        if (src == null) {
            return null;
        }

        try {
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        } catch (Exception e) {
            logger.error("op=ObjectToString", e);
            return null;
        }
    }

    /**
     * Object => byte[]
     */
    public static <T> byte[] object2Byte(T src) {
        if (src == null) {
            return null;
        }

        try {
            return src instanceof byte[] ? (byte[]) src : objectMapper.writeValueAsBytes(src);
        } catch (Exception e) {
            logger.error("op=ObjectToByte", e);
            return null;
        }
    }

    /**
     * String => Object
     */
    public static <T> T string2Object(String str, Class<T> clazz) {
        if (str == null || clazz == null) {
            return null;
        }
        str = escapesSpecialChar(str);
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            logger.error("op=ParseStringToObject String={} Class<T>={}", str, clazz.getName(), e);
            return null;
        }
    }

    /**
     * byte[] => Object
     */
    public static <T> T byte2Object(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(byte[].class) ? (T) bytes : objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            logger.error("op=ParseByteToObject byte={} Class<T>={}", bytes, clazz.getName(), e);
            return null;
        }
    }

    /**
     * String => Object
     */
    public static <T> T string2Object(String str, TypeReference<T> typeReference) {
        if (str == null || "".equals(str) || typeReference == null) {
            return null;
        }

        str = escapesSpecialChar(str);
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            logger.error("op=ParseStringToObject String={} TypeReference<T>={}", str, typeReference.getType(), e);
            return null;
        }
    }

    /**
     * byte[] => Object
     */
    public static <T> T byte2Object(byte[] bytes, TypeReference<T> typeReference) {
        if (bytes == null || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(byte[].class) ? bytes : objectMapper.readValue(bytes,
                    typeReference));
        } catch (Exception e) {
            logger.error("op=ParseByteToObject byte={} TypeReference<T>={}", bytes, typeReference.getType(), e);
            return null;
        }
    }

    /**
     * Escapes Special Character
     */
    private static String escapesSpecialChar(String str) {
        return str.replace("\n", "\\n").replace("\r", "\\r");
    }

    public static <T> T decodeJson(String jsonString, TypeReference<T> tr) {

        if (jsonString == null || "".equals(jsonString)) {
            return null;
        } else {
            try {
                return (T) objectMapper.readValue(jsonString, tr);
            } catch (Exception e) {
                logger.error("op=ParseStringToObject String={} TypeReference<T>={}", jsonString, tr.getType(), e);
            }
        }
        return null;
    }
}

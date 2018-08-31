package FormUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.rerloan.basic.util.IpUtil.verifyIp;

public class WebUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUtil.class);

    public static String getClientIp(HttpServletRequest request) {
        //1: get ip address from x-forwarded-for
        String ipAddress = request.getHeader("x-forwarded-for");
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            String[] ipAddressArray = ipAddress.split(",");
            if (ipAddressArray.length > 0) {
                ipAddress = ipAddressArray[ipAddressArray.length - 1].trim();
            }
        }
        if (verifyIp(ipAddress)) {
            return ipAddress;
        }

        //2: get ip address from REMOTE_ADDR
        ipAddress = request.getHeader("REMOTE_ADDR");

        if (verifyIp(ipAddress)) {
            return ipAddress;
        }

        //3: get ip address from X-Real-IP
        ipAddress = request.getHeader("X-Real-IP");
        if (verifyIp(ipAddress)) {
            return ipAddress;
        }

        ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("127.0.0.1")) {
            //根据网卡取本机配置的IP
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
                ipAddress = inet.getHostAddress();
            } catch (UnknownHostException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return ipAddress;
    }

    //////////////////////////////cookie相关////////////////////////////////////////////////////////////////////////////
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        javax.servlet.http.Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);
        return cookie != null && !"null".equals(cookie.getValue().toLowerCase()) ? cookie.getValue() : null;
    }
    public static void setCookie(HttpServletResponse response, String name, String value, long expire) {
        setCookie(response, name, value, "/", expire);
    }
    public static void setCookie(HttpServletResponse response, String name, String value, String path, long expire) {
        if(value == null) throw new IllegalArgumentException("cookie值不能为空");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        response.setHeader("SET-COOKIE", name + "=" + value
                + ";Path=" + path + ";expires=" + dateFormat.format(new Date(expire)) + " GMT" +
                ";HttpOnly");
    }
    public static void deleteCookie(HttpServletResponse response, String name) {
        deleteCookie(response, name, "/");
    }
    public static void deleteCookie(HttpServletResponse response, String name, String path) {
        response.setHeader("SET-COOKIE", name + "=;Path=" + path + ";expires=0;HttpOnly");
    }

    //////////////////////////////cookie相关end/////////////////////////////////////////////////////////////////////////

    //////////////////////////////session相关////////////////////////////////////////////////////////////////////////////
    public static void setSessionAttribute(HttpSession session, String sessionKey, String sessionId) {
        session.setAttribute(sessionKey, sessionId);
    }
    public static void removeSessionAttribute(HttpSession session, String sessionKey) {
        session.removeAttribute(sessionKey);
    }
    //////////////////////////////session相关end/////////////////////////////////////////////////////////////////////////

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

    public static String getUrl(HttpServletRequest request) {
        String url = request.getScheme() +"://" + request.getServerName()
                + ":" +request.getServerPort()
                + request.getServletPath();
        if (request.getQueryString() != null){
            url += "?" + request.getQueryString();
        }
        return url;
    }

    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static String urlDecode(String value, String encoding) {
        if (value == null || "".equals(value)) {
            return value;
        }
        try {
            return URLDecoder.decode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static String paramToQueryString(Map<String, String> params, String[] excludes, String charset) {

        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String value = p.getValue();
            if(excludes != null) {
                boolean ignore = false;
                for (String k : excludes) {
                    if(key.equals(k)) {
                        ignore = true;break;
                    }
                }
                if(ignore) continue;
            }

            if (!first) {
                paramString.append("&");
            }

            // Urlencode each request parameter
            paramString.append(urlEncode(key, charset));
            if (value != null) {
                paramString.append("=").append(urlEncode(value, charset));
            }

            first = false;
        }

        return paramString.toString();
    }

    public static String paramToQueryString(Map<String, String> params, String charset) {
        return paramToQueryString(params, null, charset);
    }

}

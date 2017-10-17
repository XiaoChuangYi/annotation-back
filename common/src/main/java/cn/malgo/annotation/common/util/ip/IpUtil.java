package cn.malgo.annotation.common.util.ip;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cn.malgo.annotation.common.util.log.LogUtil;
import org.apache.log4j.Logger;

/**
 * Created by 张钟 on 2017/9/26.
 */
public class IpUtil {

    private final static Logger log = Logger.getLogger(IpUtil.class);

    public static String getIp() {
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.error(log,e,"获取本机IP地址异常");
        }
        return null;
    }

}

package cn.malgo.annotation.common.dal.sequence;

import java.net.NetworkInterface;
import java.util.Enumeration;

import cn.malgo.annotation.common.util.log.LogUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * 全局唯一、时间有序序列号生成器
 * <pre>
 *      请参见https://github.com/twitter/snowflake
 * </pre>
 *
 * @author jiminglei
 * @version $Id: SequenceGenerator.java, v 0.1 2015年8月25日 下午9:10:34 jiminglei Exp $
 */

@Service
public class SequenceGeneratorImpl implements SequenceGenerator {

    /** 日志*/
    protected static final Logger logger             = Logger.getLogger(SequenceGenerator.class);

    /** 时间纪元*/
    private final long            twepoch            = 1434988800000L;

    /** 机器ID*/
    private final long            machineId;

    /** 毫秒内初始序列号*/
    private long                  sequence           = 0L;

    /** 机器ID字节数*/
    private final long            machineIdBits      = 10L;

    /** 最大机器ID*/
    private final long            maxMachineId       = -1L ^ -1L << this.machineIdBits;

    /** 毫秒内序列号字节数*/
    private final long            sequenceBits       = 12L;

    /** 机器ID偏移*/
    private final long            machineIdShift     = this.sequenceBits;

    /** 时间戳左偏移*/
    private final long            timestampLeftShift = this.sequenceBits + this.machineIdBits;

    /** 序列除余数*/
    private final long            sequenceMask       = -1L ^ -1L << this.sequenceBits;

    /** 时间戳*/
    private long                  lastTimestamp      = -1L;

    /**
     * 序列生成器
     */
    public SequenceGeneratorImpl() {
        this.machineId = getMachineId();
        if (machineId > maxMachineId || machineId < 0) {
            LogUtil.error(logger, "Get hardware Id failed: machineId > maxMachineId");
        }
    }

    /**
     * 通过业务类型获取流水号
     *
     */
    @Override
    public String nextCodeByType(CodeGenerateTypeEnum codeType) {

        return codeType.getValue() + nextId();

    }

    @Override
    public Long nextCodeByType() {
        return  Long.valueOf(nextId());
    }

    /**
     * 下一序列号
     *
     * @return
     */
    public synchronized long nextId() {

        long timestamp = this.timeGen();

        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1 & this.sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            LogUtil.error(logger,
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.",
                    (this.lastTimestamp - timestamp)));
        }

        this.lastTimestamp = timestamp;
        return timestamp - this.twepoch << this.timestampLeftShift
               | this.machineId << this.machineIdShift | this.sequence;
    }

    /**
     * 等到下一毫秒
     *
     * @param lastTimestamp
     * @return
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 当前时间
     *
     * @return
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 根据服务器硬件获取机器ID
     *
     * @return
     */
    protected long getMachineId() {
        try {
            byte[] mac = getFirstNoLoopbackAddress();
            long id = ((0x000000FF & (long) mac[mac.length - 1])
                       | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;

            return id;
        } catch (Exception e) {
            LogUtil.error(logger, e, "Parse unique machine ID from mac address failed!");
        }
        return 0L;
    }

    /**
     * 获取第一个有效的网卡mac地址
     *
     * @return
     */
    public static byte[] getFirstNoLoopbackAddress() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();

                if (!ni.isLoopback() && ni.isUp()) {
                    byte[] address = ni.getHardwareAddress();
                    if(address!=null){
                        return address;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error(logger, e, "Get hardware mac address failed!");
        }
        return new byte[0];
    }

}

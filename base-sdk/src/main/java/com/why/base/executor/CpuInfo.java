package com.why.base.executor;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Created by wuhongyun on 17-8-30.
 *
 * 通常核心线程数可以设为CPU数量+1，而最大线程数可以设为CPU的数量*2+1。
 * 线程创建数量是与cpu数量相较计算得来
 */

public class CpuInfo {
    //cpu文件位置
    public static final String cpuPath = "/sys/devices/system/cpu/";
    //默认cpu数量
    public static final int defaultCpuNum = 4;
    /**
     * cpu内核会以文件形式展现出来，文件位置：/sys/devices/system/cpu下
     * @return
     */
    public static int getCpuNumber(){
        int cpuNumber = 0;
        try {
            File cpuDir = new File(cpuPath);
            File[] files = cpuDir.listFiles(new CpuFileFilter());
            if (files!=null){
                cpuNumber = files.length;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (cpuNumber == 0){
            cpuNumber = defaultCpuNum;
        }

        return cpuNumber;
    }

    /**
     * 用于过滤cpu内核文件
     */
    static class CpuFileFilter implements FileFilter{


        @Override
        public boolean accept(File pathname) {
            //cpu内核文件通常会以cpu[0-9]文件辨识
            if (Pattern.matches("cpu[0-9]",pathname.getName())){
                return true;
            }
            return false;
        }
    }

    /**
     * 获取可用的内核数
     * @return
     */
    public static int getAvailableProcessors(){
        return Runtime.getRuntime().availableProcessors();
    }


}

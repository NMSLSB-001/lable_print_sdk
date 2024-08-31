package org.example;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.DriverPrinterConnection;

public class Test {
    public static void main(String[] args) throws ConnectionException {
        // 使用已知的 USB 端口号直接连接
        // Connection printerConnection = new UsbConnection("Port_#001", 50, 50); // 请将 "Port_#0001" 替换为实际的端口号
        Connection printerConnection = new DriverPrinterConnection("ZDesigner ZD888-203dpi ZPL");

        try {
            // 打开连接
            printerConnection.open();

            // 检查连接是否成功
            if (printerConnection.isConnected()) {
                System.out.println("打印机已连接");
                // 在此处添加打印任务代码
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            try {
                printerConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


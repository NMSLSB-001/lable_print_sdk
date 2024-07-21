package org.example;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.UsbConnection;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class Main {
    public static void main(String[] args) throws ConnectionException {
        // 创建与打印机的连接（此示例使用USB连接）
        Connection printerConnection = new UsbConnection("USB端口号"); // 请根据实际情况填写USB端口号

        try {
            // 打开连接
            printerConnection.open();

            // 创建打印机实例
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(printerConnection);
            PrinterStatus printerStatus = printer.getCurrentStatus();

            // 检查打印机状态
            if (printerStatus.isReadyToPrint) {
                // 打印指令示例（ZPL语言）
                String zplData = "^XA^FO50,50^ADN,36,20^FDHello, Zebra!^FS^XZ";
                printerConnection.write(zplData.getBytes());
                System.out.println("打印指令已发送");
            } else {
                // 打印机状态检查
                if (printerStatus.isPaused) {
                    System.out.println("打印机暂停");
                }
                if (printerStatus.isHeadOpen) {
                    System.out.println("打印机头打开");
                }
                if (printerStatus.isPaperOut) {
                    System.out.println("打印机缺纸");
                }
                System.out.println("打印机不准备好，无法打印");
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            try {
                printerConnection.close();
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }
}


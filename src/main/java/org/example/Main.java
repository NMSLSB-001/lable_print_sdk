package org.example;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class Main {
    public static void main(String[] args) {
        // 连接到打印机
        Connection connection = new TcpConnection("192.168.1.100", 9100); // 替换为你的打印机IP和端口
        try {
            connection.open();

            // 创建打印机实例
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

            // 定义自定义标签格式
            String zplCommand = "^XA^FO50,50^A0N,50,50^FDHello, World!^FS^FO50,150^B3N,N,100,Y,N^FD1234567890^FS^XZ";

            // 发送打印命令到打印机
            printer.sendCommand(zplCommand);

            // 关闭连接
            connection.close();
        } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
            e.printStackTrace();
        }
    }

}
package org.example;

import com.zebra.sdk.comm.*;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterDriver;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ConnectionException {

        String dllPath = new File("dll").getAbsolutePath();
        System.setProperty("java.library.path", dllPath);

        // 重新设置库路径以使其生效
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<DiscoveredPrinterDriver> list = getUsbPrintersAndAddToList();
        Connection printerConnection = getConnection(list);

        // 创建与打印机的连接（此示例使用USB连接）
        // Connection printerConnection = new UsbConnection("Port_#0001"); // 请根据实际情况填写USB端口号

        // Connection printerConnection = new TcpConnection("192.168.11.112", 9100);

        String prnFilePath = "C:\\Users\\cern\\Desktop\\test3.prn";
        try {
            String prnData = new String(Files.readAllBytes(Paths.get(prnFilePath)));
            prnData = prnData.replace("12345678>69", "12424443" + ">6" + "5");
            prnData = prnData.replace("{{qrcode}}", "123456789012");
            prnData = prnData.replace("????", "设备类型");
            prnData = prnData.replace("{{dt_1}}", "Yes_1");
            prnData = prnData.replace("{{dt_2}}", "Yes_2");
            prnData = prnData.replace("?????", "设备名称");
            prnData = prnData.replace("{{device_name_1}}", "Device_Name_1");
            prnData = prnData.replace("{{device_name_2}}", "Device_Name_2");

            // 打开连接
            printerConnection.open();

            // 创建打印机实例
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(printerConnection);
            PrinterStatus printerStatus = printer.getCurrentStatus();

            // 检查打印机状态
            if (printerStatus.isReadyToPrint) {
                // 打印指令示例（ZPL语言）
                String zplData = "^XA^FO50,50^ADN,36,20^FDHello, Kazami Tech!^FS^XZ";
                String zplData2 = "^XA\n" +
                        "^CF0,60\n" +
                        "^FO50,50^FDVTB-LIVE^FS\n" +
                        "\n" +
                        "^BY3,3,100\n" +
                        "^FO50,150^BC^FD987654321098^FS\n" +
                        "\n" +
                        "^FO50,300^GB500,0,3^FS\n" +
                        "\n" +
                        "^CFA,30\n" +
                        "^FO600,50^FD设备类型：^FS\n" +
                        "^FO600,100^FD新的测试文^FS\n" +
                        "^FO600,150^FD本内容^FS\n" +
                        "\n" +
                        "^FO600,200^GB500,0,3^FS\n" +
                        "\n" +
                        "^FO600,250^FD设备名称：^FS\n" +
                        "^FO600,300^FD新的^FS\n" +
                        "^FO600,350^FD测试文本^FS\n" +
                        "^FO600,400^FD内容^FS\n" +
                        "^FO600,450^FD替换^FS\n" +
                        "\n" +
                        "^FO800,50^BQN,2,10\n" +
                        "^FDQA,https://newurl.com^FS\n" +
                        "\n" +
                        "^XZ\n";

                // printerConnection.write(zplData2.getBytes());
                printerConnection.write(prnData.getBytes(StandardCharsets.UTF_8));
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
        } catch (ConnectionException | ZebraPrinterLanguageUnknownException | IOException e) {
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

    public static Connection getConnection(List<DiscoveredPrinterDriver> list) throws ConnectionException {
        DiscoveredPrinterDriver printer = list.get(0);
        System.out.println(printer.printerName);
        return new DriverPrinterConnection(printer.printerName);
    }

    private static List<DiscoveredPrinterDriver> getUsbPrintersAndAddToList() {
        List<DiscoveredPrinterDriver> list = new ArrayList<>();
        DiscoveredPrinterDriver[] discoPrinters;
        try {
            discoPrinters = UsbDiscoverer.getZebraDriverPrinters();
            list.addAll(Arrays.asList(discoPrinters));

        } catch (ConnectionException e) {
            list.clear();
        }
        return list;
    }
}



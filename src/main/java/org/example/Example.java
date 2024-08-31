package org.example;

import com.zebra.sdk.comm.*;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterDriver;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Example {
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

        Path csvPath = Paths.get("C:\\Users\\cern\\Desktop\\test.csv");

        List<DiscoveredPrinterDriver> list = getUsbPrintersAndAddToList();
        // Connection printerConnection = getConnection(list);
        Connection printerConnection = new DriverPrinterConnection("ZDesigner ZD888-203dpi ZPL");

        // 创建与打印机的连接（此示例使用USB连接）
        // Connection printerConnection = new UsbConnection("Port_#0001"); // 请根据实际情况填写USB端口号

        // Connection printerConnection = new TcpConnection("192.168.11.112", 9100);
        Path path = Paths.get("src/main/config/label_layout.prn");

        try {

            String prnData = new String(Files.readAllBytes(path));
//            prnData = prnData.replace("{{barcode_1}}", "12424443");
//            prnData = prnData.replace("{{barcode_2}}", "");
//            prnData = prnData.replace("{{qrcode}}", "123456789012");
            prnData = prnData.replace("?????_device_type", "设备类型");
//            prnData = prnData.replace("{{device_type_1}}", "测试");
//            prnData = prnData.replace("{{device_type_2}}", "Yes_2");
            prnData = prnData.replace("?????_device_name", "设备名称");
//            prnData = prnData.replace("{{device_name_1}}", "Device_Name_1");
//            prnData = prnData.replace("{{device_name_2}}", "Device_Name_2");
            String newPrnData = generatePrnDataFromCsv(csvPath, prnData);
//            // 打开连接
            printerConnection.open();
//
//            // 创建打印机实例
//            ZebraPrinter printer = ZebraPrinterFactory.getInstance(printerConnection);
//            PrinterStatus printerStatus = printer.getCurrentStatus();

            // 检查打印机状态
            printerConnection.write(newPrnData.getBytes(StandardCharsets.UTF_8));
            System.out.println("打印指令已发送");
//            if (printerStatus.isReadyToPrint) {
//                // 打印指令示例（ZPL语言）
//                String zplData = "^XA\n" +
//                        // "^CW1,E:SIMSUN.FNT^FS\n" +
//                        "^CI28 \n" +
//                        "^FO50,50^A1N,50,50^FD设备类型：^FS\n" +
//                        "^FO50,150^A1N,50,50^FD新的测试文本^FS\n" +
//                        "^XZ\n";
//
//                // printerConnection.write(zplData.getBytes());
//                printerConnection.write(prnData.getBytes(StandardCharsets.UTF_8));
//                System.out.println("打印指令已发送");
//            } else {
//                // 打印机状态检查
//                if (printerStatus.isPaused) {
//                    System.out.println("打印机暂停");
//                }
//                if (printerStatus.isHeadOpen) {
//                    System.out.println("打印机头打开");
//                }
//                if (printerStatus.isPaperOut) {
//                    System.out.println("打印机缺纸");
//                }
//                System.out.println("打印机未准备好，无法打印");
//            }
        } catch (ConnectionException | IOException e) {
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

    public static String generatePrnDataFromCsv(Path csvPath, String prnData) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(csvPath), Charset.forName("GB18030")));
        String headerLine = br.readLine(); // 读取表头
        String dataLine = br.readLine(); // 读取第一行数据

        Map<String, String> dataMap = new HashMap<>();
        if (headerLine != null && dataLine != null) {
            String[] headers = headerLine.split(",");
            String[] values = dataLine.split(",");

            for (int i = 0; i < headers.length; i++) {
                dataMap.put(headers[i].trim(), values[i].trim());
            }
        }

        // 替换占位符
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            prnData = prnData.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return prnData;
    }


}



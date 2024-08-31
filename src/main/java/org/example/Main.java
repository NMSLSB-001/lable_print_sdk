package org.example;

import com.zebra.sdk.comm.*;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Path csvPath = Paths.get("C:\\Users\\cern\\Desktop\\test.csv");
        Path prnPath = Paths.get("src/main/config/label_layout.prn");

        try {
            // 从CSV文件读取数据
            List<Map<String, String>> csvData = readCsvData(csvPath);

            // 打开连接
            Connection printerConnection = new DriverPrinterConnection("ZDesigner ZD888-203dpi ZPL");
            printerConnection.open();

            // 打印每一行CSV数据
            printData(csvData, prnPath, printerConnection);
            System.out.println("所有打印指令已发送");


            printerConnection.close();
        } catch (ConnectionException | IOException e) {
            e.printStackTrace();
        }
    }

    // 读取CSV文件并将每一行数据存入一个List中
    public static List<Map<String, String>> readCsvData(Path csvPath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(csvPath), Charset.forName("GB18030")));
        String headerLine = br.readLine(); // 读取表头
        String dataLine;

        if (headerLine != null) {
            String[] headers = headerLine.split(",");

            while ((dataLine = br.readLine()) != null) { // 逐行读取数据
                String[] values = dataLine.split(",");
                Map<String, String> dataMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    dataMap.put(headers[i].trim(), values[i].trim());
                }
                data.add(dataMap); // 将每一行的数据存入List
            }
        }
        return data;
    }

    // 根据CSV数据和PRN模板打印标签
    public static void printData(List<Map<String, String>> csvData, Path prnPath, Connection printerConnection) throws IOException, ConnectionException {
        for (Map<String, String> dataMap : csvData) {
            String prnData = new String(Files.readAllBytes(prnPath), StandardCharsets.UTF_8);
            // 替换抬头
            prnData = prnData.replace("?????_device_type", "设备类型");
            prnData = prnData.replace("?????_device_name", "设备名称");
            // 替换占位符
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                prnData = prnData.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            // 发送打印指令到打印机
            printerConnection.write(prnData.getBytes(StandardCharsets.UTF_8));
        }
    }
}

package cn.malgo.annotation.common.util;

import org.apache.poi.hssf.usermodel.*;

/**
 * Created by cjl on 2017/11/27.
 */
public class ExcelUtil {
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String []title, String [][]values, HSSFWorkbook wb){
        // 第一步，创建一个webbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }
        HSSFSheet sheet = wb.createSheet(sheetName);

        HSSFRow row = sheet.createRow(0);

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        HSSFCell cell = null;
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }
}

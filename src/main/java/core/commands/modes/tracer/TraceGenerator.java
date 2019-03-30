package core.commands.modes.tracer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Arthur Kupriyanov
 */
public class TraceGenerator {
    public File generate(String traceCommands, String fileName){
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet();
        int rowCount = 0;
        for(String line : traceCommands.split("\n")){
            int columnCount = 0;
            Row newRow = sheet.createRow(rowCount++);
            for (String column : line.split(" +")){
                Cell newCell = newRow.createCell(columnCount++);
                newCell.setCellValue(column);
            }
        }
        sheet.autoSizeColumn(1);

        try {
            String path = "src/main/resources/xls";
            File folder = new File(path);
            if (!folder.exists()){
                folder.mkdir();
            }
            String filePath = path + "/" + fileName + ".xls";
            File file = new File(filePath);
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();

            book.write(new FileOutputStream(file));
            book.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}

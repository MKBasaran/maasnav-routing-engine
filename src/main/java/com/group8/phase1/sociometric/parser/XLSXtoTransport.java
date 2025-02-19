package com.group8.phase1.sociometric.parser;

import com.group8.phase1.database.ConnectionGrabber;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class XLSXtoTransport {

    private static final String pathToDemographics = "./GeoJSON/demographics.xlsx";

    /**
     * Reads data from an XLSX file and inserts it into a database table.
     * The XLSX file contains demographic information.
     * The method skips the header row and starts reading data from the third row.
     * The data is extracted from the XLSX file, transformed to SQL statements,
     * and executed to insert the data into the database.
     *
     * @throws FileNotFoundException If the XLSX file is not found.
     * @throws IOException If an I/O error occurs while reading the XLSX file.
     * @throws SQLException If a database access error occurs.
     */
    public static void readXLSXFile() {
        try {
            Connection connection = ConnectionGrabber.getInstance().getConnectionJSON();
            FileInputStream fileInputStream = new FileInputStream(pathToDemographics);
            System.out.println("ok 1");
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            //to skip the header column
            int rowCount = sheet.getPhysicalNumberOfRows();
            for (int i = 3; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                String postCode = String.valueOf(row.getCell(0));
                double val1 = getValue(row.getCell(4));
                double val2 = getValue(row.getCell(5));
                double val3 = getValue(row.getCell(6));
                double val4 = getValue(row.getCell(7));
                double val5 = getValue(row.getCell(8));
                PreparedStatement preparedStatement = connection.prepareStatement(toSQL(postCode, (int) returnAverageIndex(val1, val2, val3, val4, val5)));
                preparedStatement.executeUpdate();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Creates an SQL statement to insert data into the CodeToAge table.
     *
     * @param postCode The postal code value to insert.
     * @param groupNo The group number value to insert.
     * @return The SQL statement as a string.
     */
    public static String toSQL(String postCode, int groupNo){
        return String.format("INSERT INTO CodeToAge VALUES('%s','%s')",postCode,groupNo);
    }

    /**
     * Calculates the average index based on the given values and returns the corresponding age group.
     *
     * @param val1 The first value.
     * @param val2 The second value.
     * @param val3 The third value.
     * @param val4 The fourth value.
     * @param val5 The fifth value.
     * @return The average index age group: 1, 2, 3, 4, or 5.
     */
    public static double returnAverageIndex(double val1, double val2, double val3, double val4, double val5){
        double ageGroup;
        if (val1 >= val2 && val1 >= val3 && val1 >= val4 && val1 >= val5) {
            ageGroup = 1;
        } else if (val2 >= val3 && val2 >= val4 && val2 >= val5) {
            ageGroup = 2;
        } else if (val3 >= val4 && val4 >= val5) {
            ageGroup = 3;
        } else if (val4 >= val5) {
            ageGroup = 4;
        } else {
            ageGroup = 5;
        }
        return ageGroup;
    }

    /**
     * Calculates the value of a cell in a spreadsheet.
     *
     * @param cell The cell to get the value from.
     * @return The value of the cell as a double. If the cell is null or empty, returns 0.0.
     *         If the cell contains a numeric value, returns the numeric value.
     *         If the cell contains a string value that can be parsed as a double, returns the parsed double value.
     *         Otherwise, returns 0.0.
     */
    private static double getValue(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }
}
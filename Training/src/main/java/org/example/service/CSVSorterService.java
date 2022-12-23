package org.example.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CSVSorterService {

    private static final Logger logger = LogManager.getLogger(CSVSorterService.class);
    private static final String CSV_SEPARATOR = ",";
    private static void writeToCSV(String[] headers, List<Student> students, File outputFile) throws IOException{
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
        StringBuilder line = new StringBuilder();
        for(String header: headers){
            line.append(header);
            line.append(CSV_SEPARATOR);
        }
        bw.write(String.valueOf(line));
        bw.newLine();
        for (Student student : students) {
            String oneLine = student.getYear() +
                    CSV_SEPARATOR +
                    student.getAge() +
                    CSV_SEPARATOR +
                    student.getEthnic() +
                    CSV_SEPARATOR +
                    student.getSex() +
                    CSV_SEPARATOR +
                    student.getArea() +
                    CSV_SEPARATOR +
                    student.getCount();
            bw.write(oneLine);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    public void sort(File inputFile, File outputFile, String sortOn){
        try {
            FileReader fileReader = new FileReader(inputFile);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] headers = csvReader.readNext();
            List<Student> students = new ArrayList<>();
            while(csvReader.peek() != null){
                String[] line = csvReader.readNext();
                String year = line[0];
                int age = Integer.parseInt(line[1]);
                String ethnic = line[2];
                String sex = line[3];
                Long area = Long.parseLong(line[4]);
                Long count = Long.parseLong(line[5]);
                Student student = new Student(year, age, ethnic, sex, area, count);
                students.add(student);
            }
            Comparator<Student> comparator = null;
            switch (sortOn){
                case "count":
                    comparator = Comparator.comparing(Student::getCount);
                    break;
                case "area":
                    comparator = Comparator.comparing(Student::getArea);
                    break;
                case "age":
                    comparator = Comparator.comparing(Student::getAge);
                    break;
            }
            students.sort(comparator);
            writeToCSV(headers, students, outputFile);

        } catch (FileNotFoundException e) {
            logger.error("The file was not found!");
        } catch (IOException e) {
            logger.error("There was an IOException with message: " + e.getMessage());
        } catch (CsvValidationException e) {
            logger.error("There was an CsvValidationException with message: " + e.getMessage());
        } catch (NumberFormatException e){
            logger.error("There was a problem converting the data from the csv file with message " + e.getMessage());
        }
    }
}

package org.example.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Student;

import java.io.*;
import java.util.*;

public class CSVSorterService {

    private static final Logger logger = LogManager.getLogger(CSVSorterService.class);
    private static final String CSV_SEPARATOR = ",";

    private static final Long LINES_READ = 100000L;

    public void sort(File inputFile, File outputFile) {
        try (CSVReader csvReader = new CSVReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            List<File> chunks = new ArrayList<>();
            List<CSVReader> chunkReaders = new ArrayList<>();
            List<Student> students = new ArrayList<>();
            int chunkIndex = 0;
            String[] header = csvReader.readNext();
            Long linesRead = 0L;

            while (csvReader.peek() != null) {
                String[] line = csvReader.readNext();
                String year = line[0];
                int age = Integer.parseInt(line[1]);
                String ethnic = line[2];
                String sex = line[3];
                Long area = Long.parseLong(line[4]);
                Long count = Long.parseLong(line[5]);
                Student student = new Student(year, age, ethnic, sex, area, count);
                students.add(student);

                linesRead++;

                if (students.size() >= LINES_READ) {
                    chunks.add(File.createTempFile("chunk" + chunkIndex, ".csv"));

                    try (BufferedWriter chunkWriter = new BufferedWriter(new FileWriter(chunks.get(chunkIndex)))) {
                        students.sort(Comparator.comparing(Student::getCount));

                        for (Student sortedStudent : students) {
                            chunkWriter.write(
                                    sortedStudent.getYear() + CSV_SEPARATOR
                                            + sortedStudent.getAge() + CSV_SEPARATOR
                                            + sortedStudent.getEthnic() + CSV_SEPARATOR
                                            + sortedStudent.getSex() + CSV_SEPARATOR
                                            + sortedStudent.getAge() + CSV_SEPARATOR
                                            + sortedStudent.getCount());
                            chunkWriter.newLine();
                        }
                    }

                    chunkReaders.add(new CSVReader(new FileReader(chunks.get(chunkIndex))));
                    students.clear();
                    chunkIndex++;
                }
            }

            // Sort and write the remaining lines
            if (!students.isEmpty()) {
                chunks.add(File.createTempFile("chunk" + chunkIndex, ".csv"));

                try (BufferedWriter chunkWriter = new BufferedWriter(new FileWriter(chunks.get(chunkIndex)))) {
                    students.sort(Comparator.comparing(Student::getCount));

                    for (Student sortedStudent : students) {
                        chunkWriter.write(
                                sortedStudent.getYear() + CSV_SEPARATOR
                                        + sortedStudent.getAge() + CSV_SEPARATOR
                                        + sortedStudent.getEthnic() + CSV_SEPARATOR
                                        + sortedStudent.getSex() + CSV_SEPARATOR
                                        + sortedStudent.getAge() + CSV_SEPARATOR
                                        + sortedStudent.getCount());
                        chunkWriter.newLine();
                    }
                }
            }

            chunkReaders.add(new CSVReader(new FileReader(chunks.get(chunkIndex))));

            List<Student> firstStudentFromChunks = new ArrayList<>();
            for (int y = 0; y < chunks.size(); y++) {
                String[] line = chunkReaders.get(y).readNext();
                String year = line[0];
                int age = Integer.parseInt(line[1]);
                String ethnic = line[2];
                String sex = line[3];
                Long area = Long.parseLong(line[4]);
                Long count = Long.parseLong(line[5]);
                Student student = new Student(year, age, ethnic, sex, area, count);
                firstStudentFromChunks.add(student);
            }

            writer.write(header[0] + CSV_SEPARATOR
                    + header[1] + CSV_SEPARATOR
                    + header[2] + CSV_SEPARATOR
                    + header[3] + CSV_SEPARATOR
                    + header[4] + CSV_SEPARATOR
                    + header[5] + "\n");

            for (int i = 0; i < linesRead; i++) {
                Long minValue = Long.MAX_VALUE;
                int readNext = 0;

                for (int j = 0; j < firstStudentFromChunks.size(); j++) {
                    if (firstStudentFromChunks.get(j).getCount() < minValue) {
                        readNext = j;
                        minValue = firstStudentFromChunks.get(j).getCount();
                    }
                }
                Student temp = firstStudentFromChunks.get(readNext);
                writer.write(temp.getYear() + CSV_SEPARATOR
                        + temp.getAge() + CSV_SEPARATOR
                        + temp.getEthnic() + CSV_SEPARATOR
                        + temp.getSex() + CSV_SEPARATOR
                        + temp.getAge() + CSV_SEPARATOR
                        + temp.getCount() + "\n");

                if (chunkReaders.get(readNext).peek() != null) {
                    String[] line = chunkReaders.get(readNext).readNext();
                    String year = line[0];
                    int age = Integer.parseInt(line[1]);
                    String ethnic = line[2];
                    String sex = line[3];
                    Long area = Long.parseLong(line[4]);
                    Long count = Long.parseLong(line[5]);
                    firstStudentFromChunks.set(readNext, new Student(year, age, ethnic, sex, area, count));
                }
                else {
                    chunkReaders.get(readNext).close();
                    chunkReaders.remove(readNext);
                    firstStudentFromChunks.remove(readNext);
                    if(chunks.get(readNext).delete())
                        chunks.remove(readNext);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("File wasn't found!");
        } catch (IOException e) {
            logger.error("There was an IOException with message " + e.getMessage());
        } catch (CsvValidationException e) {
            logger.error("There was an CsvValidationException with message " + e.getMessage());
        }
    }
}

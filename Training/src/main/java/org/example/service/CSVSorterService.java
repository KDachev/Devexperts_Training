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
            long linesRead = 0L;
            String[] header = csvReader.readNext();

            while (csvReader.peek() != null) {
                Student student = new Student(csvReader.readNext());
                students.add(student);
                linesRead++;

                if (students.size() >= LINES_READ) {
                    chunks.add(File.createTempFile("chunk" + chunkIndex, ".csv"));

                    try (BufferedWriter chunkWriter = new BufferedWriter(new FileWriter(chunks.get(chunkIndex)))) {
                        students.sort(Comparator.comparing(Student::getCount));

                        for (Student sortedStudent : students) {
                            writeToFile(chunkWriter, sortedStudent);
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
                        writeToFile(chunkWriter, sortedStudent);
                        chunkWriter.newLine();
                    }
                }
            }

            for(int i = 0; i < header.length - 1; i++){
                writer.write(header[i] + CSV_SEPARATOR);
            }
            writer.write(header[header.length - 1] + "\n");

            chunkReaders.add(new CSVReader(new FileReader(chunks.get(chunkIndex))));
            List<Student> firstStudentFromChunks = new ArrayList<>();
            for (int y = 0; y < chunks.size(); y++) {
                Student student = new Student(chunkReaders.get(y).readNext());
                firstStudentFromChunks.add(student);
            }

            for (int i = 0; i < linesRead; i++) {
                Long minValue = Long.MAX_VALUE;
                int writeNext = 0;

                for (int j = 0; j < firstStudentFromChunks.size(); j++) {
                    if (firstStudentFromChunks.get(j).getCount() < minValue) {
                        writeNext = j;
                        minValue = firstStudentFromChunks.get(j).getCount();
                    }
                }
                Student temp = firstStudentFromChunks.get(writeNext);
                writeToFile(writer, temp);
                writer.write("\n");

                if (chunkReaders.get(writeNext).peek() != null) {
                    firstStudentFromChunks.set(writeNext, new Student(chunkReaders.get(writeNext).readNext()));
                }
                else {
                    chunkReaders.get(writeNext).close();
                    chunkReaders.remove(writeNext);
                    firstStudentFromChunks.remove(writeNext);
                    if(chunks.get(writeNext).delete()) {
                        chunks.remove(writeNext);
                    }
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

    private void writeToFile(Writer writer, Student student) throws IOException {
        writer.write(student.getYear() + CSV_SEPARATOR
                + student.getAge() + CSV_SEPARATOR
                + student.getEthnic() + CSV_SEPARATOR
                + student.getSex() + CSV_SEPARATOR
                + student.getAge() + CSV_SEPARATOR
                + student.getCount());
    }
}

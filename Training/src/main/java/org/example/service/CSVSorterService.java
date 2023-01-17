package org.example.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.CustomComparator;
import org.example.model.CustomExceptions;
import org.example.model.Student;

import java.io.*;
import java.util.*;

public class CSVSorterService {

    private static final Logger logger = LogManager.getLogger(CSVSorterService.class);
    private static final String CSV_SEPARATOR = ",";

    private static final Long LINES_READ = 100000L;

    public void sort(File inputFile, File outputFile, String... compareOnFields) {
        List<CSVReader> chunkReaders = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            List<File> chunks = new ArrayList<>();
            List<Student> students = new ArrayList<>();
            int chunkIndex = 0;
            long linesRead = 0L;

            String[] header = csvReader.readNext();
            for (String field : compareOnFields) {
                if (Arrays.stream(header).map(String::toLowerCase).noneMatch(field.toLowerCase()::equals)) {
                    throw new CustomExceptions("Wrong field supplied");
                }
            }

            while (csvReader.peek() != null) {
                Student student = new Student(csvReader.readNext());
                students.add(student);
                linesRead++;

                if (students.size() >= LINES_READ || csvReader.peek() == null) {
                    chunks.add(File.createTempFile("chunk" + chunkIndex, ".csv"));
                    try (BufferedWriter chunkWriter = new BufferedWriter(new FileWriter(chunks.get(chunkIndex)))) {
                        students.sort(getComparator(compareOnFields));
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

            for (int i = 0; i < header.length - 1; i++) {
                writer.write(header[i] + CSV_SEPARATOR);
            }
            writer.write(header[header.length - 1]);
            writer.newLine();

            List<Student> firstStudentFromChunks = new ArrayList<>();
            for (int y = 0; y < chunks.size(); y++) {
                Student student = new Student(chunkReaders.get(y).readNext());
                firstStudentFromChunks.add(student);
            }

            for (int i = 0; i < linesRead; i++) {
                int writeNext = 0;
                Student temp = firstStudentFromChunks.stream().min(getComparator(compareOnFields)).orElseThrow();
                for (int j = 0; j < firstStudentFromChunks.size(); j++) {
                    if (firstStudentFromChunks.get(j) == temp) {
                        writeNext = j;
                    }
                }
                writeToFile(writer, temp);
                writer.newLine();

                if (chunkReaders.get(writeNext).peek() != null) {
                    firstStudentFromChunks.set(writeNext, new Student(chunkReaders.get(writeNext).readNext()));
                } else {
                    chunkReaders.get(writeNext).close();
                    chunkReaders.remove(writeNext);
                    firstStudentFromChunks.remove(writeNext);
                    if (chunks.get(writeNext).delete()) {
                        chunks.remove(writeNext);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("File wasn't found!");
        } catch (IOException e) {
            logger.error("There was an IOException with message: " + e.getMessage());
        } catch (CsvValidationException e) {
            logger.error("There was a CsvValidationException with message: " + e.getMessage());
        } catch (CustomExceptions e) {
            logger.error("There was a CustomException with message: " + e.getMessage());
        } finally {
            for(CSVReader csvReader : chunkReaders){
                if(csvReader != null){
                    try {
                        csvReader.close();
                    } catch (IOException e){
                        logger.error("There was an IOException with message: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void writeToFile(Writer writer, Student student) throws IOException {
        writer.write(student.getId() + CSV_SEPARATOR
                + student.getYear() + CSV_SEPARATOR
                + student.getAge() + CSV_SEPARATOR
                + student.getEthnic() + CSV_SEPARATOR
                + student.getSex() + CSV_SEPARATOR
                + student.getArea() + CSV_SEPARATOR
                + student.getCount());
    }

    private Comparator<Student> getComparator(String... fields) throws CustomExceptions {
        Comparator<Student> comparator = new CustomComparator<>();
        for (String field : fields) {
            switch (field.toLowerCase()) {
                case "year":
                    comparator = comparator.thenComparing(Student::getYear);
                    break;
                case "age":
                    comparator = comparator.thenComparing(Student::getAge);
                    break;
                case "ethnic":
                    comparator = comparator.thenComparing(Student::getEthnic);
                    break;
                case "sex":
                    comparator = comparator.thenComparing(Student::getSex);
                    break;
                case "area":
                    comparator = comparator.thenComparing(Student::getArea);
                    break;
                case "count":
                    comparator = comparator.thenComparing(Student::getCount);
                    break;
            }
        }
        return comparator;
    }
}

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

    private static final Long MAX_LINES_READ = 100000L;

    public void sort(File inputFile, File outputFile, String... compareOnFields) {
        List<CSVReader> chunkReaders = new ArrayList<>();
        List<File> chunks = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        try (CSVReader inputCSVReader = new CSVReader(new FileReader(inputFile));
             BufferedWriter outputFileWriter = new BufferedWriter(new FileWriter(outputFile))) {
            int chunkIndex = 0;
            long linesOfInputFile = 0L;

            String[] header = inputCSVReader.readNext();
            for (String field : compareOnFields) {
                if (Arrays.stream(header).map(String::toLowerCase).noneMatch(field.toLowerCase()::equals)) {
                    throw new CustomExceptions("Wrong field supplied");
                }
            }

            while (inputCSVReader.peek() != null) {
                Student student = new Student(inputCSVReader.readNext());
                students.add(student);
                linesOfInputFile++;

                if (students.size() >= MAX_LINES_READ || inputCSVReader.peek() == null) {
                    File chunk = File.createTempFile("chunk" + chunkIndex, ".csv");
                    chunks.add(chunk);
                    try (BufferedWriter chunkWriter = new BufferedWriter(new FileWriter(chunks.get(chunkIndex)))) {
                        students.sort(getComparator(compareOnFields));
                        for (Student sortedStudent : students) {
                            writeToFile(chunkWriter, sortedStudent);
                            chunkWriter.newLine();
                        }
                    }
                    chunkReaders.add(new CSVReader(new FileReader(chunk)));
                    students.clear();
                    chunkIndex++;
                }
            }

            for (String s : header) {
                outputFileWriter.write(s + CSV_SEPARATOR);
            }
            outputFileWriter.newLine();

            List<Student> firstStudentFromEachChunk = new ArrayList<>();
            for (CSVReader chunkReader : chunkReaders) {
                Student student = new Student(chunkReader.readNext());
                firstStudentFromEachChunk.add(student);
            }

            for (int i = 0; i < linesOfInputFile; i++) {
                Student temp = firstStudentFromEachChunk.stream().min(getComparator(compareOnFields)).orElseThrow();
                int indexOfMin = firstStudentFromEachChunk.indexOf(temp);
                writeToFile(outputFileWriter, temp);
                outputFileWriter.newLine();

                if (chunkReaders.get(indexOfMin).peek() != null) {
                    firstStudentFromEachChunk.set(indexOfMin, new Student(chunkReaders.get(indexOfMin).readNext()));
                } else {
                    chunkReaders.get(indexOfMin).close();
                    chunkReaders.remove(indexOfMin);
                    firstStudentFromEachChunk.remove(indexOfMin);
                    if (chunks.get(indexOfMin).delete()) {
                        chunks.remove(indexOfMin);
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

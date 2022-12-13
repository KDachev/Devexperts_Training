package org.example.service;

import com.github.davidmoten.bigsorter.Serializer;
import com.github.davidmoten.bigsorter.Sorter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class CSVSorterService {

    private static final Logger logger = LogManager.getLogger(CSVSorterService.class);

    public void sortIntegers(File inputFile, File outputFile,String compareUsing) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String header = br.readLine();
        if (header != null) {
            String[] columns = header.split(",");
            Serializer<CSVRecord> serializer = Serializer.csv(
                    CSVFormat.DEFAULT
                            .builder()
                            .setHeader(columns)
                            .setSkipHeaderRecord(true)
                            .build(),
                    StandardCharsets.UTF_8);
            Comparator<CSVRecord> comparator = (x, y) -> {
                int a = Integer.parseInt(x.get(compareUsing));
                int b = Integer.parseInt(y.get(compareUsing));
                return Integer.compare(a, b);
            };
            Sorter
                    .serializer(serializer)
                    .comparator(comparator)
                    .input(inputFile)
                    .output(outputFile)
                    .sort();
        }
        else {
            logger.warn("The input file has no headings");
        }
    }
}

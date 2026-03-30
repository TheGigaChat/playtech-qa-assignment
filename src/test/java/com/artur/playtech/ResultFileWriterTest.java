package com.artur.playtech;

import com.artur.playtech.utils.ResultFileWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ResultFileWriterTest {

    @Test
    void shouldWriteTextFile() {
        String filePath = "output/results.txt";
        String content = "Playtech QA assignment results";

        assertDoesNotThrow(() -> ResultFileWriter.writeToFile(filePath, content));
    }
}

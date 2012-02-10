package com.test.performance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.testng.annotations.Test;

@Test
public class PlainFileSystemPerformanceTest extends AbstractFileSystemPerformanceTest {
    private static final String TEST_OUTPUT_FILES_FOLDER = "c:/2/out";

    protected OutputStream createWriteOutput(File file) throws Exception {
        File parent = new File(TEST_OUTPUT_FILES_FOLDER);
        parent.mkdirs();
        File newFile = new File(parent, file.getName());
        if (newFile.exists()) {
            newFile.delete();
        }
        return new BufferedOutputStream(new FileOutputStream(newFile));
    }

    protected InputStream createReadInput(File file) throws Exception {
        File parent = new File(TEST_OUTPUT_FILES_FOLDER);
        return new BufferedInputStream(new FileInputStream(new File(parent, file.getName())));
    }
}

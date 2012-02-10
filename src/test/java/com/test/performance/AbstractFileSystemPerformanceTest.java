package com.test.performance;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public abstract class AbstractFileSystemPerformanceTest {
    private static final String TEST_FILES_FOLDER = "e:/";
    
    private static final int BUFFER_SIZE = 10000;
    
    private static final float MEASURE_TIME_MS = 100f;
    
    private final Logger log = Logger.getLogger(this.getClass());

    private long avgWriteTime = 0;
    private long avgReadTime = 0;
    
    // 1. Measure total time
    // 2. Measure throughput
    @Test
    public void testPerformance() {
        //Retrieve files list
        File[] files = getFiles();
        long startTime = System.currentTimeMillis();
        // write section
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
            log.info("Writing file " + file.getName() + ", size= " + file.length());
            long writeTime = System.currentTimeMillis();
            writeSampleFile(file);
            long operationTime = System.currentTimeMillis() - writeTime;
            long avgSpeedCalc = Math.round((file.length() * 1000f) / (1024 * operationTime));
            log.info("\tWrote file " + file.getName() + " in " + operationTime + " ms, calculated speed " + avgSpeedCalc + " Kb/s");
        }
        long avgWriteSpeed = avgWriteTime / files.length;
        long passed = System.currentTimeMillis() - startTime;
        long avgWriteSpeedCalc = Math.round((totalSize * 1000f) / (1024 * passed));
        String avgSpeedMsg = "with avg speed " + avgWriteSpeed;
        log.info("------------------------------------");
        log.info("Write operations (total size = " + (totalSize / (1024 * 1024)) + " Mb) performed in "
                 + passed + " ms " + (log.isDebugEnabled() ? avgSpeedMsg : "") + " Kb/s, calculated avg speed " + avgWriteSpeedCalc + " Kb/s");
        log.info("------------------------------------");
        // read section
        startTime = System.currentTimeMillis();
        for (File file : files) {
            log.info("Reading file " + file.getName() + ", size= " + file.length());
            long readTime = System.currentTimeMillis();
            readSampleFile(file);
            long operationTime = System.currentTimeMillis() - readTime;
            long avgSpeedCalc = Math.round((file.length() * 1000f) / (1024 * operationTime));
            log.info("\tRead file " + file.getName() + " in " + operationTime + " ms, calculated speed " + avgSpeedCalc + " Kb/s");
        }
        long avgReadSpeed = avgReadTime / files.length;
        passed = System.currentTimeMillis() - startTime;
        long avgReadSpeedCalc = Math.round((totalSize * 1000f) / (1024 * passed));
        avgSpeedMsg = "with avg speed " + avgReadSpeed;
        log.info("------------------------------------");
        log.info("Read operations (total size = " + (totalSize / (1024 * 1024)) + " Mb) performed in "
                + passed + " ms " + (log.isDebugEnabled() ? avgSpeedMsg : "") + " Kb/s, calculated avg speed " + avgReadSpeedCalc + " Kb/s");
        log.info("------------------------------------");
    }

    protected void writeSampleFile(File file) {
        InputStream is = null;
        OutputStream os = null;
        long avgSpeed = 0;
        int count = 0;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            os = createWriteOutput(file);
            // measure helper vars
            int readPerS = 0;
            long speedMillis = System.currentTimeMillis();
            // == read to buffer
            byte buffer[] = new byte[BUFFER_SIZE];
            int bytesRead = is.read(buffer);
            while( bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
                readPerS += bytesRead;
                long passed = System.currentTimeMillis() - speedMillis;
                if (passed > MEASURE_TIME_MS) {
                    long speed = Math.round((float) readPerS * 1000 / (1024 * passed));
                    log.debug("\t\tWrite Speed per second - " + speed + " Kb");
                    count++;
                    avgSpeed += speed;
                    speedMillis = System.currentTimeMillis();
                    readPerS = 0;
                    os.flush();
                }
                bytesRead = is.read(buffer);
            }
            long passed = System.currentTimeMillis() - speedMillis;
            long speed = Math.round((float) readPerS * 1000 / (1024 * passed));
            log.debug("\t\tWrite Speed per second - " + speed + " Kb");
            avgSpeed = (avgSpeed + speed) / (count + 1);
            log.debug("\tAverage write Speed per second - " + avgSpeed + " Kb");
            avgWriteTime += avgSpeed;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    protected abstract OutputStream createWriteOutput(File file) throws Exception;

    private void readSampleFile(File file) {
        InputStream is = null;
        long avgSpeed = 0;
        int count = 0;
        try {
            is = createReadInput(file);
            // measure helper vars
            int readPerS = 0;
            long speedMillis = System.currentTimeMillis();
            // == read to buffer
            byte buffer[] = new byte[BUFFER_SIZE];
            int bytesRead = is.read(buffer);
            while( bytesRead > 0) {
                readPerS += bytesRead;
                long passed = System.currentTimeMillis() - speedMillis;
                if (passed > MEASURE_TIME_MS) {
                    long speed = Math.round((float) readPerS * 1000 / (1024 * passed));
                    log.debug("\t\tRead Speed per second - " + speed + " Kb");
                    count++;
                    avgSpeed += speed;
                    speedMillis = System.currentTimeMillis();
                    readPerS = 0;
                }
                bytesRead = is.read(buffer);
            }
            long passed = System.currentTimeMillis() - speedMillis;
            long speed = Math.round((float) readPerS * 1000 / (1024 * passed));
            log.debug("\t\tRead Speed per second - " + speed + " Kb");
            avgSpeed = (avgSpeed + speed) / (count + 1);
            log.debug("\tAverage read Speed per second - " + avgSpeed + " Kb");
            avgReadTime += avgSpeed;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected abstract InputStream createReadInput(File file) throws Exception;

    private File[] getFiles() {
        return new File(TEST_FILES_FOLDER).listFiles(new FileFilter() {
            
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }
}

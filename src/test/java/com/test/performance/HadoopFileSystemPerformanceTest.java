package com.test.performance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class HadoopFileSystemPerformanceTest extends AbstractFileSystemPerformanceTest {

    private FileSystem hdfs;
    
    @BeforeClass
    public void setUp() throws IOException {
        Configuration conf = new Configuration();
        hdfs = FileSystem.get(conf);
    }
    
    @Override
    protected OutputStream createWriteOutput(File file) throws Exception {
        Path path = new Path(file.getName());
        if (hdfs.exists(path)) {
            hdfs.delete(path, false);
        }
        return hdfs.create(path);
    }

    @Override
    protected InputStream createReadInput(File file) throws Exception {
        return hdfs.open(new Path(file.getName()));
    }

}

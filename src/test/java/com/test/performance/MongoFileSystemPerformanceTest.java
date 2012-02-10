package com.test.performance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Test
public class MongoFileSystemPerformanceTest extends AbstractFileSystemPerformanceTest {
    private GridFS gfs;
    
    @BeforeClass
    public void setUp() throws UnknownHostException, MongoException {
        Mongo m = new Mongo( "localhost" , 27017 );
        DB db = m.getDB( "mydb" );
        gfs = new GridFS(db);
    }

    protected void writeSampleFile(File file) {
        GridFSInputFile gridFsFile;
        try {
            GridFSDBFile dbFile = gfs.findOne(file.getName());
            if (dbFile != null) {
                gfs.remove(file.getName());
            }
            gridFsFile = gfs.createFile(file);
            gridFsFile.put("filename", gridFsFile.getFilename());
            gridFsFile.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected OutputStream createWriteOutput(File file) throws Exception {
        return null;
    }

    @Override
    protected InputStream createReadInput(File file) throws Exception {
        GridFSDBFile dbFile = gfs.findOne(file.getName());
        return dbFile.getInputStream();
    }

}

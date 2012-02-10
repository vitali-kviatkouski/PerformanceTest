OVERVIEW:
Performance tests for testing:
- storing in plain filesystem
- storing in mongodb file system (GridFS)
- storing in Hadoop file system (HDFS)
Tests copy all files from specified directory in file system (AbstractFileSystemPerformanceTest.TEST_FILES_FOLDER - by default "e:/").
Its better if this directory would be mounted in RAM (for more clear results)
Performance tests are run by maven ('mvn test').

PREREQUISITES:
Be aware that for tests following prerequisites are required:
- mongodb server running on localhost:27017
- hadoop running on hdfs://localhost:9100 (configuration should be similar to that one in ,src/test/resources/hdfs-site.xml)
 
RESULTS:
Results are saved in target dir:
 - target/perfomance-stats-fs.log - results for plain filesystem
 - target/perfomance-stats-hdfs.log - results for hdfs
 - target/perfomance-stats-mongo.log - results for mongodb


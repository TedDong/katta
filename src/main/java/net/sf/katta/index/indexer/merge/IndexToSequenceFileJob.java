package net.sf.katta.index.indexer.merge;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.IdentityMapper;

public class IndexToSequenceFileJob {


  public void indexToSequenceFile(Path indexPath, Path outputPath) throws IOException {

    JobConf jobConf = new JobConf();

    // input and output format
    jobConf.setInputFormat(DfsIndexInputFormat.class);
    jobConf.setOutputFormat(SequenceFileOutputFormat.class);

    // input and output path
    jobConf.addInputPath(indexPath);
    jobConf.setOutputPath(outputPath);

    //set input and output key/value class
    jobConf.setOutputKeyClass(Text.class);
    jobConf.setOutputValueClass(DocumentInformation.class);

    // mapper and reducer
    jobConf.setMapperClass(IdentityMapper.class);
    jobConf.setReducerClass(IndexDuplicateReducer.class);

    // set document.duplicate.information.class
    InputStream asStream = IndexToSequenceFileJob.class.getResourceAsStream("/katta.index.properties");
    Properties properties = new Properties();
    properties.load(asStream);
    String className = (String) properties.get(DfsIndexInputFormat.DOCUMENT_INFORMATION);
    jobConf.set("document.duplicate.information.class", className);

    // run the job
    JobClient.runJob(jobConf);

  }

  public static void main(String[] args) throws IOException {
    IndexToSequenceFileJob index = new IndexToSequenceFileJob();
    index.indexToSequenceFile(new Path(args[0]), new Path(args[1]));
  }

}

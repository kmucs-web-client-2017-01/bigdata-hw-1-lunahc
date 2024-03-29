package kr.ac.kookmin.cs.bigdata;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.json.JSONException;
import org.json.JSONObject;

public class ReviewScore extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        int res = ToolRunner.run(new Configuration(), new ReviewScore(), args);
        
        System.exit(res);
    }
    
    @Override
    public int run(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        
        Job job = Job.getInstance(getConf());
        job.setJarByClass(ReviewScore.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        job.waitForCompletion(true);
        
        return 0;
    }
    
    public static class Map extends Mapper<LongWritable, Text, Text, DoubleWritable> {
        private Text word = new Text();
        
        @Override
        public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
            try {
                JSONObject jsonObject = new JSONObject(value.toString());
                word.set((String) jsonObject.get("asin"));
                context.write(word, new DoubleWritable(jsonObject.getDouble("overall")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static class Reduce extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
        throws IOException, InterruptedException {
            double sum = 0;
            int count = 0;
            for (DoubleWritable val : values) {
                sum += val.get();
                count += 1;
            }
            
            context.write(key, new DoubleWritable(sum/count));
        }
    }
}

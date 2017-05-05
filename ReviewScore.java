package kr.ac.kookmin.cs.bigdata;

public class ReviewScore extends Configured implements Tool {
    public static void main(String[] args) throws Exception {

    }

    @Override
    public int run(String[] args) throws Exception {

    }
   
    public static class Map extends Mapper<LongWritable, Text, Text, DoubleWritable> {
        
    }

    public static class Reduce extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    }
}


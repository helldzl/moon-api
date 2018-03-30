package com.mifan.batch.analyzer.classifier;

import com.mifan.batch.analyzer.AbstractTests;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.mahout.utils.vectors.io.SequenceFileVectorWriter;
import org.junit.Test;

import java.util.function.Consumer;

import static org.apache.hadoop.io.SequenceFile.Writer;
import static org.apache.hadoop.io.SequenceFile.createWriter;

/**
 * <p>实例化一次writer, 写入训练数据, 然后再close writer</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/5/22
 */
public class HadoopTests extends AbstractTests {

    private Configuration conf = new Configuration();

    @Test
    public void name() throws Exception {
        String pathString = "hdfs://192.168.1.229:9000/user/hadoop/test/hello/thomann2-seq";
        write(LongWritable.class, Text.class, pathString, writer -> {
            try {
                for (int i = 0; i < 1; i++) {
                    System.out.println(i);
                    LongWritable key = new LongWritable(i);
                    // VectorWritable vector = new VectorWritable();

                    Text value = new Text("Most of these toxic bachelors seriously consider settling down once they have a mid-life crisis and see the writing on the wall. Then they try to get married in a hurry to have kids, and to ensure their own immortality through their offspring.");
                    writer.append(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private <T extends Writable, U extends Writable> void write(Class<T> keyClass, Class<U> valueClass, String pathString, Consumer<Writer> consumer) throws Exception {
        try (Writer writer = createWriter(conf,
                Writer.file(new Path(pathString)),
                Writer.keyClass(keyClass),
                Writer.valueClass(valueClass))) {
            // 监听数据, 超时后退出, 并close writer

            // 对向量进行处理, 并调用Writer的写入方法
            new SequenceFileVectorWriter(writer);
            consumer.accept(writer);
        }
    }
}

package cn.edana.benchmark;

import lombok.Value;

/**
 * @author 郑喜荣
 * @create 2020-07-22 7:45 下午
 **/
@Value
public class BenchmarkResult {
    private long total;
    private long success;
    private long avgRT;
    private long _95thRT;
}

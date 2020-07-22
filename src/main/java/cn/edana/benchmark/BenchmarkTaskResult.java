package cn.edana.benchmark;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author 郑喜荣
 * @create 2020-07-22 7:45 下午
 **/
@Value
public class BenchmarkTaskResult {
    private int status;
    private long responseTime;
}

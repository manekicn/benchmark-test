package cn.edana.benchmark;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author 郑喜荣
 * @create 2020-07-22 7:38 下午
 **/
public class BenchmarkExecutor {
    private ExecutorService executorService;

    private String url;

    private int timeout;

    private int times;

    public BenchmarkExecutor(String url, int concurrency, int timeout, int times) {
        this.url = url;
        this.timeout = timeout;
        this.times = times;
        this.executorService = new ScheduledThreadPoolExecutor(concurrency,
            new BasicThreadFactory.Builder().namingPattern("benchmark-schedule-pool-%d").daemon(true).build());
    }

    public BenchmarkResult execute() throws InterruptedException, ExecutionException, TimeoutException {
        CompletionService cs = new ExecutorCompletionService<>(executorService);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout*1000).setConnectTimeout(timeout*1000).build();
        for (int i = 0; i < times; i++) {
            final HttpGet request = new HttpGet(this.url);
            request.setConfig(requestConfig);
            cs.submit(new BenchmarkTask(HttpClients.createDefault(), request));
        }

        List<BenchmarkTaskResult> results = new ArrayList<>(times);
        for (int i = 0; i< times; i++ ){
            results.add((BenchmarkTaskResult) cs.take().get());
        }

        Supplier<Stream<BenchmarkTaskResult>> supplier = () -> results.stream().filter(r -> r.getStatus() == 200);

        long success = supplier.get().count();
        long avgRT = supplier.get().mapToLong(r -> r.getResponseTime()).sum() / times;
        final long _95RT = supplier.get().sorted((r1, r2) -> {
            if (r1.getResponseTime() > r2.getResponseTime()) {
                return 1;
            }
            if (r1.getResponseTime() < r2.getResponseTime()) {
                return -1;
            }
            return 0;
        }).skip((long) (times * .95)).findFirst().get().getResponseTime();

        return new BenchmarkResult(times, success, avgRT, _95RT);
    }
}

package cn.edana.benchmark;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.Callable;

/**
 * @author 郑喜荣
 * @create 2020-07-22 7:38 下午
 **/
public class BenchmarkTask implements Callable {

    CloseableHttpClient httpClient;

    HttpRequestBase request;

    public BenchmarkTask(CloseableHttpClient httpClient, HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public BenchmarkTaskResult call() throws Exception {
        long t = System.currentTimeMillis();
        final CloseableHttpResponse response = httpClient.execute(request);
        return new BenchmarkTaskResult(response.getStatusLine().getStatusCode(), System.currentTimeMillis() - t);
    }
}

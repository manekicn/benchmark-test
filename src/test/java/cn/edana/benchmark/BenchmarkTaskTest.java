package cn.edana.benchmark;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BenchmarkTaskTest {

    private CloseableHttpClient client;

    @Before
    public void setup() {
        client = HttpClients.createDefault();
    }

    @Test
    public void testCall() throws Exception {
        HttpGet get = new HttpGet("http://www.baidu.com");
        BenchmarkTask caller = new BenchmarkTask(client, get);
        final BenchmarkTaskResult call = caller.call();
        assertEquals(200, call.getStatus());
    }
}
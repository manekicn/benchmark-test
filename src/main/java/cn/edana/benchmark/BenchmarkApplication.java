package cn.edana.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

/**
 * @author 郑喜荣
 * @create 2020-07-22 7:18 下午
 **/
@SpringBootApplication
public class BenchmarkApplication implements CommandLineRunner {

    public static final String OPT_REQUEST = "n";
    public static final String OPT_CONCURRENCY = "c";
    public static final String OPT_TIMELIMIT = "t";
    public static final String OPT_TIMEOUT = "s";

    public static void main(String[] args) {
        SpringApplication.run(BenchmarkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /* Build command line */
        Options options = buildCommandLineOptions();

        /* Parse command line */
        CommandLine result = parseCommandLine(args, options);
        int times = Integer.parseInt(result.getOptionValue(OPT_REQUEST));
        int concurrency = Integer.parseInt(result.getOptionValue(OPT_CONCURRENCY));
        int timeout = Integer.parseInt(result.getOptionValue(OPT_TIMEOUT));
        String url = result.getArgList().get(result.getArgList().size() - 1);

        BenchmarkExecutor benchmarkExecutor = new BenchmarkExecutor(url, concurrency, timeout, times);
        final BenchmarkResult r = benchmarkExecutor.execute();

        System.out.println(String.format("总共压测%d次, 成功%d次，平均响应时间%dms, 95响应时间%dms", r.getTotal(), r.getSuccess(), r.getAvgRT(), r.get_95thRT()));

    }

    private Options buildCommandLineOptions() {
        Options options = new Options();

        options.addOption(Option.builder(OPT_REQUEST)
            .hasArg()
            .argName("request")
            .desc("Number of requests to perform")
            .build());
        options.addOption(Option.builder(OPT_CONCURRENCY)
            .hasArg()
            .argName("concurrency")
            .desc("Number of multiple requests to make at a time")
            .build());

        options.addOption(Option.builder(OPT_TIMELIMIT)
            .hasArg()
            .argName("timelimit")
            .desc("Seconds to max. to spend on benchmarking. This implies -n 50000")
            .build());

        options.addOption(Option.builder(OPT_TIMEOUT)
            .hasArg()
            .argName("timeout")
            .desc("Seconds to max. wait for each response. Default is 30 seconds")
            .build());

        options.addOption(Option.builder("h")
            .desc("help for bt")
            .build());

        return options;
    }

    private CommandLine parseCommandLine(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        CommandLine result = null;
        try {
            result = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
        }

        if (result.getOptions().length == 0 || result.hasOption("h")) {
            printHelp(options);
        }
        return result;
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar bt.jar [options] <url>\n\noptions: ", options, false);
        System.exit(1);
    }
}

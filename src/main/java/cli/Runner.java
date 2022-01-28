package cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(subcommands = {Identifier.class})
public class Runner implements Callable<Integer> {
    @CommandLine.Option(names = "--ffmpeg", defaultValue = "/usr/bin/ffmpeg", description = "ffmpeg location (Default : ${DEFAULT-VALUE}")
    String ffmpegLocation;

    @CommandLine.Option(names = "-ds", defaultValue = "./data.ser", description = "Dataset location (Default : ${DEFAULT-VALUE}")
    String datasetLocation;

    public Integer call() {
        System.out.println(ffmpegLocation + " -- " + datasetLocation);
        return 0;
    }
}

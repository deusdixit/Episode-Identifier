package cli;

import io.DataSet;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(subcommands = {Identifier.class, Loader.class}, scope = CommandLine.ScopeType.INHERIT)
public class Runner implements Callable<Integer> {
    @CommandLine.Option(names = "--ffmpeg", defaultValue = "/usr/bin/ffmpeg", description = "ffmpeg location (Default : ${DEFAULT-VALUE}")
    String ffmpegLocation;

    @CommandLine.Option(names = "-ds", defaultValue = "./data.ser", description = "Dataset location (Default : ${DEFAULT-VALUE}", converter = DatasetConverter.class)
    DataSet dataset;

    @CommandLine.Parameters(paramLabel = "FILE", description = "one or more episode files")
    File[] files;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display help")
    private boolean helpRequested = false;

    public Integer call() {
        System.out.println(ffmpegLocation + " -- " + dataset.length());
        return 0;
    }
}

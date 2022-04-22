package cli;


import gui.MainGui;
import io.DataSet;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(subcommands = {Identifier.class, Loader.class, Downloader.class}, scope = CommandLine.ScopeType.INHERIT)
public class Runner implements Callable<Integer> {

    @CommandLine.Option(names = "--ffmpeg", defaultValue = "/usr/bin/ffmpeg", description = "ffmpeg location (Default : ${DEFAULT-VALUE}")
    String ffmpegLocation;

    @CommandLine.Option(names = "-ds", defaultValue = "./data.ser", description = "Dataset location (Default : ${DEFAULT-VALUE}", converter = DatasetConverter.class)
    DataSet dataset;

    @CommandLine.Parameters(paramLabel = "FILE", description = "one or more episode files", scope = CommandLine.ScopeType.INHERIT)
    File[] files;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display help")
    private boolean helpRequested = false;

    @CommandLine.Option(names = {"-ng", "--nogui"}, description = "display gui")
    private boolean nogui = false;

    @CommandLine.Option(names = {"-v", "--debug"}, description = "Debugging")
    private boolean debug = false;

    public static boolean DEBUG_MODE = false;

    public Integer call() {
        if (debug) {
            System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
            DEBUG_MODE = true;
        }
        MainGui mw = new MainGui();
        mw.show();
        return 0;
    }


}

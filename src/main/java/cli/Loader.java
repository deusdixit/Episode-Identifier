package cli;

import io.DataSet;
import io.Dataloader;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "loader", description = "load subtitle files")
public class Loader implements Callable<Integer> {
    @CommandLine.Parameters(paramLabel = "FOLDER", description = "folder with subtitles files")
    private File file;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "search recursive for subtitle files")
    private boolean recursive = false;


    @Override
    public Integer call() throws Exception {
        DataSet ds = Dataloader.loadSrt(file.toPath(), recursive);
        Dataloader.save(ds, Paths.get("./data.ser"));
        return 0;
    }
}

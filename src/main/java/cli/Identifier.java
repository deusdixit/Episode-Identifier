package cli;

import io.Extract;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "identify", description = "analyzes and tries to identify the episode files")
public class Identifier implements Callable<Integer> {

    @CommandLine.ParentCommand
    private Runner parent;

    @Override
    public Integer call() throws Exception {
        File[] files = parent.files;
        for (int i = 0; i < files.length; i++) {
            if (files[i].exists()) {
                File[] subs = Extract.extractAll(files[i].toPath());

            }
        }
        return 0;
    }
}

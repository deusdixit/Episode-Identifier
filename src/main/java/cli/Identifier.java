package cli;

import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import model.Candidate;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "identify", description = "analyzes and tries to identify the episode files")
public class Identifier implements Callable<Integer> {

    @CommandLine.ParentCommand
    private Runner parent;

    @CommandLine.Option(names = {"-d", "--dry"}, defaultValue = "false", description = "Dry run")
    private boolean dry = false;

    @CommandLine.Option(names = {"-c", "--copy"}, defaultValue = "false", description = "Copy files")
    private boolean rename = false;

    @CommandLine.Option(names = "-u", description = "Opensubtitles.com Username")
    String username = null;

    @CommandLine.Option(names = "-p", description = "Opensubtitles.com Username")
    String password = null;

    @CommandLine.Option(names = "-t", description = "Opensubtitles.com Api-Key")
    String token = null;


    @Override
    public Integer call() throws Exception {
        Opensubtitles os = null;
        if (username != null && password != null && token != null) {
            os = new Opensubtitles(username, password, token);
            LoginResult lr = os.login();
            if (lr == null || lr.status != 200) {
                os = null;
                System.out.println("Couldn't log in");
            }
        }
        File[] files = parent.files;
        Candidate[] cans = new Candidate[files.length];
        for (int i = 0; i < files.length; i++) {
            if (files[i].exists()) {
                Candidate c = new Candidate(files[i], parent.dataset);
                cans[i] = c;
            } else {
                System.out.println();
            }
        }
        for (int i = 0; i < cans.length; i++) {
            if (cans[i] != null) {
                String sugg = cans[i].getSuggestion(os);
                System.out.println(cans[i].getAbsolutePath() + " -> " + sugg);
                if (!dry) {
                    if (!rename) {
                        Files.copy(Paths.get(cans[i].getAbsolutePath()), Paths.get(sugg));
                    } else {
                        Files.move(Paths.get(cans[i].getAbsolutePath()), Paths.get(sugg), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        return 0;
    }
}

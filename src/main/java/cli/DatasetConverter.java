package cli;

import io.DataSet;
import io.Dataloader;
import picocli.CommandLine;

import java.io.File;

public class DatasetConverter implements CommandLine.ITypeConverter<DataSet> {
    @Override
    public DataSet convert(String s) throws Exception {
        File f = new File(s);
        if (f.exists()) {
            return Dataloader.load(f.toPath());
        } else {
            return null;
        }
    }
}

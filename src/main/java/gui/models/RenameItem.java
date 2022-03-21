package gui.models;

import javafx.beans.value.ObservableValueBase;

import java.io.File;

public class RenameItem extends ObservableValueBase<File> {

    private File file;

    public RenameItem(File file) {
        this.file = file;
    }

    public RenameItem() {
        file = null;
    }

    public void setValue(File f) {
        file = f;
        fireValueChangedEvent();
    }

    @Override
    public String toString() {
        return file.getName();
    }

    @Override
    public File getValue() {
        return file;
    }
}

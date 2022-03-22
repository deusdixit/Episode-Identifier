package gui.models;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.io.File;
import java.util.List;

public class RenamePreviewWrapper implements Observable {

    private RenameItem renameItem;
    private PreviewItem previewItem;
    public boolean isSet = false;

    public RenamePreviewWrapper(RenameItem rI, PreviewItem pI) {
        renameItem = rI;
        previewItem = pI;
    }

    public RenamePreviewWrapper(RenameItem rI) {
        renameItem = rI;
        previewItem = new PreviewItem();
    }

    public RenamePreviewWrapper(PreviewItem pI) {
        renameItem = new RenameItem();
        previewItem = pI;
    }

    public void setRenameItem(File rI) {
        renameItem.setValue(rI);
    }

    public void setPreviewItem(List<PreviewItem.ComboItem> pI) {
        previewItem.setValue(pI);
        if (pI.size() > 0) {
            isSet = true;
        }
    }

    public RenameItem getRenameItem() {
        return renameItem;
    }

    public PreviewItem getPreviewItem() {
        return previewItem;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        renameItem.addListener(invalidationListener);
        previewItem.addListener(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        renameItem.removeListener(invalidationListener);
        previewItem.addListener(invalidationListener);
    }
}

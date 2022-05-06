package gui.models;

import gui.components.PreviewListItem;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableBooleanValue;

import java.util.List;

public class RenamePreviewWrapper implements Observable, Comparable<RenamePreviewWrapper> {

    private final RenameItem renameItem;
    private final PreviewItem previewItem;
    public boolean isSet = false;

/*    public RenamePreviewWrapper(RenameItem rI, PreviewItem pI) {
        renameItem = rI;
        previewItem = pI;
    }*/

    public RenamePreviewWrapper(RenameItem rI) {
        renameItem = rI;
        previewItem = new PreviewItem();
    }

/*    public RenamePreviewWrapper(PreviewItem pI) {
        renameItem = new RenameItem();
        previewItem = pI;
    }*/

/*    public void setRenameItem(File rI) {
        renameItem.setValue(rI);
    }*/

    public void setPreviewItem(List<PreviewListItem.ComboItem> pI) {
        previewItem.setValue(pI);
        if (pI.size() > 0) {
            isSet = true;
        }
    }

    public ObservableBooleanValue isActive() {
        return getPreviewItem().isActive();
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

    @Override
    public int compareTo(RenamePreviewWrapper renamePreviewWrapper) {
        return this.renameItem.getValue().compareTo(renamePreviewWrapper.renameItem.getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RenamePreviewWrapper) {
            RenamePreviewWrapper c = (RenamePreviewWrapper) obj;
            return this.compareTo(c) == 0;
        } else {
            return false;
        }
    }
}

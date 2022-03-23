package gui.models;

import javafx.scene.control.ListCell;

public class ListFactoryItem extends ListCell<RenamePreviewWrapper> {

    private final boolean isRenameList;

    public ListFactoryItem(boolean isRenameList) {
        this.isRenameList = isRenameList;
    }

    @Override
    public void updateItem(RenamePreviewWrapper item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isRenameList) {
                this.setMaxHeight(25);
                this.setMinHeight(25);
                this.setPrefHeight(25);
                setText(item.getRenameItem().toString());
                setGraphic(null);
            } else {
                this.setMaxHeight(25);
                this.setMinHeight(25);
                this.setPrefHeight(25);
                setText(null);
                setGraphic(item.getPreviewItem().getValue());
            }
        }
    }

}

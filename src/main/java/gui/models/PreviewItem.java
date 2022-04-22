package gui.models;

import gui.components.PreviewListItem;
import javafx.beans.value.ObservableValueBase;

import java.util.List;

public class PreviewItem extends ObservableValueBase<PreviewListItem> {

    private PreviewListItem item;

    @Override
    public PreviewListItem getValue() {
        if (item != null && item.getComboBox() != null) {
            item.getComboBox().getSelectionModel().clearSelection();
            item.getComboBox().getSelectionModel().selectFirst();
        }
        return item;
    }

    public PreviewItem() {
        item = null;
    }

    public void setValue(List<PreviewListItem.ComboItem> sims) {
        item = new PreviewListItem(sims);
        fireValueChangedEvent();
    }

    public PreviewItem(List<PreviewListItem.ComboItem> sims) {
        setValue(sims);
    }

    public boolean isActive() {
        return item.isActive();
    }

    public double getSelectedAccuracy() {
        return item.getComboBox().getSelectionModel().getSelectedItem().getSim().getAccuarcy();
    }


    public String getSelectedFilename() {
        if (item.getComboBox() == null) {
            return "";
        } else {
            return item.getComboBox().getSelectionModel().getSelectedItem().getFilename();
        }
    }
}

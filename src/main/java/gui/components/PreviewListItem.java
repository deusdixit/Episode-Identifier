package gui.components;

import hamming.Similarity;
import io.AttributesWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import utils.Naming;

import java.util.List;

public class PreviewListItem extends HBox {

    private ComboBox<ComboItem> item;
    private CheckBox checkBoxItem;
    private Label errorLabel;


    public PreviewListItem(List<ComboItem> sims) {
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        checkBoxItem = new CheckBox();
        this.getChildren().add(checkBoxItem);
        if (sims.size() > 0) {
            initCombo(sims);
            this.getChildren().add(item);
            if (item.getItems().size() > 0 && item.getSelectionModel().getSelectedItem().sim.getAccuarcy() < 0.2) {
                checkBoxItem.setSelected(false);
            } else {
                checkBoxItem.setSelected(true);
            }
        } else {
            errorLabel = new Label(" no subtitle found");
            this.getChildren().add(errorLabel);
            checkBoxItem.setDisable(true);
        }
    }

    private void initCombo(List<ComboItem> sims) {
        item = new ComboBox<>();
        item.setMaxHeight(25);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setMinHeight(25);
        item.setPrefHeight(25);
        HBox.setHgrow(item, Priority.SOMETIMES);
        item.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ComboItem>() {
            @Override
            public void changed(ObservableValue<? extends ComboItem> observableValue, ComboItem comboItem, ComboItem t1) {
                item.getStylesheets().clear();
                if (t1 != null && t1.sim.getAccuarcy() < 0.2) {
                    item.getStylesheets().add(getClass().getResource("/css/ComboBoxItemSelectedRed.css").toExternalForm());
                } else if (t1 != null && t1.sim.getAccuarcy() >= 0.2) {
                    item.getStylesheets().add(getClass().getResource("/css/ComboBoxItemSelectedGreen.css").toExternalForm());
                }
            }
        });
        item.setCellFactory(new Callback<ListView<ComboItem>, ListCell<ComboItem>>() {
            @Override
            public ListCell<ComboItem> call(ListView<ComboItem> comboItemListView) {
                return new ListCell<ComboItem>() {
                    @Override
                    protected void updateItem(ComboItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            this.getStylesheets().clear();
                            if (item.sim.getAccuarcy() < 0.2) {
                                this.getStylesheets().add(getClass().getResource("/css/ComboBoxItemRed.css").toExternalForm());
                            } else {
                                this.getStylesheets().add(getClass().getResource("/css/ComboBoxItemGreen.css").toExternalForm());
                            }
                            setText(item.toString());
                        }
                    }
                };
            }
        });
        item.getItems().addAll(sims);
        item.getSelectionModel().selectFirst();
    }

    public ComboBox<ComboItem> getComboBox() {
        return item;
    }

    public boolean isActive() {
        return checkBoxItem.isSelected();
    }

    public static class ComboItem {
        private final Similarity.SimResult sim;
        private AttributesWrapper aWrapper;

        public ComboItem(Similarity.SimResult sim, AttributesWrapper aWrapper) {
            this.sim = sim;
            this.aWrapper = aWrapper;
        }

        public Similarity.SimResult getSim() {
            return sim;
        }

        public void setAtrributesWrapper(AttributesWrapper aWrapper) {
            this.aWrapper = aWrapper;
        }

        public String getFilename() {
            if (aWrapper != null) {
                return Naming.getInstance().getName(aWrapper);
            } else {
                return "";
            }
        }

        @Override
        public String toString() {
            return (int) (sim.getAccuarcy() * 100) + "% - " + Naming.getInstance().getName(aWrapper);
        }
    }
}

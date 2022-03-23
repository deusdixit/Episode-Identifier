package gui.models;

import hamming.Similarity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.nio.file.Paths;
import java.util.List;

public class PreviewItem extends ObservableValueBase<ComboBox<PreviewItem.ComboItem>> {

    private ComboBox<PreviewItem.ComboItem> item;

    @Override
    public ComboBox<ComboItem> getValue() {
        return item;
    }

    public static class ComboItem {
        private final Similarity.SimResult sim;
        private final String filename;

        public ComboItem(Similarity.SimResult sim, String filename) {
            this.sim = sim;
            this.filename = filename;
        }

        public Similarity.SimResult getSim() {
            return sim;
        }

        ;

        public String getFilename() {
            return filename;
        }

        ;

        @Override
        public String toString() {
            return (int) (sim.getAccuarcy() * 100) + "% - " + Paths.get(filename).getFileName();
        }
    }

    public PreviewItem() {
        item = null;
    }

    public void setValue(List<ComboItem> sims) {
        item = new ComboBox<>();
        item.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ComboItem>() {
            @Override
            public void changed(ObservableValue<? extends ComboItem> observableValue, ComboItem comboItem, ComboItem t1) {
                item.getStylesheets().clear();
                if (t1.sim.getAccuarcy() < 0.2) {
                    //item.getStylesheets().add(getClass().getResource("/css/ComboBoxItemRed.css").toExternalForm());
                    item.getStylesheets().add(getClass().getResource("/css/ComboBoxItemSelectedRed.css").toExternalForm());
                } else {
                    //item.getStylesheets().add(getClass().getResource("/css/ComboBoxItemGreen.css").toExternalForm());

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
        item.setMaxHeight(25);
        item.setMaxWidth(Double.MAX_VALUE);
        item.setMinSize(25, 25);
        item.setPrefSize(25, 25);
        item.getSelectionModel().selectFirst();
        fireValueChangedEvent();
    }

    public PreviewItem(List<ComboItem> sims) {
        setValue(sims);
    }


    public String getSelectedFilename() {
        return item.getSelectionModel().getSelectedItem().getFilename();
    }
}

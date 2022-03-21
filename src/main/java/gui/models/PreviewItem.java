package gui.models;

import hamming.Similarity;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.control.ComboBox;

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
        item.getItems().addAll(sims);
        item.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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

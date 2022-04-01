package gui.controller;

import io.Item;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class DatabaseTabController {

    private ObservableList<Item> data;

    @FXML
    private TableColumn<Item, String> fileIdColumn;

    @FXML
    private TableColumn<Item, String> imdbColumn;

    @FXML
    private TableColumn<Item, String> seasonColumn;

    @FXML
    private TableColumn<Item, String> episodeColumn;

    @FXML
    private TableColumn<Item, String> parentTitleColumn;

    @FXML
    private TableView osTable;


    public DatabaseTabController() {

    }

    public void clear() {
        data.clear();
    }

    public void add(Item i) {
        data.add(i);
    }

    public void addAll(ArrayList<Item> arr) {
        data.addAll(arr);
    }

    @FXML
    private void initialize() {
        fileIdColumn.setCellValueFactory(itemStringCellDataFeatures -> new SimpleStringProperty(String.valueOf(itemStringCellDataFeatures.getValue().getFileId())));
        imdbColumn.setCellValueFactory(itemStringCellDataFeatures -> new SimpleStringProperty(String.valueOf(itemStringCellDataFeatures.getValue().getImdbId())));
        seasonColumn.setCellValueFactory(item -> {
            return item.getValue().getAttributeWrapper() != null ? new SimpleStringProperty(String.valueOf(item.getValue().getAttributeWrapper().getSeasonNumber())) : new SimpleStringProperty();
        });
        episodeColumn.setCellValueFactory(item -> {
            return item.getValue().getAttributeWrapper() != null ? new SimpleStringProperty(String.valueOf(item.getValue().getAttributeWrapper().getEpisodeNumber())) : new SimpleStringProperty();
        });
        parentTitleColumn.setCellValueFactory(item -> {
            return item.getValue().getAttributeWrapper() != null ? new SimpleStringProperty(String.valueOf(item.getValue().getAttributeWrapper().getParentTitle())) : new SimpleStringProperty();
        });
        data = FXCollections.observableList(new ArrayList<>());
        osTable.setItems(data);
    }

}

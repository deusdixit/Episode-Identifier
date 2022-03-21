package gui.controller;

import io.Item;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;

public class DatabaseTabController {

    private ObservableList<Item> data;

    @FXML
    private TableColumn<Item, String> fileIdColumn;

    @FXML
    private TableColumn<Item, String> imdbColumn;

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
        fileIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> itemStringCellDataFeatures) {
                return new SimpleStringProperty(String.valueOf(itemStringCellDataFeatures.getValue().getFileId()));
            }
        });
        imdbColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> itemStringCellDataFeatures) {
                return new SimpleStringProperty(String.valueOf(itemStringCellDataFeatures.getValue().getImdbId()));
            }
        });
        data = FXCollections.observableList(new ArrayList<>());
        osTable.setItems(data);
    }

}

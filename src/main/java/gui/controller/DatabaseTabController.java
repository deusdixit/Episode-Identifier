package gui.controller;

import io.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
        fileIdColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("fileId"));
        imdbColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("imdbId"));
        data = FXCollections.observableList(new ArrayList<>());
        osTable.setItems(data);
    }

}

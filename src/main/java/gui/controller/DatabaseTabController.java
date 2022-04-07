package gui.controller;

import gui.exceptions.NoOpensubtitlesException;
import id.gasper.opensubtitles.models.features.Episode;
import id.gasper.opensubtitles.models.features.Feature;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import io.AttributesWrapper;
import io.Item;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Database;
import utils.Drawing;
import utils.OsApi;

import java.io.File;
import java.io.IOException;
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
    private TableColumn<Item, String> tmbdColumn;

    @FXML
    private TableColumn<Item, String> parentTitleColumn;

    @FXML
    private TableColumn<Item, String> yearColumn;

    @FXML
    private TableView<Item> osTable;

    private Stage mainStage;


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

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
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
        tmbdColumn.setCellValueFactory(item -> {
            return item.getValue().getAttributeWrapper() != null ? new SimpleStringProperty(String.valueOf(item.getValue().getAttributeWrapper().getTmbdId())) : new SimpleStringProperty();
        });
        yearColumn.setCellValueFactory(item -> {
            return item.getValue().getAttributeWrapper() != null ? new SimpleStringProperty(String.valueOf(item.getValue().getAttributeWrapper().getYear())) : new SimpleStringProperty();
        });
        try {
            data = FXCollections.observableList(Database.getDatabase().get());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        osTable.setItems(data);
        ContextMenu cm = new ContextMenu();
        MenuItem mItem = new MenuItem("Export Timeline");
        MenuItem rItem = new MenuItem("Reload feature details");
        cm.getItems().add(mItem);
        cm.getItems().add(rItem);
        osTable.setContextMenu(cm);
        mItem.setOnAction((event) -> {
            if (osTable.getSelectionModel().getSelectedCells().size() > 0) {
                TablePosition tp = osTable.getSelectionModel().getSelectedCells().get(0);
                Item item = osTable.getItems().get(tp.getRow());
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save timeline");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));
                File file = fileChooser.showSaveDialog(mainStage);
                Drawing.draw(item.getData(), file);
            }
        });
        rItem.setOnAction((event) -> {
            for (Item i : osTable.getItems()) {
                if (i.getAttributeWrapper() != null && i.getAttributeWrapper().getTmbdId() > 0) {
                    continue;
                }
                int imdb = i.getImdbId();
                FeatureQuery fq = new FeatureQuery().setImdbId(imdb);
                try {
                    Feature[] fr = OsApi.getInstance().getFeatures(fq.build());
                    if (fr.length > 0 && fr[0] instanceof Episode) {
                        Episode e = (Episode) fr[0];
                        AttributesWrapper aWrapper = new AttributesWrapper(e.attributes);
                        i.setAttributeWrapper(aWrapper);
                    }
                } catch (NoOpensubtitlesException noe) {
                    noe.getMainController().showOpensubtitles();
                    break;
                } catch (IOException | InterruptedException ie) {
                    System.out.println(ie.getLocalizedMessage());
                }
            }
            osTable.refresh();
        });
    }

}

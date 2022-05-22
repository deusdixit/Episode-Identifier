package gui.components;

import gui.exceptions.NoOpensubtitlesException;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.infos.UserResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utils.OsApi;

import java.io.IOException;
import java.util.Objects;


public class AccountDetails extends HBox {

    private final ImageView imgView;
    private final SimpleStringProperty accountText;

    public AccountDetails() {
        accountText = new SimpleStringProperty();
        imgView = new ImageView();
        imgView.setFitHeight(20);
        imgView.setFitWidth(20);

        Label textLabel = new Label("");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        textLabel.setMaxHeight(Double.MAX_VALUE);

        textLabel.textProperty().bind(accountText);
        HBox.setMargin(imgView, new Insets(0, 10, 0, 20));
        this.getChildren().add(imgView);
        this.getChildren().add(textLabel);

    }

    public void set(LoginResult lr) {
        if (lr.status == 200) {
            imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/green_checkmark.png"))));
            try {
                UserResult ur = OsApi.getInstance().getUserInfo();
                accountText.set("Remaining downloads : " + ur.data.remaining_downloads + "/" + ur.data.allowed_downloads);
            } catch (IOException | InterruptedException e) {
                accountText.set("Allowed downloads : " + lr.user.allowed_downloads);
                e.printStackTrace();
            } catch (NoOpensubtitlesException noe) {
                noe.getMainController().showOpensubtitles();
            }

        } else {
            imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/red_checkmark.png"))));
            accountText.set(lr.status + " Error");
        }
    }


}

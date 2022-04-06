package gui.components;

import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.infos.UserResult;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utils.OsApi;

import java.io.IOException;


public class AccountDetails extends HBox {

    private ImageView imgView;
    private Label textLabel;

    public AccountDetails() {
        imgView = new ImageView();
        imgView.setFitHeight(20);
        imgView.setFitWidth(20);

        textLabel = new Label("");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        textLabel.setMaxHeight(Double.MAX_VALUE);
        this.getChildren().add(imgView);
        this.getChildren().add(textLabel);
    }

    public void set(LoginResult lr) {
        if (lr.status == 200) {
            imgView.setImage(new Image(getClass().getResourceAsStream("/icons/green_checkmark.png")));
            try {
                UserResult ur = OsApi.getInstance().getUserInfo();
                textLabel.setText("Remaining downloads : " + ur.data.remaining_downloads + "/" + ur.data.allowed_downloads);
            } catch (IOException | InterruptedException e) {
                textLabel.setText("Allowed downloads : " + lr.user.allowed_downloads);
                e.printStackTrace();
            }

        } else {
            imgView.setImage(new Image(getClass().getResourceAsStream("/icons/red_checkmark.png")));
            textLabel.setText(lr.status + " Error");
        }
    }


}

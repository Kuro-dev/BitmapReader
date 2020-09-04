package org.kurodev.bmpreader.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.kurodev.bmpreader.logic.image.EncodableImage;
import org.kurodev.bmpreader.logic.util.EasyByteReader;
import org.kurodev.bmpreader.logic.util.EasyByteWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @author kuro
 **/
public class OverlayController implements Initializable {
    private final EncodableImage img = new EncodableImage(500, 500);
    @FXML
    public ToggleButton colourButton;
    @FXML
    private Slider brushSize;
    @FXML
    private Label brushSizeLabel;
    @FXML
    private Canvas image;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    private DrawHandler drawHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setImage(img);
        setupColourButton();
        drawHandler = new DrawHandler(image, img, colourButton);
        image.setOnMouseDragged(drawHandler);
        setupBrushSizeHandler();
    }

    private void setupBrushSizeHandler() {
        brushSize.setMax(10);
        brushSize.setMin(1);
        brushSize.valueProperty().addListener((observable, oldValue, val) -> {
            brushSizeLabel.setText(String.valueOf(val.intValue()));
            drawHandler.setBrushSize(val.intValue());
        });
        brushSize.setValue(2);
    }

    private void setupColourButton() {
        String border = "-fx-border-color: Grey;";
        String black = "-fx-background-color: Black;" + border;
        String white = "-fx-background-color: White;" + border;
        colourButton.selectedProperty().addListener((observable, oldValue, selected) -> {
            String color;
            Paint textFill;
            if (selected) {
                color = black;
                textFill = Paint.valueOf("white");
                colourButton.setText("black");
            } else {
                color = white;
                colourButton.setText("white");
                textFill = Paint.valueOf("black");
            }
            colourButton.setStyle(color);
            colourButton.setTextFill(textFill);
        });
        colourButton.setSelected(true);
    }

    private void setImage(EasyByteReader reader) {
        img.decode(reader);
        setImage(img);
    }

    private void setImage(EncodableImage img) {
        progressBar.setAccessibleText("Test");
        System.out.println("Loading image");
        ImageLoader loader = new ImageLoader(img, image, progressLabel, progressBar);
        this.img.copy(img);
        Thread thread = new Thread(loader, "ImageLoading thread");
        image.setHeight(img.getHeight());
        image.setWidth(img.getWidth());
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
     void menuNew_OnAction() {
        Pattern syntax = Pattern.compile("\\d+\\s*[xX]\\s*\\d+");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create new BitMap");
        dialog.setHeaderText("please enter your dimensions: ");
        dialog.setContentText("Syntax: width x height");
        dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        dialog.getEditor().textProperty().addListener((observable, oldValue, text) ->
                dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(!syntax.matcher(text).matches()));
        Optional<String> res = dialog.showAndWait();
        res.ifPresent(text -> {
            if (syntax.matcher(text).matches()) {
                String[] split = text.replaceAll("\\s", "").split("[xX]");
                int widthIndex = 0, heightIndex = 1;
                int x = Integer.parseInt(split[widthIndex]), y = Integer.parseInt(split[heightIndex]);
                setImage(new EncodableImage(x, y));
                Platform.runLater(() -> Main.getStage().sizeToScene());
            } else {
                System.err.println("Refusing to load\ninvalid input: " + text);
            }
        });
    }

    private FileChooser createChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return chooser;
    }

    @FXML
     void menuSave_OnAction() throws IOException {
        FileChooser chooser = createChooser();
        WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        image.snapshot(new SnapshotParameters(), writableImage);
        img.decode(writableImage);
        chooser.setTitle("Save");
        File p = chooser.showSaveDialog(Main.getStage());
        if (p != null)
            saveToFile(p.toPath());
    }

    @FXML
     void menuOpen_OnAction() throws IOException {
        FileChooser chooser = createChooser();
        chooser.setTitle("Open");
        File p = chooser.showOpenDialog(Main.getStage());
        if (p != null)
            loadFile(p.toPath());
    }

    private void saveToFile(Path p) throws IOException {
        if (!Files.exists(p.getParent())) {
            System.err.println("creating Folder: " + p.getParent());
            Files.createDirectories(p.getParent());
        }
        if (!Files.exists(p)) {
            System.err.println("creating File: " + p);
            Files.createFile(p);
        }
        if (Files.isRegularFile(p)) {
            System.out.println("saving image to " + p);
            OutputStream out = Files.newOutputStream(p);
            EasyByteWriter content = new EasyByteWriter();
            img.encode(content);
            out.write(content.toByteArray());
        }
    }

    private void loadFile(Path p) throws IOException {
        if (Files.isRegularFile(p)) {
            InputStream out = Files.newInputStream(p);
            int a = out.available();
            byte[] bytes = new byte[a];
            int read = out.read(bytes);
            if (read != a) {
                System.err.println("what the fuck? available: " + a + ", read: " + read);
            }
            EasyByteReader reader = new EasyByteReader(bytes);
            setImage(reader);
        }
    }
}

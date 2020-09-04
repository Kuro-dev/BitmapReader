package org.kurodev.bmpreader.gui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import org.kurodev.bmpreader.logic.image.EncodableImage;

import java.util.function.Consumer;

/**
 * @author kuro
 **/
public class ImageLoader implements Runnable, Consumer<Double> {
    private final EncodableImage img;
    private final Canvas image;
    private final Label progressLabel;
    private final ProgressBar progressBar;

    public ImageLoader(EncodableImage img, Canvas image, Label progressLabel, ProgressBar progressBar) {
        this.img = img;
        this.image = image;
        this.progressLabel = progressLabel;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        Image img = this.img.toImage(this);
        image.getGraphicsContext2D().drawImage(img, 0, 0, img.getWidth(), img.getHeight());
    }

    @Override
    public void accept(Double prog) {
        if (prog < 1) {
            Platform.runLater(() -> {
                progressLabel.setText("Loading: " + ((int) (prog * 100)) + "%");
                progressBar.setProgress(prog);
            });
        } else {
            Platform.runLater(() -> {
                progressBar.setProgress(0);
                progressLabel.setText(String.format("width: %d, height: %d", img.getWidth(), img.getHeight()));
                Platform.runLater(() -> Main.getStage().sizeToScene());
            });
        }
    }
}

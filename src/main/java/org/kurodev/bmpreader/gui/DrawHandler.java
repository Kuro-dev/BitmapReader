package org.kurodev.bmpreader.gui;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.kurodev.bmpreader.logic.image.EncodableImage;

/**
 * @author kuro
 **/
public class DrawHandler implements EventHandler<MouseEvent> {
    private final Canvas image;
    private final EncodableImage img;
    private final ToggleButton colourButton;
    private int brushSize = 2;

    public DrawHandler(Canvas image, EncodableImage img, ToggleButton colourButton) {
        this.image = image;
        this.img = img;
        this.colourButton = colourButton;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            if (event.getTarget() == image) {
                int x = (int) event.getSceneX(), y = (int) event.getSceneY();
//                System.out.printf("x:%d, y:%d\n", x, y);
                boolean selected = colourButton.isSelected();
                draw(x, y, selected);
            }
        }
    }

    private void draw(int x, int y, boolean black) {
        if (x < img.getWidth() || y < img.getHeight() || x < 0 || y < 0) {
            img.write(x, y, black);
            Color fill = black ? Color.BLACK : Color.WHITE;
            image.getGraphicsContext2D().setFill(fill);
            image.getGraphicsContext2D().fillOval(x, y, brushSize, brushSize);
        }
    }
}

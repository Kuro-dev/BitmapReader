package org.kurodev.bmpreader.gui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

/**
 * @author kuro
 **/
public class HotkeyHandler implements EventHandler<KeyEvent> {
    private OverlayController controller;

    public HotkeyHandler(OverlayController controller) {

        this.controller = controller;
    }
//TODO i hate this but its quick and it works.
    @Override
    public void handle(KeyEvent event) {
        try {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case S:
                        controller.menuSave_OnAction();
                        break;
                    case N:
                        controller.menuNew_OnAction();
                        break;
                    case O:
                        controller.menuOpen_OnAction();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

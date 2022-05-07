package gui.exceptions;

import gui.controller.MainController;

public class NoOpensubtitlesException extends Exception {

    private final MainController mainController;

    public NoOpensubtitlesException(MainController mc) {
        super();
        mainController = mc;
    }

    public MainController getMainController() {
        return mainController;
    }
}

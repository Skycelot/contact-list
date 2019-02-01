package ru.skycelot;

import ru.skycelot.controller.FrontController;
import ru.skycelot.controller.NetworkListener;

import java.io.IOException;
import java.nio.channels.Selector;

public class Launcher {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        FrontController frontController = new FrontController(selector);
        NetworkListener networkListener = new NetworkListener(selector, frontController);
        networkListener.service();
    }
}

package opticnav.ardd.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class ARDListener implements Runnable {
    private final class Spawner implements Listener.ConnectionSpawner {
        @Override
        public Runnable create(Closeable closeable, InputStream input,
                OutputStream output) {
            // TODO
            return null;
        }
    }

    private Listener listener;
    
    public ARDListener(int port) throws IOException {
        Logger logger = Logger.getLogger("ARDListener");
        this.listener = new Listener(port, new Spawner(), logger);
    }

    @Override
    public void run() {
        this.listener.run();
    }
}
package opticnav.ardd.net;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import opticnav.ardd.ARDListsManager;
import opticnav.ardd.clients.ardclient.ARDChannelsManager;
import opticnav.ardd.protocol.chan.Channel;

public class ARDListener implements Runnable {
    private final class Spawner implements Listener.ConnectionSpawner {
        @Override
        public Callable<Void> create(Channel channel, ExecutorService threadPool) {
            return new ARDChannelsManager(channel, threadPool, ardListsManager);
        }
    }

    private ARDListsManager ardListsManager;
    private Listener listener;
    
    public ARDListener(int port, ARDListsManager ardListsManager)
            throws IOException {
        this.ardListsManager = ardListsManager;
        this.listener = new Listener(this.getClass(), port, new Spawner());
    }

    @Override
    public void run() {
        this.listener.call();
    }
}

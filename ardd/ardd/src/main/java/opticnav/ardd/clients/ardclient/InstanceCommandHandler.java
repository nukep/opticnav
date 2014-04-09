package opticnav.ardd.clients.ardclient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import opticnav.ardd.ARDConnection;
import opticnav.ardd.BlockingValue;
import opticnav.ardd.clients.AnnotatedCommandHandler;
import opticnav.ardd.instance.Entity;
import opticnav.ardd.protocol.GeoCoordFine;
import opticnav.ardd.protocol.PrimitiveReader;
import opticnav.ardd.protocol.PrimitiveWriter;
import opticnav.ardd.protocol.consts.ARDdARDProtocol.Connected.Instance.Commands;

public class InstanceCommandHandler extends AnnotatedCommandHandler {
    private static final XLogger LOG = XLoggerFactory
            .getXLogger(InstanceCommandHandler.class);
    
    private final ARDConnection connection;
    private final BlockingValue<Entity> entity;

    public InstanceCommandHandler(ARDConnection connection, BlockingValue<Entity> entity) {
        super(InstanceCommandHandler.class);
        this.connection = connection;
        this.entity = entity;
    }

    @Override
    public void close() throws IOException {
        try {
            entity.get().close();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }   
    
    @Command(Commands.MOVE)
    public void move(PrimitiveReader in, PrimitiveWriter out) throws Exception {
        final int lng = in.readSInt32();
        final int lat = in.readSInt32();
        final GeoCoordFine geoCoord = new GeoCoordFine(lng, lat);
        
        entity.get().move(geoCoord);
    }
}

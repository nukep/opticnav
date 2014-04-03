package opticnav.ardd.integration.admin;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;

import opticnav.ardd.admin.ARDdAdmin;
import opticnav.ardd.admin.ARDdAdminStartInstanceStatus;
import opticnav.ardd.admin.InstanceDeployment;
import opticnav.ardd.admin.InstanceDeployment.Anchor;
import opticnav.ardd.admin.InstanceDeploymentBuilder;
import opticnav.ardd.broker.admin.ARDdAdminBroker;
import opticnav.ardd.protocol.GeoCoordFine;
import opticnav.ardd.protocol.Protocol;
import opticnav.ardd.protocol.chan.Channel;
import opticnav.ardd.protocol.chan.ChannelUtil;

public class AdminDeployInstanceIntegrationDriver {
    private static List<Anchor> saitAnchors() {
        // hard-coded anchor points for SAIT
        final int[] local = {463,346, 714,409, 937,200};
        final int[] internalGPS = {-41072424,18383378, -41071669,18383259, -41071005,18383656};
        final List<InstanceDeployment.Anchor> anchors = new ArrayList<>(3);
    
        for (int i = 0; i < 3; i++) {
            GeoCoordFine geo = new GeoCoordFine(internalGPS[i * 2 + 0]<<5, internalGPS[i * 2 + 1]<<5);
            Anchor anchor = new Anchor(geo, local[i*2+0], local[i*2+1]);
            anchors.add(anchor);
        }
        return anchors;
    }
    
    public static void main(String[] args) throws Exception {
        final InputStream mapImageInput = AdminDeployInstanceIntegrationDriver.class.getResourceAsStream("/saitcampus.png");
        // In this case, available() returns the resource size
        final int mapImageSize = mapImageInput.available();
        final List<InstanceDeployment.Anchor> mapAnchors = saitAnchors();
        
        final Socket socket = new Socket("localhost", Protocol.DEFAULT_ADMIN_PORT);
        final Channel channel = ChannelUtil.fromSocket(socket);
        
        try (ARDdAdmin broker = new ARDdAdminBroker(channel)) {
            final List<InstanceDeployment.ARDIdentifier> ardList = new ArrayList<>();
            ardList.add(new InstanceDeployment.ARDIdentifier(1, "Bob"));
            ardList.add(new InstanceDeployment.ARDIdentifier(2, "Joe"));

            final InstanceDeployment deployment;
            deployment = new InstanceDeploymentBuilder()
                           .setARDList(ardList)
                           .setMapName("Integration Test Map")
                           .setMapImage(new MimeType("image/png"), mapImageSize, mapImageInput, mapAnchors)
                           .build();
            
            final ARDdAdminStartInstanceStatus status = broker.deployInstance(0, deployment);
            System.out.println("Status: " + status.getStatus());
        }
    }
}

package opticnav.ardd.connections;

import java.io.IOException;

import opticnav.ardd.ARDListsManager;
import opticnav.ardd.protocol.HexCode;
import opticnav.ardd.protocol.PrimitiveReader;
import opticnav.ardd.protocol.PrimitiveWriter;
import opticnav.ardd.protocol.Protocol;

public class AdminClientCommandHandler implements ClientConnection.CommandHandler {
    private ARDListsManager ardListsManager;

    public AdminClientCommandHandler(ARDListsManager ardListsManager) {
        this.ardListsManager = ardListsManager;
    }
    
    @Override
    public void command(int code, PrimitiveReader in, PrimitiveWriter out)
            throws IOException {
        if (code == Protocol.AdminClient.Commands.REGARD.getCode()) {
            byte[] hexCode = in.readFixedBlob(Protocol.CONFCODE_BYTES);
            HexCode confcode = new HexCode(hexCode);
            
            int ard_id;
            ard_id = this.ardListsManager.persistPendingWithConfCode(confcode);

            out.writeUInt31(ard_id);
            out.flush();
        }
    }
}

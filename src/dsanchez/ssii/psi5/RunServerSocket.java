package dsanchez.ssii.psi5;
import java.io.IOException;

/**
 * Created by Daniel S�nchez on 11/05/2016.
 */
public class RunServerSocket {

    public static void main (String[] args) throws IOException {

        //Run the server
        AndroidServerSocket androidServerSocket = new AndroidServerSocket();
        androidServerSocket.runServer();

    }
}

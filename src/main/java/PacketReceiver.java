import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

public class PacketReceiver implements Runnable {
    static final int SECONDS_TO_DISCONNECT = 5;
    static final int BUFSIZE = 256;
    
    private final MulticastSocket socket;
    private final ConcurrentHashMap<SimpleEntry<InetAddress, String>, Integer> connections;
    
    public PacketReceiver(MulticastSocket socket, ConcurrentHashMap<SimpleEntry<InetAddress, String>, Integer> connections) {
        this.socket = socket;
        this.connections = connections;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFSIZE];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, BUFSIZE);
        while(true) {
            try {
                socket.receive(receivedPacket);
                String hostName = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                SimpleEntry<InetAddress, String> entry = new SimpleEntry<>(receivedPacket.getAddress(), hostName);
                if (!connections.containsKey(entry)) {
                    connections.put(entry, SECONDS_TO_DISCONNECT);
                    connections.forEach((key, value) -> System.out.println(key.getValue() + key.getKey()));
                    System.out.println();
                }
                else {
                    connections.put(entry, SECONDS_TO_DISCONNECT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

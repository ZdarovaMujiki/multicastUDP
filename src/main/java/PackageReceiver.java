import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.AbstractMap;
import java.util.HashMap;

public class PackageReceiver {
    private static final int SECONDS_TO_DISCONNECT = 5;
    private static final int BUFSIZE = 256;
    private static final int ZERO_SECONDS = 0;
    private static final HashMap<AbstractMap.SimpleEntry<InetAddress, String>, Integer> connections = new HashMap<>();

    private final MulticastSocket socket;

    private static void updateConnections() {
        connections.forEach((pairOfAddressAndHostname, secondsToDisconnect) -> connections.put(pairOfAddressAndHostname, secondsToDisconnect - 1));
        if (connections.containsValue(ZERO_SECONDS)) {
            connections.entrySet().removeIf(connection -> ZERO_SECONDS == connection.getValue());
            connections.forEach((pairOfAddressAndHostname, secondsToDisconnect) -> System.out.println(pairOfAddressAndHostname.getValue() + pairOfAddressAndHostname.getKey()));
            System.out.println();
        } else {
            connections.entrySet().removeIf(connection -> ZERO_SECONDS == connection.getValue());
        }
    }
    public PackageReceiver(MulticastSocket socket) {
        this.socket = socket;
    }
    public void start() throws IOException {
        byte[] buffer = new byte[BUFSIZE];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, BUFSIZE);
        String hostName = ManagementFactory.getRuntimeMXBean().getName();

        while(true) {
            socket.receive(receivedPacket);
            String senderHostName = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            AbstractMap.SimpleEntry<InetAddress, String> entry = new AbstractMap.SimpleEntry<>(receivedPacket.getAddress(), senderHostName);
            if (!connections.containsKey(entry)) {
                connections.put(entry, SECONDS_TO_DISCONNECT);
                connections.forEach((pairOfAddressAndHostname, secondsToDisconnect) ->
                        System.out.println(pairOfAddressAndHostname.getValue() + pairOfAddressAndHostname.getKey()));
                System.out.println();
            }
            else {
                connections.put(entry, SECONDS_TO_DISCONNECT);
            }
            if (senderHostName.equals(hostName)) {
                updateConnections();
            }
        }
    }
}

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

public class Main {
    private static final int SECONDS_TO_DISCONNECT = 5;
    private static final int BUFSIZE = 256;

    private static final HashMap<SimpleEntry<InetAddress, String>, Integer> connections = new HashMap<>();

    private static void updateConnections() {
        connections.forEach((key, value) -> connections.put(key, value - 1));
        if (connections.containsValue(0)) {
            connections.entrySet().removeIf(entry -> entry.getValue() == 0);
            connections.forEach((key, value) -> System.out.println(key.getValue() + key.getKey()));
            System.out.println();
        } else {
            connections.entrySet().removeIf(entry -> entry.getValue() == 0);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Multicast group ip and port must be specified");
            return;
        }
        MulticastSocket socket = null;
        try {
            String groupIP = args[0];
            int groupPort = Integer.parseInt(args[1]);
            socket = new MulticastSocket(groupPort);
            InetAddress group = InetAddress.getByName(groupIP);
            socket.joinGroup(group);

            String hostName = ManagementFactory.getRuntimeMXBean().getName();
            byte[] message = hostName.getBytes();
            DatagramPacket packet = new DatagramPacket(message, message.length, group, groupPort);
            Thread senderThread = new Thread(new PacketSender(socket, packet));
            senderThread.start();

            byte[] buffer = new byte[BUFSIZE];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, BUFSIZE);

            while(true) {
                socket.receive(receivedPacket);
                String senderHostName = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                SimpleEntry<InetAddress, String> entry = new SimpleEntry<>(receivedPacket.getAddress(), senderHostName);
                if (!connections.containsKey(entry)) {
                    connections.put(entry, SECONDS_TO_DISCONNECT);
                    connections.forEach((pairOfAddressAndHostname, secondsToDisconnect) -> System.out.println(pairOfAddressAndHostname.getValue() + pairOfAddressAndHostname.getKey()));
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
        catch (IllegalArgumentException | IOException e) {
            System.out.println("bad ip address or port out of range");
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}

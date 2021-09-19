import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class Main {
    private static final int SLEEP_DURATION = 1000;

    private static final ConcurrentHashMap<SimpleEntry<InetAddress, String>, Integer> connections = new ConcurrentHashMap<>();

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

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Multicast group ip and port must be specified");
            return;
        }
        String groupIP = args[0];
        int groupPort = Integer.parseInt(args[1]);
        InetAddress group = InetAddress.getByName(groupIP);
        MulticastSocket socket = new MulticastSocket(groupPort);
        socket.joinGroup(group);

        byte[] message = ManagementFactory.getRuntimeMXBean().getName().getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, group, groupPort);
        Thread senderThread = new Thread(new PacketSender(socket, packet));
        senderThread.start();

        Thread receiverThread = new Thread(new PacketReceiver(socket, connections));
        receiverThread.start();

        while(true) {
            sleep(SLEEP_DURATION);
            updateConnections();
        }
    }
}

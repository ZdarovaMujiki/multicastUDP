import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Multicast group ip and port must be specified");
            return;
        }
        String groupIP = args[0];
        InetAddress group;
        int groupPort;

        String hostName = ManagementFactory.getRuntimeMXBean().getName();
        byte[] message = hostName.getBytes();

        try {
            groupPort = Integer.parseInt(args[1]);
            group = InetAddress.getByName(groupIP);
            try (MulticastSocket socket = new MulticastSocket(groupPort)) {
                socket.joinGroup(group);
                DatagramPacket packet = new DatagramPacket(message, message.length, group, groupPort);

                Thread senderThread = new Thread(new PacketSender(socket, packet));
                senderThread.start();

                PackageReceiver packageReceiver = new PackageReceiver(socket);
                packageReceiver.start();
            }
            catch (IOException e) {
                System.out.println("bad ip address");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("bad port");
        }
    }
}

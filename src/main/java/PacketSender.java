import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class PacketSender implements Runnable {
    private static final int SLEEP_DURATION = 1000;

    private final MulticastSocket socket;
    private final DatagramPacket packet;

    public PacketSender(MulticastSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run() {
        while(true) {
            try {
                socket.send(packet);
                Thread.sleep(SLEEP_DURATION);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

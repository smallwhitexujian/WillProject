package socket.lib.socket;

public class SocketInfo {
    public String host;
    public int port;
    public int timeout;

    @Override
    public String toString() {
        return "SocketInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", timeout=" + timeout +
                '}';
    }
}
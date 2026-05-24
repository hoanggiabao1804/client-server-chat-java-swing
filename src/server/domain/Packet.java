package domain;

public class Packet {
    private String ipAddress;
    private int port;
    private Object data;
    private String type;
    private String command;

    public Packet() {
    }

    public Packet(String ipAddress, int port, Object data, String type, String command) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.data = data;
        this.type = type;
        this.command = command;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}

package Server;

import java.io.PrintWriter;
import java.net.InetAddress;

public class Player {
    private final PrintWriter out;
    private String id = null;
    private InetAddress address;
    private int UDPPort = -1;
    private Game game = null;
    private boolean hasSentSTART = false;
    private boolean finishedPlaying = false;
    private int score = 0;
    private int row;
    private int col;

    public Player(PrintWriter out, InetAddress inetAddress) {
        this.out = out;
        this.address = inetAddress;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUDPPort() {
        return UDPPort;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean hasSentSTART() {
        return hasSentSTART;
    }

    public void pressSTART() {
        this.hasSentSTART = true;
        this.game.removeFromPlayersWhoDidntSendSTART(this);
    }

    public int getPort(int port) {
        return UDPPort;
    }

    public void setPort(int port) {
        this.UDPPort = port;
    }

    public void sendWELCO(InetAddress ipMulticast, int portMulticast) {
        // send [WELCO m h w f ip port***]
        short h = this.game.getLabyrinthWidth();
        short w = this.game.getLabyrinthHeight();
        byte h0, h1, w0, w1, f, m;
        m = this.game.getId();
        f = this.game.getNbGhosts();
        h0 = (byte) h; // lowest weight byte
        h1 = (byte) (h >> 8); // strongest weight byte
        w0 = (byte) w; // lowest weight byte
        w1 = (byte) (w >> 8); // strongest weight byte
        String ipMulticastStr = ipMulticast.getHostAddress();
        String ip = ipMulticastStr + "#".repeat(15 - ipMulticastStr.length());
        String mess = String.format("WELCO %c %c%c %c%c %c %s %04d***", Byte.toUnsignedInt(m), Byte.toUnsignedInt(h0),
                Byte.toUnsignedInt(h1), Byte.toUnsignedInt(w0), Byte.toUnsignedInt(w1), Byte.toUnsignedInt(f), ip,
                portMulticast);
        this.out.printf(mess);
        System.out.println(mess);
    }

    public void sendPOSIT() {
        // send [POSIT id x y***]
        String mess = String.format("POSIT %s %03d %03d***", this.id, this.row, this.col);
        this.out.printf(mess);
        System.out.println(mess);
    }

    public void sendPLAYR(PrintWriter dest) {
        dest.printf("PLAYR %s***", this.id);
    }

    public void sendGPLYR(PrintWriter dest) {
        dest.printf("GPLYR %s %04d %04d %04d***", id, row, col, score);
    }

    public boolean unregister() {
        if (this.game != null) {
            this.game.removePlayer(this);
            ServerImpl.INSTANCE.removePlayer(id);
            this.game = null;
            this.hasSentSTART = false;
            this.id = null;
            this.UDPPort = -1;
            return true;
        }
        return false;
    }

    public int getScore() {
        return this.score;
    }

    public void addPoints(int points) {
        this.score += points;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean hasFinishedPlaying() {
        return finishedPlaying;
    }

    public void finishPlaying() {
        this.finishedPlaying = true;
    }

    public InetAddress getAddress() {
        return address;
    }

    public boolean moveUP(int d) throws Exception {
        return game.getLabyrinth().movePlayerUP(this, d);
    }

    public boolean moveDOWN(int d) throws Exception {
        return game.getLabyrinth().movePlayerDOWN(this, d);
    }

    public boolean moveLEFT(int d) throws Exception {
        return game.getLabyrinth().movePlayerLEFT(this, d);
    }

    public boolean moveRIGHT(int d) throws Exception {
        return game.getLabyrinth().movePlayerRIGHT(this, d);
    }
}

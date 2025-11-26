import org.eclipse.jetty.websocket.api.*;

// Note: The @ServerEndpoint annotation and standard javax imports are REMOVED.
// The class now implements the Jetty API's WebSocketListener.
public class WebSocketServer implements WebSocketListener {

    private Session session;

    // Called when the connection is established
    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        System.out.println("Client connected: " + session.getRemoteAddress().getHostName());

        // Optional: Send a welcome message
        // try {
        // session.getRemote().sendString("Server connected successfully!");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    // Called when the connection receives a text message
    @Override
    public void onWebSocketText(String message) {
        System.out.println("Received from client: " + message);

        // Echo back the message
        if (session.isOpen()) {
            try {
                session.getRemote().sendString("Server received: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Called when the connection closes
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println("Connection closed: " + reason);
        this.session = null;
    }

    // Called if there is an error
    @Override
    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error: " + cause.getMessage());
    }

    // Note: This method is required by the interface but often unused
    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        // Handle binary data if needed
    }
}
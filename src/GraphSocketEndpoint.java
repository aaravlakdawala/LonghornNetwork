// File: GraphSocketEndpoint.java

import org.eclipse.jetty.websocket.api.*;

// The class must implement the Jetty API's WebSocketListener interface.
// If you are using this file as your WebSocket handler, ensure Main.java points to it.
public class GraphSocketEndpoint implements WebSocketListener {

    private Session session;

    // Called when the connection is established
    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        System.out.println("GraphSocketEndpoint connected: " + session.getRemoteAddress().getHostName());
        // Optional: Send initial handshake message
        try {
            session.getRemote().sendString("Server connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called when the connection receives a text message (OnMessage equivalent)
    @Override
    public void onWebSocketText(String message) {
        System.out.println("GraphSocketEndpoint received: " + message);

        // Echo back the message
        if (session.isOpen()) {
            try {
                session.getRemote().sendString("GraphEndpoint received: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Called when the connection closes (OnClose equivalent)
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.println("GraphSocketEndpoint closed: " + reason);
        this.session = null;
    }

    // Called if there is an error
    @Override
    public void onWebSocketError(Throwable cause) {
        System.err.println("GraphSocketEndpoint Error: " + cause.getMessage());
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        // Not used for text messages
    }
}
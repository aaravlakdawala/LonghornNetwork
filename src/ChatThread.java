import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Runnable task that simulates a chat/message exchange between two students.
 * Intended for testing concurrent messaging semantics.
 */
public class ChatThread implements Runnable {
    private UniversityStudent sender;
    private UniversityStudent receiver;
    private String message;

    /**
     * Construct a chat task between two students with a message payload.
     *
     * @param sender   the student sending the message
     * @param receiver the student receiving the message
     * @param message  the message content to send
     */
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    /**
     * Execute the chat behavior. This method is invoked when the runnable
     * is executed by a thread or executor service.
     */
    @Override
    public void run() {
        if (sender == null || receiver == null || message == null)
            return;

        // Thread-safe message delivery
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String fullMessage = "[" + timestamp + "] " + sender.getName() + ": " + message;

        // Send to receiver's chat history
        List<String> receiverChat = receiver.getChatHistory();
        synchronized (receiverChat) {
            receiverChat.add(fullMessage);
        }

        System.out.println("[" + Thread.currentThread().getName() + "] " +
                sender.getName() + " -> " + receiver.getName() + ": " + message);
    }
}

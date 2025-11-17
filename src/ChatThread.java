/**
 * Runnable task that simulates a chat/message exchange between two students.
 * Intended for testing concurrent messaging semantics.
 */
public class ChatThread implements Runnable {
    /**
     * Construct a chat task between two students with a message payload.
     *
     * @param sender   the student sending the message
     * @param receiver the student receiving the message
     * @param message  the message content to send
     */
    public ChatThread(UniversityStudent sender, UniversityStudent receiver, String message) {
        // Constructor
    }

    /**
     * Execute the chat behavior. This method is invoked when the runnable
     * is executed by a thread or executor service.
     */
    @Override
    public void run() {
        // Method signature only
    }
}

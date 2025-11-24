import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * Runnable task that simulates sending a friend request from one student to
 * another. Implementations may use synchronization primitives to emulate
 * concurrent request handling.
 */
public class FriendRequestThread implements Runnable {
    /**
     * Create a friend request task.
     *
     * @param sender   the student sending the friend request
     * @param receiver the student receiving the friend request
     */

    UniversityStudent sender;
    UniversityStudent receiver;
    private final Lock lock = new ReentrantLock();
    private final Set<UniversityStudent> friendsSet = new HashSet<>();

    public FriendRequestThread(UniversityStudent sender, UniversityStudent receiver) {
        // Constructor
        this.sender = sender;
        this.receiver = receiver;
    }

    public void sendFriendRequest() {
        lock.lock();
        try {
            // Thread-safe friend request
            if (sender == null || receiver == null)
                return;

            // Add receiver to sender's friend list
            List<UniversityStudent> senderFriends = sender.getFriendsList();
            synchronized (senderFriends) {
                if (!senderFriends.contains(receiver)) {
                    senderFriends.add(receiver);
                    System.out.println("[" + Thread.currentThread().getName() + "] " +
                            sender.getName() + " sent a friend request to " + receiver.getName());
                } else {
                    System.out.println("[" + Thread.currentThread().getName() + "] " +
                            sender.getName() + " is already friends with " + receiver.getName());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public Set<UniversityStudent> getFriends() {
        return friendsSet;
    }

    /**
     * Execute the friend request logic. This method is invoked when the
     * runnable is executed by a thread or executor service.
     */
    @Override
    public void run() {
        sendFriendRequest();
    }
}

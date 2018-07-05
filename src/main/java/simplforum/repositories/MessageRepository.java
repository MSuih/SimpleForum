package simplforum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import simplforum.models.Message;
import simplforum.models.MessageThread;

/** Repository for messages.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    /** Retrieves the position of the message in a thread. For example, if this mehod
     * returns 15 then the requested message is 15th in it's thread. Positions for
     * ID's that do not exist in the requested thread are undefined.
     * @param messageId ID of the message.
     * @param thread Thread which contains the message.
     * @return Position of the message.
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.id <= ?1 AND m.thread = ?2")
    long findReplyPlacementInThread(long messageId, MessageThread thread);

    /** Retrieves a page of messages from a thread. The mesages are sorted and paged by
     * the time they were posted by.
     * @param pageable Object representing the page which messages are retrieved from.
     * @param thread Thread from which a message is retrieved from.
     * @return A page of messages.
     */
    Page<Message> findByThreadOrderByTimePostedAsc(Pageable pageable, MessageThread thread);
}

package simplforum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import simplforum.models.MessageThread;
import simplforum.models.Topic;

/** Repository for messages.
 */
public interface ThreadRepository extends JpaRepository<MessageThread, Long> {
    /** Retrieves a page of threads from a topic. The threads are paged and sorted by last update
     * time and stickied messages are always listed before non-stickied ones.
     * @param pageable Object representing the page which threads are retrieved from.
     * @param topic The topic which threads are requested from.
     * @return A page of threads.
     */
    @Query("SELECT t FROM MessageThread t WHERE topic = ?1 ORDER BY t.stickied DESC, t.lastUpdate DESC")
    Page<MessageThread> findAllSortedByStickyAndDate(Pageable pageable, Topic topic);
}

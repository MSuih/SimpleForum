package simplforum.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import simplforum.exceptions.IdNotFoundException;
import simplforum.models.Category;
import simplforum.models.MessageThread;
import simplforum.models.Topic;
import simplforum.repositories.CategoryRepository;
import simplforum.repositories.MessageRepository;
import simplforum.repositories.ThreadRepository;
import simplforum.repositories.TopicRepository;

/** Controller for requests related to topics.
 */
@Controller
@RequestMapping("/topic")
public class TopicController {
    @Value("${simplforum.threads-per-topic}")
    private int threadsPerTopic;

    @Autowired
    private TopicRepository topicRepo;
    @Autowired
    private ThreadRepository threadRepo;
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private CategoryRepository cateRepo;

    /** Redirects the request to first page of the topic.
     * @param id ID of the topic.
     * @return Redirect to topic's first page.
     */
    @RequestMapping("/{id}")
    public String getTopic(@PathVariable long id) {
        return "redirect:/topic/" + id + "/page/1";
    }

    /** Mapping for topic and it's pages.
     * @param model Model associated with this request.
     * @param id ID of the topic being retrieved.
     * @param page Requested page of the topic.
     * @return Name of the template for a topic.
     */
    @RequestMapping("/{id}/page/{page}")
    public String getTopic(Model model, @PathVariable long id, @PathVariable int page) {
        Topic t = topicRepo.findById(id).orElseThrow(IdNotFoundException::new);
        model.addAttribute("topic", t);
        PageRequest pagereq = PageRequest.of(Math.max(0, page - 1), threadsPerTopic);
        model.addAttribute("threads", threadRepo.findAllSortedByStickyAndDate(pagereq, t));
        model.addAttribute("threadsPerPage", threadsPerTopic);
        return "topic";
    }

    /** Adds a new topic to the database.
     * @param category ID of the category which this topic is being added to.
     * @param name Name of the category.
     * @param description A description text for the category.
     * @return Redirect back to the management interface.
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String addTopicToCategory(@RequestParam long category,
            @RequestParam String name, @RequestParam String description) {
        Category cate = cateRepo.findById(category).orElseThrow(IdNotFoundException::new);
        Topic topic = new Topic();
        topic.setCategory(cate);
        topic.setName(name);
        topic.setDescription(description);
        cate.addTopic(topic);
        topicRepo.save(topic);
        cateRepo.save(cate);
        return "redirect:/manage";
    }

    /** Returns threads for a certain page of a topic.
     * @param id The topic which threads are requested from
     * @param page Number for the requested page.
     * @return List of messages.
     */
    @RequestMapping("/{id}/threadsfor/{page}")
    @ResponseBody
    public List<MessageThread> getThreadsForPage(@PathVariable long id, @PathVariable int page) {
        Topic t = topicRepo.findById(id).orElseThrow(IdNotFoundException::new);
        PageRequest pagereq = PageRequest.of(Math.max(0, page - 1), threadsPerTopic);
        return threadRepo.findAllSortedByStickyAndDate(pagereq, t).getContent();
    }

    /** Modify a certain topic.
     * @param id ID of the topic which is being modified.
     * @param name New name for the topic.
     * @param description New description for a topic.
     * @return Redirect back to the management interface.
     */
    @RequestMapping(value="/{id}", method=RequestMethod.PATCH)
    public String modifyTopic(@PathVariable long id, @RequestParam String name,
            @RequestParam String description) {
        Topic t = topicRepo.findById(id).orElseThrow(IdNotFoundException::new);
        t.setName(name);
        t.setDescription(description);
        topicRepo.save(t);
        return "redirect:/manage";
    }

    /** Deletes a topic from the database.
     * @param id ID of the topic which is being deleted.
     * @return 200 OK if the deletion was successful.
     */
    @Transactional
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ResponseEntity deleteTopic(@PathVariable long id) {
        Topic t = topicRepo.findById(id).orElseThrow(IdNotFoundException::new);
        t.getThreads().forEach(thread -> {
            messageRepo.deleteAll(thread.getMessages());
            threadRepo.delete(thread);
        });
        topicRepo.delete(t);
        return ResponseEntity.ok(t);
    }
}

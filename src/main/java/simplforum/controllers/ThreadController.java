package simplforum.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import simplforum.exceptions.IdNotFoundException;
import simplforum.exceptions.NoLoginException;
import simplforum.exceptions.NotAllowedException;
import simplforum.models.Account;
import simplforum.models.Message;
import simplforum.models.MessageThread;
import simplforum.models.Topic;
import simplforum.repositories.MessageRepository;
import simplforum.repositories.ThreadRepository;
import simplforum.repositories.TopicRepository;
import simplforum.security.UserAccount;
import simplforum.utils.StringProcessor;

/** Controller for requests related to threads.
 */
@Controller
@RequestMapping("/thread")
public class ThreadController {
    @Value("${simplforum.messages-per-thread}")
    private int messagesPerThread;
    @Value("${simplforum.baseURL}")
    private String baseLocation;

    @Autowired
    private ThreadRepository threadRepo;
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private TopicRepository topicRepo;

    /** Mapping for a thread that redirects requests for first page of it.
     * @param id ID of the thread.
     * @return Redirect to first page of the thread.
     */
    @RequestMapping(value = "/{id}", method= RequestMethod.GET)
    public String getThread(@PathVariable long id) {
        return "redirect:/thread/" + id + "/page/1";
    }

    /** Mapping for a page of a thread.
     * @param model Model associated with this request.
     * @param id ID of the thread.
     * @param page Page that should be displayed.
     * @return Name of the template used for threads.
     */
    @RequestMapping("/{id}/page/{page}")
    public String getThread(Model model, @PathVariable long id, @PathVariable int page) {
        MessageThread thread = threadRepo.findById(id).orElseThrow(IdNotFoundException::new);
        model.addAttribute("thread", thread);
        PageRequest pageable = PageRequest.of(Math.max(0, page - 1), messagesPerThread);
        Page p = messageRepo.findByThreadOrderByTimePostedAsc(pageable, thread);
        model.addAttribute("messages", messageRepo.findByThreadOrderByTimePostedAsc(pageable, thread));
        model.addAttribute("messagesPerPage", messagesPerThread);
        return "thread";
    }

    /** Mapping for retrieving messages from a thread.
     * @param id ID of the thread.
     * @param page Page of the thread which messages are retrieved from.
     * @return List of messages.
     */
    @RequestMapping("/{id}/messagesfor/{page}")
    @ResponseBody
    public List<Message> getMessagesForPage(@PathVariable long id, @PathVariable int page) {
        MessageThread thread = threadRepo.findById(id).orElseThrow(IdNotFoundException::new);
        PageRequest pageable = PageRequest.of(Math.max(0, page - 1), messagesPerThread);
        return messageRepo.findByThreadOrderByTimePostedAsc(pageable, thread).getContent();
    }

    /** Creates a new thread.
     * @param title Title of the thread.
     * @param content Body of the first message to the thread.
     * @param topicId ID of the topic which this thread is created to.
     * @return Redirect to the newly created thread.
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String createThread(@RequestParam String title,
            @RequestParam String content, @RequestParam long topicId) {
        Topic topic = topicRepo.findById(topicId).orElseThrow(IdNotFoundException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account;
        if (auth != null && auth.getPrincipal() instanceof UserAccount) {
            account = ((UserAccount) auth.getPrincipal()).getAccount();
        } else {
            throw new NoLoginException();
        }
        Message message = new Message();
        content = StringProcessor.makeHtmlSafe(content);
        content = StringProcessor.processLinebreaks(content, false);
        content = StringProcessor.processFormattingTags(content, baseLocation + "redirect/");
        message.setContent(content);
        message.setAccount(account);
        MessageThread thread = new MessageThread();
        title = StringProcessor.makeHtmlSafe(title);
        title = StringProcessor.processLinebreaks(title, true);
        thread.setTitle(title);
        thread.setAccount(account);

        thread.addMessage(message);
        message.setThread(thread);
        topic.addThread(thread);
        thread.setTopic(topic);

        threadRepo.save(thread);
        messageRepo.save(message);
        topicRepo.save(topic);
        return "redirect:/thread/" + thread.getId();
    }

    /** Modifies the title of a thread.
     * @param id ID of the thread which is being modified.
     * @param title The new title.
     * @return 200 OK if the request was successful.
     */
    @RequestMapping(value="/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity editTitle(@PathVariable long id, @RequestParam String title) {
        MessageThread thread = threadRepo.findById(id).orElseThrow(IdNotFoundException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserAccount))
            throw new NoLoginException();
        UserAccount account = (UserAccount) auth.getPrincipal();
        if (!account.getAccount().equals(thread.getAccount()) && !auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("MOD")))
            throw new NotAllowedException();
        title = StringProcessor.makeHtmlSafe(title);
        title = StringProcessor.processLinebreaks(title, true);
        thread.setTitle(title);
        threadRepo.save(thread);
        return ResponseEntity.ok(thread);
    }

    /** Deletes a thread from database. Registered users are allowed to delete threads they have
     * created and moderators are allowed to remove any thread.
     * @param id ID of the thread to be deleted.
     * @return 200 OK if the deletion was successful.
     */
    @Transactional
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteThread(@PathVariable long id){
        MessageThread thread = threadRepo.findById(id).orElseThrow(IdNotFoundException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserAccount))
            throw new NoLoginException();
        UserAccount account = (UserAccount) auth.getPrincipal();
        if (!account.getAccount().equals(thread.getAccount()) && !auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("MOD")))
            throw new NotAllowedException();
        List<Message> messages = thread.getMessages();
        messageRepo.deleteAll(messages);
        threadRepo.delete(thread);
        return ResponseEntity.ok(thread);
    }
}

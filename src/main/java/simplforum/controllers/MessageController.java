package simplforum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import simplforum.repositories.MessageRepository;
import simplforum.repositories.ThreadRepository;
import simplforum.security.UserAccount;
import simplforum.utils.StringProcessor;

/** Controller for requests related to messages.
 */
@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private ThreadRepository threadRepo;
    @Autowired
    private MessageRepository messageRepo;
    @Value("${simplforum.baseURL}")
    private String baseLocation;

    /** Adds a new message to a thread.
     * @param content The message content for this request.
     * @param threadId ID of the thread that this message is being added to.
     * @return Redirect to the newly created message.
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String addMessageToThread(@RequestParam String content, @RequestParam long threadId) {
        MessageThread thread = threadRepo.findById(threadId).orElseThrow(IdNotFoundException::new);
        Message message = new Message();
        content = StringProcessor.makeHtmlSafe(content);
        content = StringProcessor.processLinebreaks(content, false);
        content = StringProcessor.processFormattingTags(content, baseLocation + "redirect/");
        message.setContent(content);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserAccount) {
            Account account = ((UserAccount) auth.getPrincipal()).getAccount();
            message.setAccount(account);
        }
        message.setThread(thread);
        thread.addMessage(message);

        message = messageRepo.save(message);
        threadRepo.save(thread);
        return "redirect:/redirect/" + message.getId();
    }

    /** Edits a message body.
     * @param id ID of the message to be edited.
     * @param content New message body.
     * @return 200 OK if the edit was successful.
     */
    @RequestMapping(value="/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity editMessage(@PathVariable long id, @RequestParam String content) {
        Message message = messageRepo.findById(id).orElseThrow(IdNotFoundException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserAccount))
            throw new NoLoginException();
        UserAccount account = (UserAccount) auth.getPrincipal();
        if (!account.getAccount().equals(message.getAccount()) && !auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("MOD")))
            throw new NotAllowedException();
        content = StringProcessor.makeHtmlSafe(content);
        content = StringProcessor.processLinebreaks(content, false);
        content = StringProcessor.processFormattingTags(content, baseLocation + "redirect/");
        message.setContent(content);
        messageRepo.save(message);
        return ResponseEntity.ok(message);
    }

    /** Deletes a message from the database. Registered users are allowed to delete their own
     * messages and moderators are allowed to delete any message.
     * @param id ID of the message to be deleted.
     * @return 200 OK if the delete was successful.
     */
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteMessage(@PathVariable long id) {
        Message message = messageRepo.findById(id).orElseThrow(IdNotFoundException::new);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserAccount))
            throw new NoLoginException();
        UserAccount account = (UserAccount) auth.getPrincipal();
        if (!account.getAccount().equals(message.getAccount()) && !auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("MOD")))
            throw new NotAllowedException();
        messageRepo.delete(message);
        return ResponseEntity.ok(message);
    }
}

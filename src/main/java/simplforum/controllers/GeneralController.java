package simplforum.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import simplforum.exceptions.IdNotFoundException;
import simplforum.exceptions.UserNotFoundException;
import simplforum.models.Account;
import simplforum.models.Message;
import simplforum.models.MessageThread;
import simplforum.repositories.AccountRepository;
import simplforum.repositories.CategoryRepository;
import simplforum.repositories.MessageRepository;
import simplforum.security.AccountTypeUtility;

/** Controller for requests that don't fit into the other controllers.
 */
@Controller
public class GeneralController {
    @Value("${simplforum.baseURL}")
    private String baseURL;
    @Value("${simplforum.messages-per-thread}")
    private int messagesPerThread;

    @Autowired
    private CategoryRepository cateRepo;
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private AccountRepository accoRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Mapping for the front page of this application.
     * @param model Model associated with this request.
     * @return Name of template for front page.
     */
    @RequestMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("categories", cateRepo.findAll());
        return "index";
    }

    /** Redirects message ID to the thread and page which it's saved to.
     * @param id The ID of the message.
     * @return Redirect to the thread and page.
     */
    @RequestMapping("/redirect/{id}")
    public String redirectToMessage(@PathVariable long id) {
        Message message = messageRepo.findById(id).orElseThrow(IdNotFoundException::new);
        MessageThread thread = message.getThread();
        long page = (messageRepo.findReplyPlacementInThread(id, thread) + 1) / messagesPerThread;
        return "redirect:/thread/" + thread.getId() + "/page/" + page + "#msg" + id;
    }

    /** Mapping fo the signup form.
     * @return Template for signup page.
     */
    @RequestMapping("/signup")
    public String getSignupForm() {
        return "signup";
    }

    /** Logs the authenticated user out from this service.
     * @param request The current request object.
     * @param response The response object returned to the user.
     * @return Redirect to a success message.
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
            new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/login?logout";
    }

    /** Creates a new user account to the service.
     * @param username Username for the new account.
     * @param password Password for the new account.
     * @return Redirect to the login file with success message.
     */
    @Transactional
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String createAnAccount(@RequestParam String username, @RequestParam String password) {
        Account acc = new Account();
        acc.setName(username);
        acc.setPassword(passwordEncoder.encode(password));
        accoRepo.save(acc);
        return "redirect:/login?created";
    }

    /** Retrieve an user account for a name.
     * @param name Name of the account to retrieve.
     * @return User account object.
     */
    @RequestMapping("/user/findbyname/{name}")
    @ResponseBody
    public Account getUserByName(@PathVariable String name) {
        return accoRepo.findByName(name).orElseThrow(UserNotFoundException::new);
    }

    /** Modifies a user account.
     * @param id ID of the account to be modified.
     * @param role New role for the modified account.
     * @param disabled Whether this account should be disabled or enabled.
     * @return 200 OK if th request was successful.
     */
    @RequestMapping(value="/user/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity modifyUser(@PathVariable long id, @RequestParam int role, @RequestParam boolean disabled) {
        Account acc = accoRepo.findById(id).orElseThrow(IdNotFoundException::new);
        acc.setType((short) role);
        acc.setDisabled(disabled);
        accoRepo.save(acc);
        return ResponseEntity.ok(acc);
    }

    /** Mapping for forum management interface.
     * @param model Model associated with this request.
     * @return Name of the template for management interface.
     */
    @RequestMapping("/manage")
    public String manageForums(Model model) {
        model.addAttribute("roles", AccountTypeUtility.getPossibleTypes());
        model.addAttribute("categories", cateRepo.findAll());
        model.addAttribute("baseurl", baseURL);
        return "manage";
    }
}

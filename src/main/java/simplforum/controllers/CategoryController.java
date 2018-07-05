package simplforum.controllers;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import simplforum.exceptions.IdNotFoundException;
import simplforum.models.Category;
import simplforum.repositories.CategoryRepository;

/** Controller for requests related to categories.
 */
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository cateRepo;

    /** Returns a category and the topics assigned to it.
     * @param model Model for current request.
     * @param id The category ID.
     * @return The template used to show category.
     */
    @RequestMapping("/{id}")
    public String getCategory(Model model, @PathVariable long id) {
        model.addAttribute("categories", Collections.singleton(cateRepo.findById(id).orElseThrow(IdNotFoundException::new)));
        return "index";
    }

    /** Creates a new category and adds it to database.
     * @param name Name of the new category.
     * @param description Description for the category.
     * @return Redirect to the address of newly created category.
     */
    @RequestMapping(method = RequestMethod.POST)
    public String createCategory(@RequestParam String name, @RequestParam String description) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        c = cateRepo.save(c);
        return "redirect:/category/" + c.getId();
    }

    /** Modifies the current category.
     * @param id The category which is being modified.
     * @param name New name for this category.
     * @param description New description for this category.
     * @return Redirect to the management interface.
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH)
    public String modifyCategory(@PathVariable long id, @RequestParam String name,
            @RequestParam String description) {
        Category c = cateRepo.findById(id).orElseThrow(IdNotFoundException::new);
        c.setName(name);
        c.setDescription(description);
        cateRepo.save(c);
        return "redirect:/manage";
    }

    /** Deletes a category from the database.
     * @param id The ID of the category to be deleted.
     * @return 200 OK if the deletion was successful.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCategory(@PathVariable long id) {
        Category c = cateRepo.findById(id).orElseThrow(IdNotFoundException::new);
        cateRepo.delete(c);
        return ResponseEntity.ok(c);
    }
}

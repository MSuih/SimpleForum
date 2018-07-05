package simplforum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import simplforum.models.Category;

/** Repository for categories.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    //No custom methods
}

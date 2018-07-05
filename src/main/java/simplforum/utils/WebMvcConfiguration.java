package simplforum.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web MVC configuration for this application. Used only to register the login page,
 * all other controllers are automatically searched and registered by Spring.
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    /** Adds login view controller to the registry.
     * @param registry Registry of view controllers.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}

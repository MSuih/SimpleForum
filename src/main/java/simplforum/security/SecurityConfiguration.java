package simplforum.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** The security configuration implementation for this application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userService;

    /** Configures the security of this application. Called by Spring Security to
     * allow or deny requests based on request method and location.
     * @param http HttpSecurity object.
     * @throws Exception If exceptions occur when configuring.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET,
                "/",
                "/thread/**",
                "/redirect/*",
                "/category/**",
                "/topic/**",
                "/css/*.css",
                "/js/*.js",
                "/img/**",
                "/logout",
                "/login**").permitAll()
            .antMatchers(HttpMethod.POST,
                "/message").permitAll()
            .antMatchers(HttpMethod.POST,
                "/thread").hasAuthority("USER")
            .antMatchers(HttpMethod.POST,
                "/topic",
                "/category").hasAuthority("SUPERMOD")
            .antMatchers(HttpMethod.PATCH,
                "/message/**",
                "/thread/**").hasAuthority("USER")
            .antMatchers(HttpMethod.PATCH,
                "/category/**",
                "/topic/**").hasAuthority("SUPERMOD")
            .antMatchers(HttpMethod.DELETE,
                "/message/**",
                "/thread/**").hasAuthority("USER")
            .antMatchers(HttpMethod.DELETE,
                "/category/**",
                "/topic/**").hasAuthority("SUPERMOD")
            .antMatchers(
                "/manage",
                "/user/*").hasAuthority("SUPERMOD")
            .antMatchers("/signup").permitAll().and()
            .formLogin().loginPage("/login").permitAll().and()
            .logout().logoutUrl("/logout").permitAll();
    }

    /** Configures global state of the application. Called internally by Spring Security
     * when the application is launched.
     * @param auth Authentication manager builder instance.
     * @throws Exception If any exceptions occur.
     */
    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    /** The password encoder used by this application.
     * @return A password encoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

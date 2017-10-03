package pl.lingaro.od.workshop.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@Profile("default")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean(name = "userDetailsService")
    public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate) {
        final JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
        userDetailsManager.setJdbcTemplate(jdbcTemplate);
        userDetailsManager.setUsersByUsernameQuery(
                "SELECT login AS username, password_hash AS password, TRUE AS enabled FROM user WHERE login=?"
        );
        userDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT login AS username, 'ROLE_USER' FROM user WHERE login=?"
        );
        return userDetailsManager;
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(

                        "/", // allow home page with published files
                        "/download/*" // allow download of published files from home page
                ).permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .and().logout();
    }
}

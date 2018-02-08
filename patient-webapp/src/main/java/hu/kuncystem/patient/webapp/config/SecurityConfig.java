package hu.kuncystem.patient.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hu.kuncystem.patient.webapp.login.LoginController;
import hu.kuncystem.patient.webapp.login.LoginService;

/**
 * This is an default web security config. We use this to the authentication and
 * we handle the login form. We get the user and password from the database. The
 * password was crypted with BCrypt algorithm.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 22.
 * 
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginService loginService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * Set authentication provider which use the LoginService object. The
     * LoginService will define the user from the database.
     */
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // set service
        authenticationProvider.setUserDetailsService(loginService);
        // set password encoder
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        return authenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/", "/index")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')");
        http.authorizeRequests().antMatchers("/usermanager")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')");
        http.authorizeRequests().antMatchers("/mycalendar")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')");
        http.authorizeRequests().antMatchers("/about")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')");
        http.authorizeRequests().antMatchers("/myaccount")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')");
        http.authorizeRequests().antMatchers("/saveUser")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')");
        http.authorizeRequests().antMatchers("/usermanager/addUser")
            .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')");
        
        http.formLogin().loginPage("/login").loginProcessingUrl("/j_spring_security_check")
                .failureUrl("/login?type=" + LoginController.MESSAGE_TYPE_LOGIN_ERROR)
                .defaultSuccessUrl("/loginSuccess?redirect=index", true).usernameParameter("username")
                .passwordParameter("password").and().logout()
                .logoutSuccessUrl("/login?type=" + LoginController.MESSAGE_TYPE_LOGOUT).and().exceptionHandling()
                .accessDeniedPage("/403").and().csrf();
    }
}

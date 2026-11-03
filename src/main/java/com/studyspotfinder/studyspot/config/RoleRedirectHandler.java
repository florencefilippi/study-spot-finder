package com.studyspotfinder.studyspot.config;

import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import jakarta.servlet.ServletException;
import java.io.IOException;

@Component 
public class RoleRedirectHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        for(GrantedAuthority authority : authentication.getAuthorities()){
            if(authority.getAuthority().equals("ROLE_ADMIN")){
                response.sendRedirect("/admin");
                System.out.println("Redirecting to admin page");
                return;
            }else if(authority.getAuthority().equals("ROLE_CONTRIBUTOR")){
                response.sendRedirect("/contributor");
                System.out.println("Redirecting to contributor page");
                return;
            }
            else if(authority.getAuthority().equals("ROLE_USER")){
                response.sendRedirect("/home");
                System.out.println("Redirecting to user page");
                return;
            }
        }
        System.out.println("No matching role found, redirecting to home page");
        response.sendRedirect("/home");
    }
}

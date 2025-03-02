package com.nuka.nuka_server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(getClass());
    
    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (accessToken == null ) {
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = jwtUtil.resolveClaims(request);

            if(claims != null & jwtUtil.validateClaims(claims)) {
                String id = claims.getSubject();
                Authentication authentication = new UsernamePasswordAuthenticationToken(id,"",new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (Exception e) {
	    String traza = "doFilterInternal 1 : ";
	    logger.error(traza + e.getMessage());
        }
        
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
	    String traza = "doFilterInternal 2 : ";
	    logger.error(traza + e.getMessage());
	    //e.printStackTrace();
        }
        
        
    }
}
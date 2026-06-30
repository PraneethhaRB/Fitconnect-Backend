package com.fitconnect.fitconnect_backend.security;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.User;
public class SecurityUtils {
public static String getCurrentUserEmail()
{
    User userDetails = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userDetails.getUsername();
}
}

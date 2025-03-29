package com.quan_ly.spring.utils;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Objects;

public class Middleware {

    public static User middleware(HttpServletRequest request){;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(CommonConstant.SESSION_USER);
        if (Objects.nonNull(user)) {
            return user;
        } else {
            return null;
        }
    }
    public  static boolean middlewareAdmin( HttpServletRequest request){;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(CommonConstant.SESSION_ADMIN);
        if (Objects.nonNull(user)) {
            return true;
        } else {
            return false;
        }
    }

    public  static User currentUser( HttpServletRequest request){;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(CommonConstant.SESSION_ADMIN);
        return  user;
    }
}

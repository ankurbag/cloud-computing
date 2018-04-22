package com.csye6225.spring2018.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public String index(HttpServletRequest request) {
        logger.info("Loading home page.");
        HttpSession session = request.getSession();

        if (session.getAttribute("loggedInUser") == null) {
            //show login and signup options
            return "index";
        } else {
            return "Home";

        }
    }
}

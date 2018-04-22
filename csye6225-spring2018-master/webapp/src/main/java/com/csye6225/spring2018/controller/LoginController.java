package com.csye6225.spring2018.controller;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.csye6225.spring2018.model.User;
import com.csye6225.spring2018.model.UserWrapper;
import com.csye6225.spring2018.service.UserService;
import com.csye6225.spring2018.validator.UserValidator;
import com.google.gson.JsonObject;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class LoginController {

    /*@Autowired
    private UserRepository userRepository;*/
    final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    private String PROFILE_NAME;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public Environment environment;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "LoginForm";
    }
    /*REST API for login*/
    /*@RequestMapping(value = "/login.html", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String isValidUser(@RequestBody UserWrapper userWrapper){
        JsonObject jsonObject = new JsonObject();
        String email = userWrapper.getUsername();
        String password = userWrapper.getPassword();
       if(email.isEmpty() || password.isEmpty()){
           jsonObject.addProperty("message", "Enter valid credentials!");
       }else { //user has entered username and password
          boolean validEmail = validateEmail(email);
          /*check if email present in DB. If email is present, retrieve the corresponding password and
          * decrypt it. Compare decrypted password and user inputted password. if they match then valid user
          * else invalid user. If valid user display current time. Create session.
          * API for logging out
          * /
          if(validEmail){
              //search email in db
              User user = userService.findByEmail(email);
              if(user == null){
                  jsonObject.addProperty("message","enter valid credentials!");
              }else{
                  //user exists
                  //retrieve existing pwd from db, decrypt using bcrypt ,compare passwords
                  String pwdFromDb = user.getPassword();
                  //decrypt password using bcrypt
                  boolean pwd = bCryptPasswordEncoder.matches(password, pwdFromDb);
                  if(pwd){
                      Date date = new Date();
                      jsonObject.addProperty("message","Hello! The current time is: " + date.toString()
                              +" To logout click on the logout option.");
                  }
                  else{
                      jsonObject.addProperty("message","Sorry! Wrong username/password. Try Again!");
                  }
                  
              }


          }else{
              jsonObject.addProperty("message","Username has to be valid email!");
          }
       }
       return jsonObject.toString();

    }*/

    @RequestMapping(value = "/login.html", method = RequestMethod.POST)

    public String isValidUser(Model model, HttpServletRequest request) throws Exception {
        //JsonObject jsonObject = new JsonObject();
        PROFILE_NAME = environment.getProperty("app.profile.name");

        HttpSession session = request.getSession();
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(email + " " + password);
        if (email.isEmpty() || password.isEmpty()) {
            session.setAttribute("message", "Enter valid credentials!");
        } else { //user has entered username and password
            boolean validEmail = validateEmail(email);
            System.out.println("****outside validEmail method****");
            /*check if email present in DB. If email is present, retrieve the corresponding password and
             * decrypt it. Compare decrypted password and user inputted password. if they match then valid user
             * else invalid user. If valid user display current time. Create session.
             * API for logging out
             * */
            if (validEmail) {
                //search email in db
                User user = userService.findByEmail(email);
                System.out.println("****outside findByEmail method****");
                if (user == null) {
                    session.setAttribute("message", "enter valid credentials!");
                } else {
                    //user exists
                    //retrieve existing pwd from db, decrypt using bcrypt ,compare passwords
                    String pwdFromDb = user.getPassword();
                    //decrypt password using bcrypt
                    boolean pwd = bCryptPasswordEncoder.matches(password, pwdFromDb);
                    System.out.println(pwd);
                    if (pwd) {
                        Date date = new Date();
                        System.out.println("User logged in");
                        session.setAttribute("message", "Hello! The current time is: " + date.toString()
                                + " To logout click on the logout option.");
                        session.setAttribute("loggedInUser", user);
                        if(PROFILE_NAME.equalsIgnoreCase("aws"))
                        {
                            request.getSession().setAttribute("s3Mode", "on");
                            request.getSession().setAttribute("s3Url", user.getS3Url());
                        }
                        if(user.getS3Url() != null || user.getS3Url() != null)
                        {
                            logger.info("Setting s3Url: ", user.getS3Url());
                            session.setAttribute("s3Url", user.getS3Url());
                        }
                    } else {
                        session.setAttribute("message", "Sorry! Wrong username/password. Try Again!");
                    }

                }


            } else {
                session.setAttribute("message", "Username has to be valid email!");
            }
        }
        return "Home";

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) throws Exception {
        session.invalidate();

        return "index";
    }

    @RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
    public String getForgotPassword(HttpSession session) throws Exception {
        session.invalidate();

        return "forgotpassword";
    }

    @RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
    public String submitForgotpassword(HttpSession session, HttpServletRequest request) throws Exception {


//create a new SNS client and set endpoint
        AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withRegion("us-east-1")
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
                //new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());
        //snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

//create a new SNS topic
        CreateTopicRequest createTopicRequest = new CreateTopicRequest("EmailTopic");
        CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
//print TopicArn
        logger.info("Topic ARN: " + createTopicResult);
//get request id for CreateTopicRequest from SNS metadata
        logger.info("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
        request.setAttribute("emailSent", "Yes");

//publish to an SNS topic
        String emailAddress = request.getParameter("username");
        PublishRequest publishRequest = new PublishRequest(createTopicResult.getTopicArn(), emailAddress);
        PublishResult publishResult = snsClient.publish(publishRequest);
//print MessageId of message published to SNS topic
        logger.info("MessageId - " + publishResult.getMessageId());

        return "forgotpassword";

    }

    //code to validate email. Only email is allowed as username
    public boolean validateEmail(String email) {
        System.out.println("****inside validEmail method****");
        Pattern email_pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher match = email_pattern.matcher(email);
        return match.matches();
    }

    @RequestMapping(value = "/aboutme", method = RequestMethod.GET)
    public String aboutmeDisplay(HttpServletRequest request) throws Exception {
        // session.invalidate();
        HttpSession session = request.getSession();
        String aboutme = request.getParameter("aboutme");
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            /*User us= userService.findUserByEmail("viragz18@gmail.com");
            if(us==null) {
                session.setAttribute("about", "Not user registered yet");
                return "aboutme";
            }
            String about = us.getAboutme();
            System.out.println(about);
            if(about==null)
                session.setAttribute("about", "Host has not added the Aboutme, Please check it later");
            else*/
            session.setAttribute("about", "There is nothing to display here");
        } else {
            User u = userService.findUserByEmail(user.getEmail());
            session.setAttribute("aboutme", u.getAboutme());
        }
        //session.setAttribute("aboutme","Sorry! Wrong username/password. Try Again!");
        return "aboutme";
    }

    @RequestMapping(value = "/aboutme", method = RequestMethod.POST)
    public String aboutmepost(HttpSession session, @RequestParam("aboutme") String aboutme, HttpServletRequest request) throws Exception {

        User user = (User) session.getAttribute("loggedInUser");
        User u = userService.findUserByEmail(user.getEmail());
        u.setAboutme(aboutme);
        userService.saveUser(u);
        session.setAttribute("aboutme", aboutme);
        return "aboutme";
    }

    @RequestMapping(value = "search-profiles", method = RequestMethod.GET)
    public String searchProfiles(HttpSession session, HttpServletRequest request) throws Exception {
        return "searchProfiles";
    }

    @RequestMapping(value = "/search-user-profile", method = RequestMethod.POST)
    public String searchUserProfile(Model model, HttpSession session, @RequestParam("username") String username, HttpServletRequest request) throws Exception {
        User searchedUser = userService.findByEmail(username);
        request.setAttribute("searchedUserProfile", searchedUser);
        model.addAttribute("searchusername", username);
        model.addAttribute("profiledata", searchedUser.getAboutme());
        return "searchProfiles";
    }
}

package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.service.AmazonClient;
import com.csye6225.spring2018.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.csye6225.spring2018.model.User;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@PropertySource(value = "classpath:application-aws.properties")
public class UploadController {

    final static Logger logger = LoggerFactory.getLogger(UploadController.class);

    //Save the uploaded file to this folder


    String systemUserName = System.getProperty("user.name");
    private String UPLOADED_FOLDER = "/home/" + systemUserName + "/images/";


    private String PROFILE_NAME;


    @Autowired
    private UserService userService;

    @Autowired
    public Environment environment;

    @Autowired
    private AmazonClient amazonClient;

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {

        User loggedInUser = (User)request.getSession().getAttribute("loggedInUser");
        PROFILE_NAME = environment.getProperty("app.profile.name");
        String fileUrl;

        //Checking file is png, jpg or jpeg
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:home";
        }

        String originalFileName = file.getOriginalFilename();
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            String ext = originalFileName.substring(i + 1);
            if (!(ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg"))){
                redirectAttributes.addFlashAttribute("message", "Please select a JPEG, PNG or JPG file to upload");
                return "redirect:/Home";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Please select a JPEG, PNG or JPG file to upload");
            return "redirect:/Home";
        }
        // checking completed

        // Upload file to local directory
        Path path=null;
        byte[] bytes=null;
        try {

            // Get the file and save it somewhere
            bytes = file.getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if profile is AWS or DEV
        if(PROFILE_NAME.equalsIgnoreCase("aws"))
        {
            fileUrl = this.amazonClient.uploadFile(file);
            loggedInUser.setS3Url(fileUrl);
            request.getSession().setAttribute("s3Mode", "on");
            request.getSession().setAttribute("s3Url", fileUrl);
            request.getSession().setAttribute("loggedInUser", loggedInUser);
            User user = (User)request.getSession().getAttribute("loggedInUser");
            logger.info("LoggedInUser s3url: ", user.toString());
        }
        else{
            path = Paths.get(UPLOADED_FOLDER.concat(""+file.getOriginalFilename()));
            fileUrl = path.toString();
            loggedInUser.setImagePath(path.toString());
            request.getSession().removeAttribute("s3Mode");
            try {
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Image Path: " + fileUrl);
        userService.saveUser(loggedInUser);
        request.getSession().setAttribute("loggedInUser", loggedInUser);
        return "redirect:/Home";
    }
    //check


    //@GetMapping("/profilepic") // //new annotation since 4.3
    public void getProfilePicture(HttpServletResponse response, HttpServletRequest request) throws IOException {

        User user = (User)request.getSession().getAttribute("loggedInUser");
        ServletContext servletContext = request.getServletContext();
        Path path = Paths.get(UPLOADED_FOLDER.concat("space.jpg"));
        System.out.println("Path: " + path.toString());
        InputStream in = ResourceLoader.class.getClass().getResourceAsStream(path.toString());
        System.out.println("InputStream: " + in);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());

        /*Path path = Paths.get(UPLOADED_FOLDER.concat("space.jpg"));
        BufferedImage imageOnDisk = ImageIO.read(new File(path.toString()));
        //Create a ByteArrayOutputStrea object to write image to
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //Write the image to the OutputStream
        ImageIO.write(imageOnDisk, "jpg", baos);*/
    }

    @GetMapping("/profilepic")
    public ResponseEntity<byte[]> getImagesResource(HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession(false);
        User loggedInUser = (User)request.getSession().getAttribute("loggedInUser");
        String filename = loggedInUser.getImagePath();
        /*if(filename == null)
        {
            filename = Paths.get(UPLOADED_FOLDER.concat(""+"business-man-blue.png")).toString();
        }*/

        logger.info("Filename: " + filename);
        InputStream inputImage = new FileInputStream(filename);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        int l = inputImage.read(buffer);
        while(l >= 0) {
            outputStream.write(buffer, 0, l);
            l = inputImage.read(buffer);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/png");
        return new ResponseEntity<byte[]>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    /*@GetMapping("/bucket-profilepic")
    public String getBucketImage(HttpServletRequest request) throws IOException {

    }*/

    //@GetMapping(value = "/profilepic")
    @ResponseBody
    public ResponseEntity<Resource> getImageAsResource(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Resource resource =
                new ServletContextResource(request.getServletContext(), "/WEB-INF/images/image-example.jpg");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/delete")
    public String deleteImage(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        User user = (User)session.getAttribute("loggedInUser");
        String s3ModeOn = (String)request.getSession().getAttribute("s3Mode");
        if(s3ModeOn != null)
        {
            if (PROFILE_NAME.equalsIgnoreCase("aws"))
            {
                String deleteMsg = this.amazonClient.deleteFileFromS3Bucket(user.getS3Url());
                user.setS3Url(null);
                session.setAttribute("fileUrl", "");
                session.setAttribute("s3Url", null);
            }
        }
        else{
            user.setImagePath(null);
        }
        userService.saveUser(user);
        session.setAttribute("loggedInUser", user);
        return "Home";
    }

    @GetMapping("/Home")
    public String uploadStatus() {
        return "Home";
    }

}

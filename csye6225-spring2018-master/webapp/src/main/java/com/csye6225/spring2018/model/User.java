package com.csye6225.spring2018.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class User implements Serializable{

    private static final long serialVersionUID = 1113799434508676095L;

    @Id
    @GeneratedValue
    @Column(name="userid")
    private int id;

    @Column(name = "firstname")
    @Size(min=1, max=100, message="First name must be between 1 and 32 characters")
    private String firstName;

    @Column (name = "lastname")
    @Size(min=1, max=100, message="Last name must be between 1 and 32 characters")
    private String lastName;

    @Column (name = "email")
    @Pattern(regexp = "^[_  A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message="Email address is invalid")
    private String email;

    @Column (name = "password")
    @Size(min=1, message="Password can not be empty")
    private String password;

    @Column (name = "imagePath")
    @Size(min=1, max=100)
    private String imagePath;

    @Column (name = "aboutme")
    @Size(min=1, max=140, message="About me must be between 1 and 140 characters")
    private String aboutme;

    @Column (name = "s3Url")
    @Size(min=1, max=10000)
    public String s3Url;


    public User(User user) {
        this.email=user.getEmail();
        this.password=user.getPassword();
        this.id=user.getId();
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.imagePath=user.getImagePath();
        this.s3Url=user.getS3Url();
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public User() {


    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", s3Url='" + s3Url + '\'' +
                '}';
    }
}

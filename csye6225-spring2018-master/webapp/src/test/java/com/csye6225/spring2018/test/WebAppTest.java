package com.csye6225.spring2018.test;

//import com.csye6225.spring2018.controller.RegisterController;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

public class WebAppTest {

    @Test
    public void testSpringProject(){
        System.out.println("1");
        assertEquals(1,1);
    }

}

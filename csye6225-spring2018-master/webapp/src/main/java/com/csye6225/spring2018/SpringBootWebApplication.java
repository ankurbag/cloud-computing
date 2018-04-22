package com.csye6225.spring2018;

import com.csye6225.spring2018.controller.ImageServlet;
import com.csye6225.spring2018.controller.MyServletContextListener;
//import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.servlet.ServletContextListener;


@SpringBootApplication
public class SpringBootWebApplication {

  @Bean
  public ServletRegistrationBean servletRegistrationBean() {
    ServletRegistrationBean bean = new ServletRegistrationBean(
            new ImageServlet(), "/images");
    return bean;
  }

  @Bean
  public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
    ServletListenerRegistrationBean<ServletContextListener> bean =
            new ServletListenerRegistrationBean<>();
    bean.setListener(new MyServletContextListener());
    return bean;

  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringBootWebApplication.class, args);
  }

}

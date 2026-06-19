package com.ayushcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
class MyFirstSpringBootApp {

}

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Writing my first spring boot program!!");
        SpringApplication.run(MyFirstSpringBootApp.class, args);
        System.out.println("Spring boot application started!!");
    }
}

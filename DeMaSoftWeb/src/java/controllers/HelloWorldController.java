/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;

/**
 *
 * @author Fernando
 */
@Controller //declarar que esta clase es un controller
public class HelloWorldController { 
    @RequestMapping("/hello")//declarar que este metodo respondera a /hello    
    public ModelAndView helloWorld() {
        String message = "Hello World, Spring 3.0!";
        return new ModelAndView("hello", "message", message);//el primer parametro es el nombre de la vista
        
    }
}
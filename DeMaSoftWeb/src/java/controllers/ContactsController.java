/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.web.servlet.*;
import org.springframework.stereotype.*;
import models.*;

/**
 *
 * @author Fernando
 */
@Controller //declarar que esta clase es un controller
public class ContactsController { 
    @RequestMapping(value = "/addContact", method = RequestMethod.POST)//post
    public String addContact(@ModelAttribute("contact")
                            Contact contact, BindingResult result) {
         
        System.out.println("First Name:" + contact.getFirstname() + 
                    "Last Name:" + contact.getLastname());
         
        return "redirect:contacts.html";
    }
    
    @RequestMapping("/contacts")
    public ModelAndView showContacts() {
        Contact newContact = new Contact();
        newContact.setEmail("@");
        return new ModelAndView("contacts", "newContact", newContact);
    }
}
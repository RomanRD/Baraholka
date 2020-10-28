package com.roman.sprboot.controllers;

import com.roman.sprboot.entities.User;
import com.roman.sprboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/login", "/registration"})
    public String authorization(HttpServletRequest request, @ModelAttribute("user") User user, Model model) {
        model.addAttribute("login", request.getRequestURI().equals("/login"));
        return "registration/login";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam("password2") String passwordConfirm,
                          @Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        model.addAttribute("login", false);

        if(!userService.validationRegistration(passwordConfirm, user, bindingResult, model)){
            return "registration/login";
        }
        if (!userService.addUser(user)) {
            model.addAttribute("emailError", "Пользователь с таким email уже зарегистрирован");
            return "registration/login";
        }

        String email = user.getUsername();
        redirectAttributes.addAttribute("email", email.substring(email.lastIndexOf("@") + 1));
        return "redirect:/registration/check-email";
    }

    @GetMapping("/registration/check-email")
    public String checkEmail(@RequestParam(value = "email", required = false) String email, Model model) {
        if(email==null){
            return "redirect:/";
        }
        model.addAttribute("email", email);
        return "registration/registration_message";
    }

    @GetMapping("/registration/activate/{code}")                                                                    //ссылки проверить в хтмл
    public String activateUser(@PathVariable String code, Model model) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("event", "accepted");
        } else {
            model.addAttribute("event", "not accepted");
        }
        return "registration/registration_message";
    }
}
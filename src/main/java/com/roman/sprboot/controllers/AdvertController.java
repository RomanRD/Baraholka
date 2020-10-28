package com.roman.sprboot.controllers;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.User;
import com.roman.sprboot.forms.EditAdvertForm;
import com.roman.sprboot.repos.AdvertRepo;
import com.roman.sprboot.repos.UserRepo;
import com.roman.sprboot.service.AdvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class AdvertController {

    private UserRepo userRepo;
    private AdvertRepo advertRepo;
    private AdvertService advertService;

    @Autowired
    public AdvertController(UserRepo userRepo, AdvertRepo advertRepo, AdvertService advertService){
        this.userRepo = userRepo;
        this.advertRepo = advertRepo;
        this.advertService = advertService;
    }

    @GetMapping("/post-new-advert")
    public String getFormAdvert(@ModelAttribute("advert") Advert advert,
                         Authentication authentication){
        User author = userRepo.findByUsername(authentication.getName());
        String contactPerson = author.getName();
        advert.setContactPerson(contactPerson);
        advert.setAuthor(author);
        advert.setPhoneNumber(author.getPhoneNumber());
        advert.setLocation(author.getLocation());

        return "advert/post_advert";
    }

    @PostMapping("/post-new-advert")
    public String addAdvert(@RequestParam(name="lower", required = false) String lower,
                            @Valid @ModelAttribute("advert") Advert advert,
                            BindingResult bindingResult, Model model) {
        model.addAttribute("lower", lower);

        if(!advertService.validation(advert, bindingResult, model)){
            return "advert/post_advert";
        }

        if (!advertService.addAdvert(advert, lower)) {
            model.addAttribute("postError", "Упсс, что-то пошло не так. Не удалось подать ваше объявление, попробуйте сделать это заново.");
            return "advert/post_advert";
        }

        return "redirect:/profile/my_adverts";
    }

    @GetMapping("/advert/{id}/edit")
    public String getAdvertEditPage(@PathVariable Long id, Authentication authentication, Model model) {
        Advert advert = advertRepo.findById(id).get();
        if(!advert.getAuthor().equals((User)authentication.getPrincipal())){
            return "redirect:/profile/my_adverts";
        }
        if(advert.getPriceMin()==null){
            model.addAttribute("lower", "on");
        }

        model.addAttribute("editAdvert", new EditAdvertForm(advert));
        return "advert/edit_advert";
    }

    @PostMapping("/advert/{id}/edit")
    public String advertEdit(@PathVariable Long id,
                             @RequestParam(name="lower", required = false) String lower,
                             @Valid @ModelAttribute("editAdvert") EditAdvertForm editAdvertForm,
                             BindingResult bindingResult, Model model) {

        model.addAttribute("lower", lower);
        if(lower!=null && lower.equals("on")){
            editAdvertForm.setPriceMin(null);
        }

        if(!advertService.validation(editAdvertForm, bindingResult, model)){
            return "advert/edit_advert";
        }

        if(!advertService.editAdvert(editAdvertForm)){
            model.addAttribute("editError", "Упсс, что-то пошло не так. Не удалось подать ваше объявление, попробуйте сделать это заново.");
            return "advert/edit_advert";
        }
        return "redirect:/advert/" + id;
    }

}

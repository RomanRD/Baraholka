package com.roman.sprboot.controllers;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.Favorites;
import com.roman.sprboot.entities.User;
import com.roman.sprboot.repos.AdvertRepo;
import com.roman.sprboot.repos.FavoritesRepo;
import com.roman.sprboot.repos.UserRepo;
import com.roman.sprboot.service.AdvertService;
import com.roman.sprboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class ProfileController {

    private UserRepo userRepo;
    private AdvertRepo advertRepo;
    private FavoritesRepo favoritesRepo;
    private UserService userService;
    private AdvertService advertService;

    @Autowired
    public ProfileController(UserRepo userRepo, AdvertRepo advertRepo, FavoritesRepo favoritesRepo,
                             UserService userService, AdvertService advertService){
        this.userRepo = userRepo;
        this.advertRepo = advertRepo;
        this.favoritesRepo = favoritesRepo;
        this.userService = userService;
        this.advertService = advertService;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("user", ((User)authentication.getPrincipal()));
        model.addAttribute("event", "profile");
        return "profile/my_profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@RequestParam(name="img", required = false) MultipartFile photo,
                              @RequestParam(name="edit", required = false) String edit,
                              @Valid @ModelAttribute("user") User user,
                              BindingResult bindingResult, Model model) {
        model.addAttribute("edit", edit);
        model.addAttribute("event", "profile");

        boolean editData = (edit!=null && edit.equals("on"));
        if(editData || photo!=null){
            if(editData && bindingResult.hasErrors()){
                return "profile/my_profile";
            }
            model.addAttribute("succes", userService.editUserData(editData, user, photo));
        }
        return "redirect:/profile";
    }

//    @GetMapping("/favorites")
//    public String favorites(Authentication authentication, Model model) {
//        model.addAttribute("event", "favorites");
//
//        User user = userRepo.findByUsername(authentication.getName());
//        model.addAttribute("favorites", user.getFavorites());
//        return "profile/my_profile";
//    }

    @GetMapping("/profile/favorites")
    public String favorites(@RequestParam(value = "page", required = false) Long pageNum,
                            @PageableDefault(sort = {"id"}, size = 12, direction = Sort.Direction.DESC) Pageable pageable,
                            Authentication authentication, Model model) {
        model.addAttribute("event", "favorites");

        Page<Favorites> page = favoritesRepo.findAllByUser(pageable, (User)authentication.getPrincipal());
        if(pageNum != null) {
            if (pageNum <= 0) {
                return "redirect:/profile/favorites?page=1";
            }
            if (pageNum > page.getTotalPages()) {                                                           //url
                return "redirect:/profile/favorites?page=" + page.getTotalPages();
            }
        }
        model.addAttribute("favorites", page.getContent());                                       //передать url
        model.addAttribute("page", page);

        return "profile/my_profile";
    }

    @PostMapping("/favorites/{id}/remove")
    public String deleteFavorite(@PathVariable("id") Long advertId, Authentication authentication, Model model){
        model.addAttribute("event", "favorites");

        User user = userRepo.findByUsername(authentication.getName());
        advertService.removeFavorite(advertId, user);
        return "redirect:/profile/favorites";
    }

//    @GetMapping("/my_adverts")
//    public String adverts(Authentication authentication, Model model) {
//        model.addAttribute("event", "my_adverts");
//
//        User user = userRepo.findByUsername(authentication.getName());
//        model.addAttribute("adverts", user.getAdverts());
//        return "profile/my_profile";
//    }

    @GetMapping("/profile/my_adverts")
    public String adverts(@RequestParam(value = "page", required = false) Long pageNum,
                          @PageableDefault(sort = {"id"}, size = 12, direction = Sort.Direction.DESC) Pageable pageable,
                          Authentication authentication, Model model) {
        model.addAttribute("event", "my_adverts");

        Page<Advert> page = advertRepo.findAllByAuthor(pageable, (User)authentication.getPrincipal());
        if(pageNum != null) {
            if (pageNum <= 0) {
                return "redirect:/profile/my_adverts?page=1";
            }
            if (pageNum > page.getTotalPages()) {                                                           //url
                return "redirect:/profile/my_adverts?page=" + page.getTotalPages();
            }
        }
        model.addAttribute("adverts", page.getContent());                                       //передать url
        model.addAttribute("page", page);

        return "profile/my_profile";
    }

    @PostMapping("/advert/{id}/remove")
    public String advertRemove(@PathVariable Long id, Authentication authentication, Model model){
        model.addAttribute("success", advertService.removeAdvert(id, (User)authentication.getPrincipal()));
        return "redirect:/profile/my_adverts";
    }

    @GetMapping("/profile/security")
    public String security(Authentication authentication, Model model) {
        model.addAttribute("event", "security");

        model.addAttribute("currentEmail", authentication.getName());
        return "profile/my_profile";
    }

    @PostMapping("/security/edit-email")
    public String editEmail(HttpServletRequest request, Authentication authentication,
                            @RequestParam(name="newEmail", required = false) String newEmail, Model model) {
        model.addAttribute("event","security");
        if(!userService.validationEdit(newEmail, model)){
            return "profile/my_profile";
        }

//        User user = userRepo.findByUsername(authentication.getName());
        User user = (User)authentication.getPrincipal();
        userService.editEmail(newEmail, user);

        try {
            request.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        model.addAttribute("event", "edit");
        model.addAttribute("email", newEmail.substring(newEmail.lastIndexOf("@") + 1));
        return "registration/registration_message";
    }

    @GetMapping("/security/edit-email/activate/{code}")
    public String activateNewEmail(@PathVariable String code, Model model) {
        boolean isActivated = userService.activateNewEmail(code);
        if (isActivated) {
            model.addAttribute("event", "accepted");
        } else {
            model.addAttribute("event", "not accepted");
        }
        return "registration/registration_message";
    }

    @GetMapping("/security/edit-email/cancel")
    public String cancelEditEmail(@RequestParam(value = "email", required = false) String email, Model model) {
        userService.cancelChangeEmail(email);
        return "redirect:/profile";
    }

    @PostMapping("/security/edit-password")
    public String editPassword(@RequestParam(name="currentPassword", required = false) String currentPassword,
                               @RequestParam(name="newPassword", required = false) String newPassword,
                               @RequestParam(name="confirmPassword", required = false) String confirmPassword,
                               Authentication authentication, Model model) {
        model.addAttribute("event", "security");
        model.addAttribute("success",
                        userService.editPassword(currentPassword, newPassword, confirmPassword, authentication.getName(), model));
        return "profile/my_profile";
    }

    @GetMapping("/exit")
    public String exit(Model model) {
        model.addAttribute("event", "exit");
        return "profile/my_profile";
    }
}

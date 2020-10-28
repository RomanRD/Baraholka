package com.roman.sprboot.controllers;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.User;
import com.roman.sprboot.repos.AdvertRepo;
import com.roman.sprboot.repos.FavoritesRepo;
import com.roman.sprboot.service.AdvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@Controller
public class MainController {

    private AdvertService advertService;
    private FavoritesRepo favoritesRepo;
    private AdvertRepo advertRepo;

    @Autowired
    public MainController(AdvertService advertService, FavoritesRepo favoritesRepo, AdvertRepo advertRepo){
        this.advertRepo = advertRepo;
        this.advertService = advertService;
        this.favoritesRepo = favoritesRepo;
    }

    @GetMapping("/main")
    public String mainPage(@RequestParam(value = "page", required = false) Long pageNum,
                           @PageableDefault(sort = {"id"}, size = 10, direction = Sort.Direction.DESC) Pageable pageable,
                           Authentication authentication, Model model) {
        Page<Advert> page = advertRepo.findAll(pageable);
//        if(pageNum != null) {
//            if (pageNum <= 0) {
//                return "redirect:/main?page=1";
//            }
//            if (pageNum > page.getTotalPages()) {                                                           //url
//                return "redirect:/main?page=" + page.getTotalPages();
//            }
//        }

        if(authentication != null){
            Set<Long> favorites = advertService.getFavoritesId((User)authentication.getPrincipal());
            model.addAttribute("favorites", favorites);
        }
        model.addAttribute("adverts", page.getContent());                                       //передать url
        model.addAttribute("page", page);
        return "baraholka";
    }

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/main?page=1";
    }

    @GetMapping("/advert/{id}")
    public String productPage(@PathVariable Long id, Authentication authentication, Model model){
        Advert advert = advertRepo.findById(id).get();
        if(authentication!=null)
            model.addAttribute("favorite", (favoritesRepo.findByUserAndAdvert((User)authentication.getPrincipal(), advert)!=null));
        model.addAttribute("advert", advert);
        return "advert/product";
    }

    @PostMapping("/favorites")
    @ResponseBody
    public void addToFavorites(@RequestParam(value = "advertId") Long id, Authentication authentication){
        advertService.addFavorites(id, (User)authentication.getPrincipal());
    }

    @PostMapping("/aaa")
//    @ResponseBody
    public String addToFavoes(){
        return "redirect:/";
    }
}

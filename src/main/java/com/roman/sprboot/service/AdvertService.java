package com.roman.sprboot.service;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.Favorites;
import com.roman.sprboot.entities.Photo;
import com.roman.sprboot.entities.User;
import com.roman.sprboot.forms.EditAdvertForm;
import com.roman.sprboot.repos.AdvertRepo;
import com.roman.sprboot.repos.FavoritesRepo;
import com.roman.sprboot.repos.PhotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


@Service
public class AdvertService{

    @Value("${upload.path}")
    private String uploadPath;

    private AdvertRepo advertRepo;
    private PhotoRepo photoRepo;
    private FavoritesRepo favoritesRepo;

    @Autowired
    public AdvertService(AdvertRepo advertRepo, PhotoRepo photoRepo, FavoritesRepo favoritesRepo){
        this.advertRepo = advertRepo;
        this.photoRepo = photoRepo;
        this.favoritesRepo = favoritesRepo;
    }

    @Transactional(rollbackFor = IOException.class)
    public boolean addAdvert(Advert advert, String lower){
        advert.setTimestamp(new Date());
        advert.setCurrentPrice((float)advert.getPriceMax());
        if (lower != null && lower.equals("on")) {
            advert.setPriceMin(null);
        }
        Advert advertDB = advertRepo.save(advert);
        String path = advertDB.getAuthor().getId() + "/" + advertDB.getId();
        File uploadDir = new File(uploadPath + "/" + path);
        uploadDir.mkdirs();
        int numberPhoto = 0;
        String resultFilename;
        Photo photo;
        for (MultipartFile file : advertDB.files) {
            if (!file.isEmpty()) {
                resultFilename = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
                try {
                    file.transferTo(new File(uploadDir.getPath() + "/" + resultFilename));
                } catch (IOException e) {
                    e.printStackTrace();                                                                    //logging
                    return false;
                }

                photo = new Photo();                                                        //могу ли я наполнить массив в advert и чтобы оно само создало нужные записи, когда я закинул в массив
                photo.setAdvert(advert);
                photo.setFilename(path + "/" + resultFilename);
                photo.setNumber(numberPhoto);
                numberPhoto++;

                photoRepo.save(photo);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = IOException.class)
    public boolean editAdvert(EditAdvertForm editAdvertForm){
        Advert advert = advertRepo.findById(editAdvertForm.getId()).get();
        editAdvert(advert, editAdvertForm);

        String path = advert.getAuthor().getId() + "/" + advert.getId();;
        File uploadDir = new File(uploadPath + "/" + path);

        Photo photo;
        String resultFilename;
        MultipartFile file;
        int numberNewPhoto = advert.getPhotos().size()-1;
        for(int i = 0; i<editAdvertForm.getEditNumber().length; i++){
            if(editAdvertForm.getEditNumber()[i].equals("add")){
                file = editAdvertForm.getFiles()[i];
                resultFilename = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
                try {
                    file.transferTo(new File(uploadDir.getPath() + "/" + resultFilename));
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                if(i>advert.getPhotos().size()-1){
                    photo = new Photo();
                    photo.setFilename(path + "/" + resultFilename);
                    photo.setNumber(numberNewPhoto);
                    photo.setAdvert(advert);
                    photoRepo.save(photo);
                    numberNewPhoto++;
                }else{
                    photo = photoRepo.findByAdvertAndNumberIs(advert, i);
                    photo.setFilename(path + "/" + resultFilename);
                    photoRepo.save(photo);
                }
            }

            if(editAdvertForm.getEditNumber()[i].equals("delete")){
                 photo = photoRepo.findByAdvertAndNumberIs(advert, i);
                 photoRepo.delete(photo);
                 photoRepo.setPhotoNumber(advert.getId(), i);
            }
        }
        return true;
    }

    public void editAdvert(Advert changeAdvert, Advert advertForm){
        changeAdvert.setHeadline(advertForm.getHeadline());
        changeAdvert.setDescription(advertForm.getDescription());
        changeAdvert.setPriceMax(advertForm.getPriceMax());
        changeAdvert.setPriceMin(advertForm.getPriceMin());
        changeAdvert.setLocation(advertForm.getLocation());
        changeAdvert.setPhoneNumber(advertForm.getPhoneNumber());
        changeAdvert.setContactPerson(advertForm.getContactPerson());
        advertRepo.save(changeAdvert);
    }

    @Transactional
    public boolean removeAdvert(Long id, User user){
        Advert advert = advertRepo.findById(id).get();
        if(advert.getAuthor().equals(user)){
            favoritesRepo.deleteByAdvert(advert);
            removeDirectory(advert);
            advertRepo.deleteById(id);
            return true;
        }else{
            return false;
        }
    }

    private void removeDirectory(Advert advert){
        File uploadDir = new File(uploadPath + "/" + advert.getAuthor().getId() + "/" + advert.getId());
        try {
            Files.walk(uploadDir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();                                    //logging
        }
    }

    @Transactional
    public void addFavorites(Long id, User user){
        Advert advert = advertRepo.findById(id).get();
        Favorites favorites = favoritesRepo.findByUserAndAdvert(user, advert);
        if(favorites == null) {
            favorites = new Favorites();
            favorites.setUser(user);
            favorites.setAdvert(advert);
            favoritesRepo.save(favorites);
        }else{
            removeFavorite(advert.getId(), user);
        }
    }

    public HashSet<Long> getFavoritesId(User user){
        HashSet<Long> favoritesId = new HashSet();
        for(Favorites favorites : favoritesRepo.findByUser(user)){
            favoritesId.add(favorites.getAdvert().getId());
        }
        return favoritesId;
    }

    @Transactional
    public void removeFavorite(Long id, User user){
        favoritesRepo.deleteByAdvertIdAndUser(id, user);
    }

    public boolean validation(Advert advert, BindingResult bindingResult, Model model){
        boolean result = true;
        if (bindingResult.hasErrors()){
            result = false;
        }
        if (advert.getPriceMin()!=null && advert.getPriceMax()<advert.getPriceMin()){
            result = false;
            model.addAttribute("priceMinError", "Окончательная цена не может быть больше начальной");
        }
        if(!(advert instanceof EditAdvertForm) && !validationPhoto(advert)){
            result = false;
            model.addAttribute("photoError", "Загрузите минимум одну фотографию");
        }
        if((advert instanceof EditAdvertForm) && !validationPhoto((EditAdvertForm)advert)){
            result = false;
            model.addAttribute("photoError", "Загрузите минимум одну фотографию");
        }
        return result;
    }

    private boolean validationPhoto(Advert advert){
        for(MultipartFile file: advert.files){
            if(!file.isEmpty()){
                return true;
            }
        }
        return false;
    }

    private boolean validationPhoto(EditAdvertForm editAdvertForm){
        for(MultipartFile file: editAdvertForm.files){
            if(!file.isEmpty()){
                return true;
            }
        }

        int countDeleting = 0;
        int countAdding = editAdvertForm.getAdvert().getPhotos().size();
        for(String text: editAdvertForm.getEditNumber()){
            if(text.equals("delete")){
                countDeleting++;
            }
        }
        if(countDeleting >= countAdding) return false;
        return true;
    }

    @Scheduled(fixedRate = 1000*60*60)              //1 hour
    public void clearOldAdvert() {
        List<Advert> adverts = advertRepo.findAllByTimestampLessThan(new Date(new Date().getTime() - 1000*3600*24*14));
        for(Advert advert: adverts){
            removeDirectory(advert);
            advertRepo.deleteById(advert.getId());
        }
    }

    @Scheduled(fixedRate = 4*60*60*1000)              //4 hour
    public void priceDrop() {
        List<Advert> adverts = advertRepo.findAllByTimestampLessThanAndPriceMinIsNotNull(new Date(new Date().getTime() - 1000*3600*24*7));    //Start reducing the price after 7 days
        int priceDecrease;
        for(Advert advert: adverts){
            priceDecrease = (advert.getPriceMax() - advert.getPriceMin())/(7*6);                               //Reduce the price 6 times every 7 days
            advert.setCurrentPrice(advert.getCurrentPrice()-priceDecrease);
            advertRepo.save(advert);
        }
    }
}

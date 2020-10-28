package com.roman.sprboot.forms;

import com.roman.sprboot.entities.Advert;

public class EditAdvertForm extends Advert{

    private Advert advert = new Advert();

    public String[] editNumber = new String[8];

    public EditAdvertForm() {}

    public EditAdvertForm(Advert advert) {
        setId(advert.getId());
        setHeadline(advert.getHeadline());
        setDescription(advert.getDescription());
        setPriceMax(advert.getPriceMax());
        setPriceMin(advert.getPriceMin());
        setContactPerson(advert.getContactPerson());
        setPhoneNumber(advert.getPhoneNumber());
        setLocation(advert.getLocation());
        setPhotos(advert.getPhotos());
        setAuthor(advert.getAuthor());
        setAdvert(advert);
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public String[] getEditNumber() {
        return editNumber;
    }

    public void setEditNumber(String[] editNumber) {
        this.editNumber = editNumber;
    }
}

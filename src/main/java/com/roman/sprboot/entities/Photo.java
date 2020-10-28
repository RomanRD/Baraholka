package com.roman.sprboot.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="photo", schema = "public", catalog = "baraholka")
public class Photo {

    @Id
    @SequenceGenerator(name = "photo", sequenceName = "photo_sequences", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "photo")
    private Long id;

    @ManyToOne(targetEntity=Advert.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "advert_id")
    private Advert advert;

    private String filename;

    private Integer number;

    public Photo(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

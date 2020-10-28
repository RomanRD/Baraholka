package com.roman.sprboot.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="favorites", schema = "public", catalog = "baraholka")
public class Favorites {

    @Id
    @SequenceGenerator(name = "favorites", sequenceName = "favorites_sequences", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favorites")
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity=Advert.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "advert_id")
    private Advert advert;

    public Favorites(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorites favorites = (Favorites) o;
        return Objects.equals(id, favorites.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

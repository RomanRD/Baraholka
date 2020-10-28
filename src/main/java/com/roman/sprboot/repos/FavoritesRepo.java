package com.roman.sprboot.repos;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.Favorites;
import com.roman.sprboot.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepo extends JpaRepository<Favorites, Long> {
    Favorites findByUserAndAdvert(User user, Advert advert);

    List<Favorites> findByUser(User user);

    Page<Favorites> findAllByUser(Pageable pageable, User user);

    void deleteByAdvert(Advert advert);

    void deleteByAdvertIdAndUser(Long id, User user);

}

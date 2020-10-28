package com.roman.sprboot.repos;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.Photo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PhotoRepo  extends CrudRepository<Photo, Long> {

    Photo findByAdvertAndNumberIs(Advert advert, Integer number);

    @Transactional
    @Modifying
    @Query(value = "update public.photo set number = number - 1 where advert_id = :id and number > :number", nativeQuery = true)
    void setPhotoNumber(@Param("id") Long advertId, @Param("number") Integer afterNumber);

    void delete(Photo photo);
}

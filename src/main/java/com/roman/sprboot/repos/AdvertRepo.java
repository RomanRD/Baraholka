package com.roman.sprboot.repos;

import com.roman.sprboot.entities.Advert;
import com.roman.sprboot.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertRepo extends JpaRepository<Advert, Long> {

    List<Advert> findAll();

    Page<Advert> findAll(Pageable pageable);

    Page<Advert> findAllByAuthor(Pageable pageable, User author);

    Optional<Advert> findById(Long id);

    List<Advert> findAllByTimestampLessThan(Date date);

    List<Advert> findAllByTimestampLessThanAndPriceMinIsNotNull (Date date);

    @Query(value = "SELECT last_value FROM advert_sequences", nativeQuery = true)
    int getCurrentValAdvertSequence();

    void delete(Advert advert);

    void deleteById(Long id);
}

package com.roman.sprboot.repos;

import com.roman.sprboot.entities.EmailActivationCodes;
import com.roman.sprboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmailActivationCodesRepo extends JpaRepository<EmailActivationCodes, Long> {
    List<EmailActivationCodes> findAllByTimestampLessThan (Date date);

    EmailActivationCodes findByActivationCode (String code);

    EmailActivationCodes findByEmail (String email);

    EmailActivationCodes findByUser (User user);

    void delete(EmailActivationCodes emailActivationCodes);
}

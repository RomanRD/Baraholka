package com.roman.sprboot.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "email_code", schema = "public", catalog = "baraholka")
public class EmailActivationCodes {

    @Id
    @SequenceGenerator(name = "code", sequenceName = "code_sequences", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "code")
    Long id;

    private String email;

    private String activationCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public EmailActivationCodes(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailActivationCodes that = (EmailActivationCodes) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.roman.sprboot.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "usr", schema = "public", catalog = "baraholka")
public class User implements UserDetails {

    @Id
    @SequenceGenerator(name = "user", sequenceName = "user_sequences", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user")
    private Long id;

    @Pattern(regexp="(\\D*)", message = "Нельзя вводить цифры")
    @NotBlank(message = "Поле обязательно для заполнения")
    @Size(max=70, message = "Не может быть длинее 70 символов")
    private String name;

    @Email(message = "Неправильный формат email")
    @NotBlank(message = "Поле обязательно для заполнения")
    @Column(name = "email")
    private String username;

    @Size(min = 6, message = "Минимум 6 символов")
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Pattern(regexp="(^(\\+?(38)?0|0)[0-9]{9}$|)", message = "Неправильный формат номера телефона")
    @Column(name = "phone_number")
    private String phoneNumber;

    private String location;

    private String photo;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private EmailActivationCodes emailActivationCodes;

    private boolean active;

    @OneToMany(targetEntity=Advert.class, mappedBy = "author", fetch = FetchType.LAZY)
    @OrderBy("timestamp DESC")
    private List<Advert> adverts;

    @OneToMany(targetEntity=Favorites.class, mappedBy = "user", fetch = FetchType.LAZY)
    @OrderBy("id DESC")
    private List<Favorites> favorites;

    public User(){
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public EmailActivationCodes getEmailActivationCodes() {
        return emailActivationCodes;
    }

    public void setEmailActivationCodes(EmailActivationCodes emailActivationCodes) {
        this.emailActivationCodes = emailActivationCodes;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Advert> getAdverts() {
        return adverts;
    }
    public void setAdverts(List<Advert> adverts) {
        this.adverts = adverts;
    }

    public List<Favorites> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorites> favorites) {
        this.favorites = favorites;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
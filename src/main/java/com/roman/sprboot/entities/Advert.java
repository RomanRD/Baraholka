package com.roman.sprboot.entities;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="advert", schema = "public", catalog = "baraholka")
public class Advert {

    @Id
    @SequenceGenerator(name = "advert", sequenceName = "advert_sequences", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advert")
    private Long id;

    @NotBlank(message = "Поле обязательно для заполнения")
    @Length(max = 70, message = "Заголовок не может быть длинее 70 знаков")
    private String headline;

    @Length(max = 9000, message = "Описание не может быть длинее 9000 знаков")
    private String description;

    @Column(name = "price_max")
    @NotNull(message = "Заполните начальную цену")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private Integer priceMax;

    @Column(name = "current_price")
    private Float currentPrice;

    @Column(name = "price_min")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private Integer priceMin;

    @OneToMany(targetEntity=Photo.class, mappedBy = "advert", orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("number ASC")
    private List<Photo> photos;

    @Transient
    public MultipartFile[] files = new MultipartFile[8];

    @NotBlank(message = "Поле обязательно для заполнения")                                                     //сделать отдельной таблицей
    private String location;

    @Pattern(regexp="(^(\\+?(38)?0|0)[0-9]{9}$)", message = "Неправильный формат номера телефона")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Pattern(regexp="(\\D*)", message = "Нельзя вводить цифры")
    @Size(max=70, message = "Не может быть длинее 70 символов")
    @Column(name = "contact_person")
    private String contactPerson;

    @ManyToOne(targetEntity=User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;


    public Advert() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriceMax() {
        return priceMax;
    }
    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public Float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Integer getPriceMin() {
        return priceMin;
    }
    public void setPriceMin(Integer priceMin) {
        this.priceMin = priceMin;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advert advert = (Advert) o;
        return Objects.equals(id, advert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
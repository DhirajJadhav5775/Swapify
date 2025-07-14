package com.Rewards.Swapify.model;

import jakarta.persistence.*;

@Entity
@Table(name = "giftCart")
public class GiftCart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // or email/username if not using userId
    private Long giftId;

    private String brand;
    private String details;
    private Double price;
    private String expdate;
    private String seller;
    private String promocode;
    // Constructors
    public GiftCart() {}

    public GiftCart(Long userId, Gift gift) {
        this.email = email;
        this.giftId = (long) gift.getId();
        this.brand = gift.getBrand();
        this.details = gift.getDetails();
        this.price = (double) gift.getPrice();
        this.expdate = gift.getExpdate();
    }

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

    public Long getGiftId() {
        return giftId;
    }

    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }
}
//th>Brand</th>
//<th>Price</th>
//<th>Seller</th>
//<th>Promo Code</th>
//<th>Actions</th>

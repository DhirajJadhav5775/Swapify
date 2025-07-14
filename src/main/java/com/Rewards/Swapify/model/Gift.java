package com.Rewards.Swapify.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Gift")
public class Gift
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String brand;
    @Column
    private String details;
    @Column
    private String promocode;
//    @Column
//    private boolean isPurchased;

    @Column(nullable = false)
    private Boolean isPurchased = false;

    @Column
    private int price;
    @Column
    private String expdate;
    @Column
    private String status;
    @Column
    private String seller;
    @Column(name = "listed_date")
    private LocalDateTime listedDate;

//    @Transient
//    private LocalDateTime orderDate;

    @Column(name = "order_date") // This will map to the database column
    private LocalDateTime orderDate; // Use LocalDateTime to store date and time

    @Column(nullable = false)
    private boolean isExpired = false;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller1_id")
    private User seller1;

    @Transient
    private boolean addedToCart;

    public boolean isAddedToCart() {
        return addedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        this.addedToCart = addedToCart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSeller1() {
        return seller1;
    }

    public void setSeller1(User seller1) {
        this.seller1 = seller1;
    }

    //    public int getID() {
//        return id;
//    }
//
//    public void setID(int ID) {
//        this.id = ID;
//    }

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

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
    //    public boolean isPurchased() {
//        return isPurchased;
//    }
//
//    public void setisPurchased(boolean purchased) {
//        isPurchased = purchased;
//    }

    public Boolean getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
    }



    //    public User getSeller1() {
//        return seller1;
//    }
//
//    public void setSeller1(String seller1) {
//        this.seller1 = seller1;
//    }

    @Override
    public String toString() {
        return "Gift{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", details='" + details + '\'' +
                ", promocode='" + promocode + '\'' +
                ", price=" + price +
                ", expdate='" + expdate + '\'' +
                ", status='" + status + '\'' +
                ", seller='" + seller + '\'' +
                ", orderDate=" + orderDate +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gift)) return false;
        Gift gift = (Gift) o;
        return id == gift.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

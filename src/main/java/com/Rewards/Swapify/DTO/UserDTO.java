package com.Rewards.Swapify.DTO;

public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;

    private int buyVoucherCount;
    private int sellVoucherCount;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id, String firstname, String lastname, String email, int buyVoucherCount, int sellVoucherCount) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.buyVoucherCount = buyVoucherCount;
        this.sellVoucherCount = sellVoucherCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBuyVoucherCount() {
        return buyVoucherCount;
    }

    public void setBuyVoucherCount(int buyVoucherCount) {
        this.buyVoucherCount = buyVoucherCount;
    }

    public int getSellVoucherCount() {
        return sellVoucherCount;
    }

    public void setSellVoucherCount(int sellVoucherCount) {
        this.sellVoucherCount = sellVoucherCount;
    }
}


package com.Rewards.Swapify.service;

import com.Rewards.Swapify.Repository.GiftRepository;
import com.Rewards.Swapify.model.Gift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GiftService
{
    @Autowired
    private GiftRepository giftRepository;

    public long countgifts()
    {
        return giftRepository.count();
    }

    public long countGiftsSoldToday() {
        LocalDate today = LocalDate.now(); // Get today's date
        return giftRepository.countByOrderDate(today); // Count gifts sold today
    }

    public List<Gift> getRecentVouchers()
    {
        return giftRepository.findTop3ByOrderByOrderDateDesc();
    }

    public List<Gift> findAll()
    {
        return giftRepository.findAll();
    }

    public void deleteVoucherByID(int id) {
        System.out.println("Attempting to delete voucher with ID: " + id);
        giftRepository.deleteById(id);
    }

    public int getBuyVoucherCountByUserId(Long userId)
    {
        System.out.println("getting buy count for user: " + userId);
        return giftRepository.countByBuyerId(userId);
    }

    public int getSellVoucherCountByUserId(Long userId)
    {
        return giftRepository.countBySeller1Id(userId);
    }



//    public List<Gift> getSellOrdersByDate(LocalDate date) {
//        return  giftRepository.findAllByOrderDate(date);
//    }
//    public long countSellOrdersForToday() {
//        LocalDate today = LocalDate.now();
//        return giftRepository.countByOrderDate(today);
//    }
}

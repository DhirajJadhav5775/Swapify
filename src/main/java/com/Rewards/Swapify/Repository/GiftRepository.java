package com.Rewards.Swapify.Repository;

import com.Rewards.Swapify.model.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Integer> {
    List<Gift> findByStatus(String status);

    @Query("SELECT g FROM Gift g WHERE g.status = 'available' AND g.seller != :email")
    List<Gift> findAvailableGiftsExcludingSeller(@Param("email") String email);


    long count();
    List<Gift> findAll();

    @Query("SELECT COUNT(g) FROM Gift g WHERE DATE(g.orderDate) = :date")
    long countByOrderDate(@Param("date") LocalDate date);
//    long sellCount();

//    @Query("SELECT g FROM Gift g ORDER BY g.orderDate DESC")
    List<Gift> findTop3ByOrderByOrderDateDesc();

    int countByBuyerId(Long buyerId);
    int countBySeller1Id(Long seller1Id);

    List<Gift> findByBuyerId(Long buyerId);
    List<Gift> findBySeller1Id(Long seller1Id);

//    int countByListedDate(LocalDate date);

    //    void deleteById(int id);
    @Query("SELECT g FROM Gift g WHERE g.seller1.id = :userId")
    List<Gift> findGiftsBySeller1UserId(@Param("userId") Long userId);

    @Query("SELECT g FROM Gift g WHERE g.buyer.id = :userId")
    List<Gift> findGiftsByBuyerUserId(@Param("userId") Long userId);

//    int countVouchersByListedDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
//    @Query("SELECT COUNT(g) FROM Gift g WHERE g.orderDate BETWEEN :start AND :end")
//    int countVouchersByListedDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(g) FROM Gift g WHERE g.orderDate BETWEEN :start AND :end")
    long countGiftsByOrderDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    List<Gift> findByIsPurchasedTrueAndOrderDateBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}

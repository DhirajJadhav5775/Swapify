package com.Rewards.Swapify.Repository;

import com.Rewards.Swapify.model.GiftCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<GiftCart,Long>
{
    List<GiftCart> findByEmail(String email);

    boolean existsByEmailAndGiftId(String email, int id);

//    void save(GiftCart cartItem);
}

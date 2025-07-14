package com.Rewards.Swapify.Repository;

import com.Rewards.Swapify.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long>
{
    List<Transactions> findAllByOrderByDateTimeDesc();
}

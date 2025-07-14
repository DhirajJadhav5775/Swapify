package com.Rewards.Swapify.Repository;

import com.Rewards.Swapify.model.Contactus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contactus, Long>
{

}

package com.Rewards.Swapify.Repository;

import com.Rewards.Swapify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);

//    boolean findByEmail(String email);
    User findByEmail(String email);

//    Optional<User> findByMobile(String mobilenumber);
    List<User> findAll();

    long count();

//    int countByCreatedDate(LocalDate date);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdDate BETWEEN :start AND :end")
    int countUsersByCreatedDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    User findByFirstname(String username);

//    @Query("SELECT COUNT(u) FROM User u WHERE u.createdDate BETWEEN :start AND :end")
//    int countUsersByCreatedDate(LocalDateTime startOfDay, LocalDateTime endOfDay);

//    int countUsersByCreatedDate(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

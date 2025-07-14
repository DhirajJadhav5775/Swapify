package com.Rewards.Swapify.service;

import com.Rewards.Swapify.Repository.UserRepository;
import com.Rewards.Swapify.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService
{
    @Autowired
    private UserRepository userRepository;

    public long countusers()
    {
        return userRepository.count();
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public void deleteUser(int id)
    {
        userRepository.deleteById((long) id);
    }

    public User findByEmail(String currentUserEmail)
    {
        return userRepository.findByEmail(currentUserEmail);
    }
}

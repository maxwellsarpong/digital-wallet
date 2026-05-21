package com.example.DigitalWallet.service;
import com.example.DigitalWallet.dto.requests.user.RegisterRequest;
import com.example.DigitalWallet.dto.requests.user.UpdateUserRequest;
import com.example.DigitalWallet.dto.response.user.UserResponse;
import com.example.DigitalWallet.entity.User;
import com.example.DigitalWallet.enums.Role;
import com.example.DigitalWallet.exception.UserAlreadyExistException;
import com.example.DigitalWallet.exception.UserNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.example.DigitalWallet.repository.UserRepository;
import com.example.DigitalWallet.utils.PasswordUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    /*
        register a user
     */
    @CacheEvict(value = "users", key = "'all'")
    public UserResponse registerUser(RegisterRequest request){

        log.info("User registration started ...");

        if(userRepository.existsByEmail(request.getEmail())){
            log.error("User already registered in the database");
            throw new UserAlreadyExistException("User with the email" + request.getEmail() + "already exists");
        }
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .password(PasswordUtil.hashPassword(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .nationalId(request.getNationalId())
                .active(true)
                .role(Role.CUSTOMER)
                .build();
        log.info("New User created ...");

        User newUser = userRepository.save(user);
        return mapToResponse(newUser);
    }


    /*
        Get all the users
    */
    @Cacheable(value = "users", key = "'all'")
    public List<UserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    /*
        Get a single user
     */
    @Cacheable(value = "user", key = "#id")
    public UserResponse getUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found");
            return new UserNotFoundException("User not Found");
        }
        );
        return mapToResponse(user);

    }


    /*
        Update user
    */
    @Caching(
            put   = { @CachePut(value = "user", key = "#id") },
            evict = { @CacheEvict(value = "users", key = "'all'") }
    )
    public UserResponse updateUser(UUID id, UpdateUserRequest request){
        //check if user exists
        User user = userRepository.findById(id).orElseThrow(()-> {
                    log.error("user with id : " + id + " not found");
                    return new UserNotFoundException("User not found");
            }
        );
        if(request.getEmail() != null && !user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail()) ){
            throw new UserAlreadyExistException("User with the email already exists");
        }
        if(request.getEmail()!= null && user.getEmail().isBlank()){
            user.setEmail(request.getEmail());
        }
        if(request.getFirstName()!=null && user.getFirstName().isBlank()){
            user.setFirstName(request.getFirstName());
        }
        if(request.getLastName()!=null && user.getLastName().isBlank()){
            user.setLastName(request.getLastName());
        }
        if(request.getPhoneNumber()!=null && user.getPhoneNumber().isBlank()){
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getNationalId()!=null && user.getNationalId().isBlank()){
            user.setNationalId(request.getNationalId());
        }
        if(request.getDateOfBirth()!=null & user.getDateOfBirth()==null){
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if(request.getRole()!=null && user.getRole()==null){
            user.setRole(request.getRole());
        }

        if(request.getPassword() != null && !request.getPassword().isBlank()){
            user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        }

        User userUpdate = userRepository.save(user);
        return mapToResponse(userUpdate);
    }


    /*
        Delete a User
     */
    @Caching(evict = {
            @CacheEvict(value = "user",  key = "#id"),
            @CacheEvict(value = "users", key = "'all'")
    })
    public void deleteUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(
                ()-> new UserNotFoundException("User not found")
        );
        userRepository.delete(user);
    }

}

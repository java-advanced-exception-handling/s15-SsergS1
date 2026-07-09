package com.softserve.itacademy.service;

import com.softserve.itacademy.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoConverter userDtoConverter;

    @Transactional
    public User register(CreateUserDto createUserDto) {
        log.info("Registering new user with email: {}", createUserDto.getEmail());
        createUserDto.setRole(UserRole.USER);
        User user = userDtoConverter.convertToUser(createUserDto);
        user.setPassword("{noop}" + user.getPassword());
        return create(user);
    }

    @Transactional
    public User create(User user) {
        if (user != null) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                log.warn("Attempt to create user with existing email: {}", user.getEmail());
                throw new IllegalArgumentException("User with email '" + user.getEmail() + "' already exists");
            }
            log.info("Creating user: {}", user.getEmail());
            return userRepository.save(user);
        }
        log.error("Null entity reference while creating user");
        throw new NullEntityReferenceException("User cannot be 'null'");
    }

    @Transactional(readOnly = true)
    public User readById(long id) {
        log.info("Reading user by id: {}", id);
        return userRepository.findById(id).orElseThrow(
                () -> {
                    log.error("User with id {} not found", id);
                    return new EntityNotFoundException("User with id " + id + " not found");
                });
    }

    @Transactional
    public UserDto update(UpdateUserDto updateUserDto) {
        if (updateUserDto == null) {
            log.error("Attempted to update a null user");
            throw new NullEntityReferenceException("User cannot be 'null'");
        }
        log.info("Updating user with id: {}", updateUserDto.getId());
        User user = userRepository.findById(updateUserDto.getId()).orElseThrow(
                () -> {
                    log.error("User with id {} not found for update", updateUserDto.getId());
                    return new EntityNotFoundException("User with id " + updateUserDto.getId() + " not found");
                });
        if (updateUserDto.getRole() != null && user.getRole() == UserRole.ADMIN) {
            log.info("Changing role for user {} from {} to {}", user.getEmail(), user.getRole(), updateUserDto.getRole());
            user.setRole(updateUserDto.getRole());
            updateUserDto.setRole(null); // prevent double setting in converter if we want to be strict, 
                                         // but fillFields already has a null check now.
        } else if (updateUserDto.getRole() != null) {
            log.warn("Non-admin user {} attempted to change role or role change not allowed", user.getEmail());
            updateUserDto.setRole(null); // don't allow non-admin to change role, or admin to change to null
        }
        userDtoConverter.fillFields(user, updateUserDto);
        userRepository.save(user);
        log.info("User {} updated successfully", user.getEmail());
        return userDtoConverter.toDto(user);
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting user with id: {}", id);
        User user = readById(id);
        userRepository.delete(user);
        log.info("User {} deleted successfully", user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> findById(long id) {
        return userRepository.findById(id).map(userDtoConverter::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto findByIdThrowing(long id) {
        return userRepository.findById(id).map(userDtoConverter::toDto).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userDtoConverter::toDto).toList();
    }
}

package net.petri.springboot.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import net.bytebuddy.utility.RandomString;
import net.petri.springboot.components.UserValidator;
import net.petri.springboot.entity.User;
import net.petri.springboot.mapper.UserMapper;
import net.petri.springboot.model.FM.UserFM;
import net.petri.springboot.model.VM.UserVM;
import net.petri.springboot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;


import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 *
 * @param userMapper
 */

@CrossOrigin
@Service

public record UserService(UserRepository userRepository, UserMapper userMapper,
                          UserValidator userValidator, BCryptPasswordEncoder bCryptPasswordEncoder,
                          JavaMailSender mailSender) {

    public List<UserVM> getAll() {
        List<User> entity = userRepository.findAll();

        List<UserVM> entities = userMapper.mapToList(entity);

        return entities;
    }

    public List<UserVM> getAllPaginated(int page, int size) {
        Pageable getPage = PageRequest.of(page, size);
        Page<User> entity = userRepository.findAll(getPage);

        List<UserVM> entities = userMapper.mapToListPage(entity);

        return entities;
    }

    public List<UserVM> search(int page, int size, String search) {
        Pageable getPage = PageRequest.of(page, size);
        Page<User> entity;

        if (Objects.equals(search, "")) {
            entity = userRepository.findAll(getPage);
        } else {
            Specification<User> specification = UserRepository.search(search);
            entity = userRepository.findAll(specification, getPage);
        }

        List<UserVM> entities = userMapper.mapToListPage(entity);

        return entities;
    }

    public UserVM find(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();

        return userMapper.mapToVM(entity);
    }

    public UserVM findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return userMapper.mapToVM(user);
    }

    public UserVM create(UserFM newEntity) throws MessagingException, UnsupportedEncodingException {
        if (!userValidator.validateCreateUser(newEntity)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        newEntity.setPassword(bCryptPasswordEncoder.encode(newEntity.getPassword()));

        String randomCode = RandomString.make(64);
        newEntity.setVerificationCode(randomCode);
        newEntity.setEnabled(false);
        newEntity.setRole("ROLE_USER");

        User entity = userMapper.mapToEntity(newEntity);
        userRepository.save(entity);

        sendVerificationEmail(entity, "http://localhost:8080");
        return userMapper.mapToVM(entity);
    }


    public UserVM update(Long id, UserFM newEntity) {
        if (!userValidator().validateUpdateUser(newEntity, id)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<User> optionalUser = userRepository.findById(id);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();

        newEntity.setPassword(entity.getPassword());

        userMapper.mapToEntity(entity, newEntity);
        userRepository.save(entity);

        return userMapper.mapToVM(entity);
    }

    public UserVM editProfile(Long userId, UserFM newEntity,Authentication authentication) {
        if (!userValidator.validateEditProfile(userId, newEntity, authentication)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        entity = optionalUser.get();

        userValidator.setEditUser(newEntity, entity);

        if (!newEntity.getPassword().isEmpty() && newEntity.getPassword()  != null) {
            newEntity.setPassword(bCryptPasswordEncoder.encode(newEntity.getPassword()));
        } else {
            newEntity.setPassword(entity.getPassword());
        }

        newEntity.setRole(entity.getRole());

        userMapper.mapToEntity(entity, newEntity);
        userRepository.save(entity);

        return userMapper.mapToVM(entity);
    }

    public void delete(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User entity;
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        entity = optionalUser.get();
        userRepository.delete(entity);
    }

    public void resetPassword(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            String randomCode = RandomString.make(64);
            user.setResetToken(randomCode);

            userRepository.save(user);

            sendResetToken(user, "http://localhost:8080");
        }
    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }

    public boolean reset(String resetToken, String newPassword) {
        User user = userRepository.findByResetToken(resetToken);

        if (user == null) {
            return false;
        } else {
            if (!userValidator.passswordCheck(newPassword)) {
                return false;
            }
            user.setResetToken(null);
            User entity;

            entity = user;

            if (!user.getPassword().isEmpty() && user.getPassword()  != null) {
                entity.setPassword(bCryptPasswordEncoder.encode(newPassword));
            } else {
                entity.setPassword(user.getPassword());
            }

            userRepository.save(entity);

            return true;
        }
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "petri.auth@gmail.com";
        String senderName = "Petri Net";
        String subject = "Zweryfikuj swoją rejestracje!";
        String content = "Drogi [[name]],<br>"
                + "Nacisnij w link poniżej aby zweryfikować swoją rejestracje:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ZWERYFIKUJ</a></h3>"
                + "Dziękujemy,<br>"
                + "Petri Net Online.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "/login?verify=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }

    private void sendResetToken(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "petri.auth@gmail.com";
        String senderName = "Petri Net";
        String subject = "Resetowanie hasła!";
        String content = "Drogi [[name]],<br>"
                + "Nacisnij w link poniżej aby zresetować swoje hasło:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">RESETUJ</a></h3>"
                + "Dziękujemy,<br>"
                + "Petri Net Online.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "/reset?code=" + user.getResetToken();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

    }
}
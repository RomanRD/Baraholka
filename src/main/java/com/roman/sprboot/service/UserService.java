package com.roman.sprboot.service;

import com.roman.sprboot.entities.Role;
import com.roman.sprboot.entities.User;
import com.roman.sprboot.entities.EmailActivationCodes;
import com.roman.sprboot.repos.EmailActivationCodesRepo;
import com.roman.sprboot.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    @Value("${upload.path}")
    private String uploadPath;

    private UserRepo userRepo;
    private EmailActivationCodesRepo emailActivationCodesRepo;
    private MailSender mailSender;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, EmailActivationCodesRepo emailActivationCodesRepo,
                       MailSender mailSender, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.emailActivationCodesRepo = emailActivationCodesRepo;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByUsername(email);
    }

    @Transactional
    public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        EmailActivationCodes emailActivationCodes = new EmailActivationCodes();

        if (userFromDb != null) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));

        String activeCode = UUID.randomUUID().toString();

        emailActivationCodes.setUser(user);
        emailActivationCodes.setEmail(user.getUsername());
        emailActivationCodes.setActivationCode(activeCode);
        emailActivationCodes.setTimestamp(new Date());

        userRepo.save(user);

        try{
            emailActivationCodesRepo.save(emailActivationCodes);
        }catch (DataIntegrityViolationException e){
            if (e.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException") && ((SQLException) e.getMostSpecificCause()).getSQLState().equals("23505")) {
                activeCode = UUID.randomUUID().toString();
                emailActivationCodes.setActivationCode(activeCode);
                emailActivationCodesRepo.save(emailActivationCodes);
            }else {
                throw e;
            }
        }

        String message = String.format(
                "Здравстуйте, %s! \n" +
                "Чтобы завершить процесс регистрации перейдите по ссылке: http://localhost:8080/registration/activate/%s",
                user.getName(),
                activeCode
        );
        mailSender.sendEmail(user.getUsername(), "Активировать аккаунт", message);

        return true;
    }

    @Transactional
    public boolean activateUser(String code) {
        EmailActivationCodes emailActivationCodes = emailActivationCodesRepo.findByActivationCode(code);
        if (emailActivationCodes == null) {
            return false;
        }
        User user = emailActivationCodes.getUser();
        user.setActive(true);

        userRepo.save(user);
        emailActivationCodesRepo.deleteById(emailActivationCodes.getId());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean editUserData(boolean editData, User user, MultipartFile photo){
        User userDB = userRepo.findById(user.getId()).get();

        if (editData) {
            userDB.setName(user.getName());
            userDB.setPhoneNumber(user.getPhoneNumber());
            userDB.setLocation(user.getLocation());
        }
        if(photo!=null && !photo.isEmpty()){
            File uploadDir = new File(uploadPath + "/" + user.getId());
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + photo.getOriginalFilename();

            try{
                photo.transferTo(new File(uploadDir.getPath() + "/" + resultFilename));
                userDB.setPhoto(uploadDir.getName() + "/" + resultFilename);

                File[] files = uploadDir.listFiles();
                for(File file: files){
                    if(file.isFile() && !file.getName().equals(resultFilename)){
                            file.delete();
                    }
                }

            }catch (IOException e){
                                                                                                                        //logging
                e.printStackTrace();
                return false;
            }
        }
        userRepo.save(userDB);
        return true;
    }

    public boolean editEmail(String newEmail, User user){
        EmailActivationCodes emailActivationCodes = emailActivationCodesRepo.findByUser(user);
        if(emailActivationCodes != null){
            return false;
        }

        String activeCode = user.getId() + UUID.randomUUID().toString();
        emailActivationCodes = new EmailActivationCodes();
        emailActivationCodes.setUser(user);
        emailActivationCodes.setEmail(newEmail);
        emailActivationCodes.setActivationCode(activeCode);
        emailActivationCodes.setTimestamp(new Date());
        emailActivationCodesRepo.save(emailActivationCodes);
                                                                                            //вынести в отдельный метод, вынести слова в проперти
        String messageNewEmail = String.format(
                   "Здравстуйте, %s! \n" +
                           "Чтобы активировать новый email перейдите по ссылке: http://localhost:8080/security/edit-email/activate/%s",
                   user.getName(),
                   activeCode
           );
        mailSender.sendEmail(newEmail, "Подтвердить email", messageNewEmail);

        String messageOldEmail = String.format(
                "Здравстуйте, %s! \n" +
                        "На вашем аккаунте был изменен email. " +
                        "Вы можете отменить действие в течении 7 дней, перейдите по ссылке: http://localhost:8080/security/edit-email/cancel?email=%s.",
                user.getName(), user.getUsername()
            );
        mailSender.sendEmail(user.getUsername(), "Изменен email", messageOldEmail);

        return true;
        }

    @Transactional
    public boolean activateNewEmail(String code){
        EmailActivationCodes emailActivationCodes = emailActivationCodesRepo.findByActivationCode(code);
        if (emailActivationCodes == null) {
            return false;
        }
        User user = emailActivationCodes.getUser();
        String currentEmail = user.getUsername();
        user.setUsername(emailActivationCodes.getEmail());
        userRepo.save(user);

        emailActivationCodes.setEmail(currentEmail);
        emailActivationCodes.setTimestamp(new Date(new Date().getTime() + 1000*3600*24*7));         //одна неделя чтобы отменить изменение email
        emailActivationCodesRepo.save(emailActivationCodes);
        return true;
    }

    @Transactional(rollbackFor = NullPointerException.class)
    public boolean cancelChangeEmail(String email){                                                     //ПОПРОБОВАТЬ
        EmailActivationCodes emailActivationCodes = emailActivationCodesRepo.findByEmail(email);
        User user;
        if (emailActivationCodes == null) {
                if((user = userRepo.findByUsername(email))==null)return false;
                emailActivationCodes = emailActivationCodesRepo.findByUser(user);
                emailActivationCodesRepo.deleteById(emailActivationCodes.getId());
                return true;
        }else{
            user = emailActivationCodes.getUser();
            user.setUsername(email);

            userRepo.save(user);
            emailActivationCodesRepo.delete(emailActivationCodes);
            return true;
        }
    }

    public boolean editPassword(String currentPassword, String newPassword, String confirmPassword, String username, Model model){
        User user = userRepo.findByUsername(username);
        if(!validationEdit(currentPassword, newPassword, confirmPassword, user, model)){
            return false;
        }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);

            String messageNewEmail = String.format(
                    "Здравстуйте, %s! \n" +
                            "На вашем аккаунте изменен пароль",
                    user.getName()
            );
            mailSender.sendEmail(user.getUsername(), "Изменен пароль", messageNewEmail);
        return true;
    }

    public boolean validationRegistration(String passwordConfirm, User user, BindingResult bindingResult, Model model){
        boolean result = true;
        if (bindingResult.hasErrors()) {
            result = false;
        }
        if (user.getPassword() != null && !StringUtils.isEmpty(passwordConfirm)
                && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("password2Error", "Пароли не совпадают");
            result = false;
        }
        return result;
    }

    public boolean validationEdit(String newEmail, Model model){
        User user = userRepo.findByUsername(newEmail);
        if(user != null){
            model.addAttribute("emailError", "Этот email уже занят");
            return false;
        }
        String emailPattern ="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(newEmail);

        boolean result = matcher.matches();
        if(!result) model.addAttribute("emailError", "Неправильный формат email");
        return result;
    }

    public boolean validationEdit(String currentPassword, String newPassword, String confirmPassword, User user, Model model){
        boolean result = true;
        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            result=false;
            model.addAttribute("currentPasswordError", "Вы ввели неправильный текущий пароль");
        }
        if(newPassword.length()<6){
            result = false;
            model.addAttribute("newPasswordError", "Пароль не может быть меньше 6 символов");
        }
        if(currentPassword!=null && !newPassword.equals(confirmPassword)){
            result=false;
            model.addAttribute("confirmPasswordError", "Пароли не совпадают");
        }
        return result;
    }

    @Scheduled(fixedRate = 1000*60*60)  //1 hour
    @Transactional
    public void clearNonActivatedUsers() {
        List<EmailActivationCodes> users = emailActivationCodesRepo.findAllByTimestampLessThan(new Date(new Date().getTime() - 1000*3600*24));
        User user;
        for(int i=0; i<users.size(); i++){
            user = users.get(i).getUser();
            if(!user.isActive()) userRepo.delete(user);
        }
    }

}
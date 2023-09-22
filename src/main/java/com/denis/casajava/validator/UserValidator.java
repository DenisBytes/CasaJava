package com.denis.casajava.validator;



import com.denis.casajava.models.User;
import com.denis.casajava.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    UserService uService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        User user = (User) object;
        ValidationUtils.rejectIfEmpty(errors, "username", "Empty", "No username present");
        ValidationUtils.rejectIfEmpty(errors, "email", "Empty", "No email present");
        ValidationUtils.rejectIfEmpty(errors, "password", "Empty", "No password present");
        ValidationUtils.rejectIfEmpty(errors, "passwordConfirmation", "Empty", "No password confirmation present");
        if (uService.emailExists(user.getEmail())) {
            errors.rejectValue("email", "Exists", "Email already exists");
        }
        if(!user.getEmail().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
            errors.rejectValue("email", "Invalid", "Email not valid");
        }
        if (uService.usernameExists(user.getUsername())) {
            errors.rejectValue("username", "Exists", "Username already exists");
        }
        if (user.getUsername().length() <= 3) {
            errors.rejectValue("username", "Size", "Username must be greater than 3 characters");
        }
        if(!user.getUsername().matches("(([A-za-z]+[\\s]{1}[A-za-z]+))")){
            errors.rejectValue("username", "Invalid", "Username not valid");
        }
        if (user.getPassword().length() <= 5) {
            errors.rejectValue("password", "Size", "Password must be greater than 5 characters");
        }
        if (!user.getPasswordConfirmation().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirmation", "Match", "Passwords must match");
        }
    }

}
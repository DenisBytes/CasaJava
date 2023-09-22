package com.denis.casajava.controllers;


import com.denis.casajava.models.User;
import com.denis.casajava.services.UserService;
import com.denis.casajava.validator.UserValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;


    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute("newUser")User user,
                               BindingResult result, HttpServletRequest request){

        userValidator.validate(user,result);

        if(result.hasErrors()){
            return "loginPage";
        }
        String password= user.getPassword();

        if (userService.isEmailAlreadyRegistered(user.getEmail())) {
            result.rejectValue("email", "Duplicate.user.email");
            return "loginPage";
        }

        if (user.getEmail().equals("stayinmilan.info@gmail.com") || user.getUsername().equals("Bashkim Skendaj") || user.getUsername().equals("Lindita Skendaj")){
            userService.saveUserWithSuperAdminRole(user);
        }

        else{
            result.reject("This login page is for admin only. If you are a user you donìt need create an account");
        }

        authWithHttpServletRequest(request, user.getEmail(), password);
        return "redirect:/admin";
    }
    public void authWithHttpServletRequest(HttpServletRequest request, String email, String password) {
        try {
            request.login(email, password);
        } catch (ServletException e) {
            System.out.println("Error while login: " + e);
        }
    }


    @GetMapping("/login")
    public String loginGet(@ModelAttribute("newUser") User user,
                           @RequestParam(value="error", required=false) String error,
                           @RequestParam(value="logout", required=false) String logout,
                           Model model) {
        if(error!=null) {
            model.addAttribute("errorMessage","Invalid Credentials, Please try again.");
        }
        if(logout!=null) {
            model.addAttribute("logoutMessage","Logout Successful!");
        }


        return "loginPage";
    }

}
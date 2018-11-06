package com.apap.tutorial8.controller;

import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial8.model.PasswordModel;
import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute @Valid UserRoleModel user, BindingResult result) {
		if (result.hasErrors()) {
			return "home";
		}
		userService.addUser(user);
		return "home";
	}
	
	@RequestMapping("/updatePassword")
	public String updatePassword() {
		return "update-password";
	}
	
	public boolean validatePassword(String password) {
		if (password.length()>=8 && Pattern.compile("[0-9]").matcher(password).find() &&  Pattern.compile("[a-zA-Z]").matcher(password).find())  {
			return true;
		}
		else {
			return false;
		}
	}
	
	@RequestMapping(value="/passwordSubmit",method=RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel pass, Model model,RedirectAttributes redir) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
		
		if (pass.getConPassword().equals(pass.getNewPassword())) {
			
			if (passwordEncoder.matches(pass.getOldPassword(), user.getPassword())) {
				if (validatePassword(pass.getNewPassword())) {
					userService.changePassword(user, pass.getNewPassword());
					message = "password berhasil diubah";
				}
				else {
					message = "password baru anda belum sesuai ketentuan: lebih dari 8 karakter, mengandung minimal 1 huruf dan 1 angka";
				}
				
			}
			else {
				message = "password lama anda salah";
			}
			
		}
		else {
			message = "password baru tidak sesuai";
		}
		
		
		ModelAndView modelAndView = new ModelAndView("redirect:/user/updatePassword");
		redir.addFlashAttribute("msg",message);
		return modelAndView;
	}
}

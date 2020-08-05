package softuni.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.model.binding.UserEditBindingModel;
import softuni.model.binding.UserRegisterBindingModel;
import softuni.model.service.UserServiceModel;
import softuni.model.view.UserAllViewModel;
import softuni.model.view.UserProfileViewModel;
import softuni.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersController(UserService userService, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/login")
    public ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }



    @GetMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("userRegisterBindingModel") UserRegisterBindingModel userRegisterBindingModel,
                                 BindingResult bindingResult, ModelAndView modelAndView) {
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerConfirm(@Valid @ModelAttribute("userRegisterBindingModel") UserRegisterBindingModel userRegisterBindingModel,
                                        BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelAndView modelAndView) {

        if (bindingResult.hasErrors() ||
                !userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())
                || userService.findByUsername(userRegisterBindingModel.getUsername()) != null) {

            if(userService.findByUsername(userRegisterBindingModel.getUsername()) != null){
                redirectAttributes.addFlashAttribute("userExist",true);
            }

            if(userService.findByEmail(userRegisterBindingModel.getEmail()) != null){
                redirectAttributes.addFlashAttribute("emailExist",true);
            }

            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            modelAndView.setViewName("redirect:register");

        } else {
            this.userService.register(this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class));
            modelAndView.setViewName("redirect:login");
        }
        return modelAndView;
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")

    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel userServiceModel = this.userService.findByUsername(principal.getName());
        UserProfileViewModel model = this.modelMapper.map(userServiceModel, UserProfileViewModel.class);
        modelAndView.addObject("model", model);

        modelAndView.setViewName("profile");

        return modelAndView;

    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserAllViewModel> users = this.userService.findAllUsers()
                .stream()
                .map(u -> {
                    UserAllViewModel user = this.modelMapper.map(u, UserAllViewModel.class);
                    Set<String> authorities = u.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
                    user.setAuthorities(authorities);

                    return user;
                })
                .collect(Collectors.toList());

        modelAndView.addObject("users", users);

        modelAndView.setViewName("all-users");
        return modelAndView;

    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable Long id,ModelAndView modelAndView) {
        this.userService.setUserRole(id, "user");

        modelAndView.setViewName("redirect:/users/all");

        return modelAndView;

    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable Long id,ModelAndView modelAndView) {
        this.userService.setUserRole(id, "moderator");

        modelAndView.setViewName("redirect:/users/all");

        return modelAndView;
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable Long id,ModelAndView modelAndView) {
        this.userService.setUserRole(id, "admin");

        modelAndView.setViewName("redirect:/users/all");

        return modelAndView;
    }


    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(@Valid @ModelAttribute("userEditBindingModel") UserEditBindingModel userEditBindingModel,BindingResult bindingResult,
                                    Principal principal, ModelAndView modelAndView) {

        modelAndView.setViewName("edit-profile");
        return modelAndView;


    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(@Valid @ModelAttribute("userEditBindingModel") UserEditBindingModel userEditBindingModel,BindingResult bindingResult
            ,RedirectAttributes redirectAttributes,ModelAndView modelAndView,Principal principal) {

        if (bindingResult.hasErrors() ||
                !this.bCryptPasswordEncoder.matches(userEditBindingModel.getOldPassword(),this.userService.findByUsername(principal.getName()).getPassword())
                || !userEditBindingModel.getPassword().equals(userEditBindingModel.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("userEditBindingModel", userEditBindingModel);
            modelAndView.setViewName("redirect:/users/edit");

        } else {

            userEditBindingModel.setUsername(principal.getName());


            this.userService.editUserProfile(this.modelMapper.map(userEditBindingModel, UserServiceModel.class));
            modelAndView.setViewName("redirect:/users/profile");
        }

        return modelAndView;

    }

}

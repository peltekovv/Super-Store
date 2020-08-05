package softuni.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.model.entity.Category;
import softuni.service.CategoryService;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final CategoryService categoryService;

    public HomeController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/")
    public String index (){
        return "index";
    }


    @GetMapping("/home")
    public ModelAndView home (Principal principal,ModelAndView modelAndView){
        List<Category> categories = this.categoryService.getAllCategories();
        modelAndView.addObject("categories",categories);
        modelAndView.addObject("user",principal.getName());
        modelAndView.setViewName("home");

        return modelAndView;
    }

}

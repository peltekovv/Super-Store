package softuni.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.model.binding.CategoryAddBindingModel;
import softuni.model.entity.Category;
import softuni.model.service.CategoryServiceModel;
import softuni.model.view.CategoryViewModel;
import softuni.model.view.ProductViewModel;
import softuni.service.CategoryService;
import softuni.service.ProductService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;


    public CategoryController(ProductService productService, CategoryService categoryService, ModelMapper modelMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategory(@Valid @ModelAttribute("categoryAddBindingModel") CategoryAddBindingModel categoryAddBindingModel, BindingResult bindingResult, ModelAndView modelAndView) {
        modelAndView.addObject("categoryAddBindingModel", categoryAddBindingModel);
        modelAndView.setViewName("add-category");

        return modelAndView;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategoryConfirm(@Valid
                                           @ModelAttribute("categoryAddBindingModel") CategoryAddBindingModel categoryAddBindingModel,
                                           BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("categoryAddBindingModel", categoryAddBindingModel);


            modelAndView.setViewName("redirect:/categories/add");
            return modelAndView;
        }

        CategoryServiceModel categoryServiceModel = this.modelMapper.map(categoryAddBindingModel, CategoryServiceModel.class);
        this.categoryService.addCategory(categoryServiceModel);

        modelAndView.setViewName("redirect:all");
        return modelAndView;

    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView allCategories(ModelAndView modelAndView) {
        List<CategoryViewModel> categories = this.categoryService.getAllCategories()
                .stream()
                .map(c -> this.modelMapper.map(c, CategoryViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("categories", categories);
        modelAndView.setViewName("all-categories");

        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteCategory(@PathVariable Long id, ModelAndView modelAndView) {
        this.categoryService.deleteCategoryById(id);
        modelAndView.setViewName("redirect:/categories/all");
        return modelAndView;
    }

    @GetMapping("/current/{id}")
    public ModelAndView getProductFromCurrentCategories(@PathVariable Long id, ModelAndView modelAndView) {
        CategoryServiceModel category = this.categoryService.findCategoryById(id);


        List<ProductViewModel> products = this.productService.findAllByCategory(category.getName())
                .stream()
                .map(c -> this.modelMapper.map(c, ProductViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("products", products);
        modelAndView.addObject("category",category);
        modelAndView.setViewName("current-products");

        return modelAndView;
    }

}


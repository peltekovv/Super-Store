package softuni.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.model.binding.ProductAddBindingModel;
import softuni.model.entity.Category;
import softuni.model.service.CategoryServiceModel;
import softuni.model.service.ProductServiceModel;
import softuni.model.view.ProductDetailViewModel;
import softuni.model.view.ProductViewModel;
import softuni.service.CategoryService;
import softuni.service.ProductService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public ProductController(ProductService productService, CategoryService categoryService, ModelMapper modelMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProduct(@Valid @ModelAttribute("productAddBindingModel") ProductAddBindingModel productAddBindingModel
            , BindingResult bindingResult, ModelAndView modelAndView) {
        modelAndView.addObject("categories",this.categoryService.getAllCategories());
        modelAndView.setViewName("add-product");
        return modelAndView;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProductConfirm(@Valid @ModelAttribute("productAddBindingModel") ProductAddBindingModel productAddBindingModel
            , BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelAndView modelAndView) {


        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("productAddBindingModel", productAddBindingModel);
            modelAndView.setViewName("redirect:/products/add");
            return modelAndView;
        } else {
            ProductServiceModel productServiceModel = this.modelMapper.map(productAddBindingModel, ProductServiceModel.class);

            productServiceModel.setCategory(this.modelMapper.map(
                    this.categoryService.findCategoryByName(productAddBindingModel.getCategory()), CategoryServiceModel.class));


            this.productService.createProduct(productServiceModel);
            modelAndView.setViewName("redirect:all");


            return modelAndView;
        }
    }
    @GetMapping("/all")
    public ModelAndView allProducts(ModelAndView modelAndView) {
        modelAndView.addObject("products", this.productService.findAllProducts()
                .stream()
                .map(p -> this.modelMapper.map(p, ProductViewModel.class))
                .collect(Collectors.toList()));
        modelAndView.setViewName("all-products");
        return modelAndView;

    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteProduct(@PathVariable Long id, ModelAndView modelAndView) {
        this.productService.deleteProduct(id);
        modelAndView.setViewName("redirect:/products/all");
        return modelAndView;
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView detailsProduct(@PathVariable Long id, ModelAndView modelAndView) {
        ProductDetailViewModel model = this.modelMapper.map(this.productService.findProductById(id), ProductDetailViewModel.class);

        modelAndView.addObject("category",model.getCategory());
        modelAndView.addObject("product", model);

        modelAndView.setViewName("product-details");
        return modelAndView;
    }
}

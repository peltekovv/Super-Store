package softuni.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.model.service.OrderProductServiceModel;
import softuni.model.service.OrderServiceModel;
import softuni.model.service.UserServiceModel;
import softuni.model.view.OrderProductViewModel;
import softuni.model.view.ProductDetailViewModel;
import softuni.model.view.ShoppingCart;
import softuni.service.OrderService;
import softuni.service.ProductService;
import softuni.service.UserService;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/carts")
public class CartController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    public CartController(ProductService productService, UserService userService, OrderService orderService, ModelMapper modelMapper) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addToCartConfirm(Long id, int quantity, HttpSession session, ModelAndView modelAndView) {
        ProductDetailViewModel product = this.modelMapper
                .map(this.productService.findProductById(id), ProductDetailViewModel.class);

        OrderProductViewModel orderProductViewModel = new OrderProductViewModel();
        orderProductViewModel.setProduct(product);
        orderProductViewModel.setPrice(product.getPrice());

        ShoppingCart cartItem = new ShoppingCart();
        cartItem.setProduct(orderProductViewModel);
        cartItem.setQuantity(quantity);

        var cart = this.retrieveCart(session);
        this.addItemToCart(cartItem, cart);

        modelAndView.setViewName("redirect:/home");

        return modelAndView;
    }

    @GetMapping("/details")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView cartDetails(ModelAndView modelAndView, HttpSession session) {
        var cart = this.retrieveCart(session);
        modelAndView.addObject("totalPrice", this.calcTotal(cart));


        modelAndView.setViewName("cart-details");

        return modelAndView;

    }

    @PostMapping("/remove-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView removeFromCartConfirm(Long id, HttpSession session, ModelAndView modelAndView) {
        this.removeItemFromCart(id, this.retrieveCart(session));


        modelAndView.setViewName("redirect:/carts/details");

        return modelAndView;
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView checkoutConfirm(HttpSession session, Principal principal, ModelAndView modelAndView) {

        var cart = this.retrieveCart(session);

        if (cart.size() == 0) {

        } else {


            OrderServiceModel orderServiceModel = this.prepareOrder(cart, principal.getName());


            this.orderService.createOrder(orderServiceModel);


            this.updateCart(session);
        }

        modelAndView.setViewName("redirect:/home");

        return modelAndView;

    }

    private void updateCart(HttpSession session) {
        session.setAttribute("shopping-cart", new LinkedList<>());
    }

    private List<ShoppingCart> retrieveCart(HttpSession session) {
        this.initCart(session);

        return (List<ShoppingCart>) session.getAttribute("shopping-cart");
    }

    private void initCart(HttpSession session) {
        if (session.getAttribute("shopping-cart") == null) {
            session.setAttribute("shopping-cart", new LinkedList<>());
        }
    }

    private void addItemToCart(ShoppingCart item, List<ShoppingCart> cart) {
        for (ShoppingCart shoppingCart : cart) {
            if (shoppingCart.getProduct().getProduct().getId().equals(item.getProduct().getProduct().getId())) {
                shoppingCart.setQuantity(shoppingCart.getQuantity() + item.getQuantity());
                return;
            }
        }

        cart.add(item);
    }

    private void removeItemFromCart(Long id, List<ShoppingCart> cart) {
        cart.removeIf(ci -> ci.getProduct().getProduct().getId().equals(id));
    }

    private BigDecimal calcTotal(List<ShoppingCart> cart) {
        BigDecimal result = new BigDecimal(0);
        for (ShoppingCart item : cart) {
            result = result.add(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        return result;
    }

    private OrderServiceModel prepareOrder(List<ShoppingCart> cart, String customer) {
        OrderServiceModel orderServiceModel = new OrderServiceModel();
        UserServiceModel byUsername = this.userService.findByUsername(customer);
        orderServiceModel.setCustomer(this.userService.findByUsername(customer));
        List<OrderProductServiceModel> products = new ArrayList<>();
        for (ShoppingCart item : cart) {
            OrderProductServiceModel productServiceModel = this.modelMapper.map(item.getProduct(), OrderProductServiceModel.class);

            for (int i = 0; i < item.getQuantity(); i++) {
                products.add(productServiceModel);
            }
        }

        orderServiceModel.setProducts(products);
        orderServiceModel.setTotalPrice(this.calcTotal(cart));

        return orderServiceModel;
    }
}

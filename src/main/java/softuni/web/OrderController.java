package softuni.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.model.view.OrderViewModel;
import softuni.service.OrderService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper mapper;

    public OrderController(OrderService orderService, ModelMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView getAllOrders(ModelAndView modelAndView) {

        List<OrderViewModel> orderViewModels = orderService.findAllOrders()
                .stream()
                .map(o -> mapper.map(o, OrderViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("orders", orderViewModels);

        modelAndView.setViewName("all-orders");
        return modelAndView;
    }

    @GetMapping("/all/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView allOrderDetails(@PathVariable Long id, ModelAndView modelAndView) {

        OrderViewModel orderViewModel = this.mapper.map(this.orderService.findOrderById(id), OrderViewModel.class);

        modelAndView.addObject("order", orderViewModel);

        modelAndView.setViewName("order-details");

        return modelAndView;
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")

    public ModelAndView getMyOrders(ModelAndView modelAndView, Principal principal) {

        List<OrderViewModel> orderViewModels = orderService.findOrdersByCustomer(principal.getName())
                .stream()
                .map(o -> mapper.map(o, OrderViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("orders", orderViewModels);

        modelAndView.setViewName("all-orders");

        return modelAndView;
    }

    @GetMapping("/my/details/{id}")
    public ModelAndView myOrderDetails(@PathVariable Long id, ModelAndView modelAndView) {
        OrderViewModel orderViewModel = this.mapper.map(this.orderService.findOrderById(id), OrderViewModel.class);

        modelAndView.addObject("order", orderViewModel);

        modelAndView.setViewName("order-details");


        return modelAndView;
    }

}

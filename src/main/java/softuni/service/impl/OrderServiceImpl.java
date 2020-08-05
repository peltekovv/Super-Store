package softuni.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.model.entity.Order;
import softuni.model.entity.OrderProduct;
import softuni.model.service.OrderProductServiceModel;
import softuni.model.service.OrderServiceModel;
import softuni.model.service.UserServiceModel;
import softuni.repository.OrderRepository;
import softuni.service.OrderService;
import softuni.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, UserService userService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderServiceModel createOrder(OrderServiceModel orderServiceModel) {
        orderServiceModel.setOrderedOn(LocalDateTime.now());

        Order map = this.modelMapper.map(orderServiceModel, Order.class);


        map.setProduct(prodTransfer(orderServiceModel.getProducts()));


        this.orderRepository.saveAndFlush(map);


        return orderServiceModel;
    }

    private List<OrderProduct> prodTransfer(List<OrderProductServiceModel> products) {
        List<OrderProduct> orderProducts = new ArrayList<>();

        for (OrderProductServiceModel product : products) {
            OrderProduct map = this.modelMapper.map(product, OrderProduct.class);
            map.getProduct().setId(product.getProduct().getId());
            orderProducts.add(map);
        }
        return orderProducts;
    }

    @Override
    public List<OrderServiceModel> findAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders
                .stream()
                .map(o -> this.modelMapper.map(o, OrderServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderServiceModel> findOrdersByCustomer(String username) {
        return this.orderRepository.findAllByCustomer_Username(username)
                .stream()
                .map(o -> modelMapper.map(o, OrderServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderServiceModel findOrderById(Long id) {
        Optional<Order> byId = this.orderRepository.findById(id);

        Set<OrderProduct> newSet = new LinkedHashSet<>();

        for (OrderProduct orderProduct : byId.get().getProduct()) {
            newSet.add(orderProduct);
        }

        List<OrderProduct> newList = new LinkedList<>();

        for (OrderProduct orderProduct : newSet) {
            newList.add(orderProduct);
        }

        byId.get().setProduct(newList);


        OrderServiceModel orderServiceModel = new OrderServiceModel();

        orderServiceModel.setOrderedOn(byId.get().getOrderedOn());
        orderServiceModel.setCustomer(this.modelMapper.map(byId.get().getCustomer(), UserServiceModel.class));
        orderServiceModel.setTotalPrice(byId.get().getTotalPrice());
        orderServiceModel.setId(byId.get().getId());
        orderServiceModel.setProducts(prodTran(byId.get().getProduct()));

        return orderServiceModel;
    }

    private List<OrderProductServiceModel> prodTran(List<OrderProduct> product) {
        List<OrderProductServiceModel> orderProducts = new LinkedList<>();

        for (OrderProduct product1 : product) {
            OrderProductServiceModel map = this.modelMapper.map(product1, OrderProductServiceModel.class);
            map.getProduct().setId(product1.getProduct().getId());
            orderProducts.add(map);
        }
        return orderProducts;
    }
}

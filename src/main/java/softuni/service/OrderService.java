package softuni.service;

import softuni.model.service.OrderServiceModel;

import java.util.List;

public interface OrderService {

    OrderServiceModel createOrder(OrderServiceModel orderServiceModel);

    List<OrderServiceModel> findAllOrders();

    List<OrderServiceModel> findOrdersByCustomer(String username);

    OrderServiceModel findOrderById(Long id);

}

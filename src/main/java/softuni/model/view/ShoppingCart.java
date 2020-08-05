package softuni.model.view;

import java.io.Serializable;

public class ShoppingCart implements Serializable {
    private OrderProductViewModel product;
    private int quantity;

    public ShoppingCart() {
    }

    public OrderProductViewModel getProduct() {
        return product;
    }

    public void setProduct(OrderProductViewModel product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

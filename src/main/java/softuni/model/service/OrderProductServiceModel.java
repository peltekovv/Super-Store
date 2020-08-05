package softuni.model.service;

import java.math.BigDecimal;

public class OrderProductServiceModel {
    private Long id;
    private ProductServiceModel product;
    private BigDecimal price;

    public OrderProductServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductServiceModel getProduct() {
        return product;
    }

    public void setProduct(ProductServiceModel product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

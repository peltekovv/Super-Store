package softuni.model.view;

import java.math.BigDecimal;

public class OrderProductViewModel {

    private Long id;
    private ProductDetailViewModel product;
    private BigDecimal price;

    public OrderProductViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductDetailViewModel getProduct() {
        return product;
    }

    public void setProduct(ProductDetailViewModel product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

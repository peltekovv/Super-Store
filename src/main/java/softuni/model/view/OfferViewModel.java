package softuni.model.view;

import java.math.BigDecimal;

public class OfferViewModel {

    private ProductDetailViewModel product;
    private BigDecimal price;

    public OfferViewModel() {
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

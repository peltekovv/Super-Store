package softuni.model.binding;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class ProductAddBindingModel {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;

    public ProductAddBindingModel() {
    }

    @Length(min = 3,max = 30, message = "Name must be between 3 and 30 characters.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 10, message = "Description should have atleast 10 characters.")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DecimalMin(value = "0",message = "Price can not be negative.")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @NotEmpty(message = "Category cannot be empty!")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

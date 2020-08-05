package softuni.model.service;

import softuni.model.entity.Category;

import java.math.BigDecimal;

public class ProductServiceModel {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private CategoryServiceModel category;

    public ProductServiceModel() {
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CategoryServiceModel getCategory() {
        return category;
    }

    public void setCategory(CategoryServiceModel category) {
        this.category = category;
    }
}

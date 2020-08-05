package softuni.service;

import softuni.model.service.ProductServiceModel;

import java.util.List;

public interface ProductService {
    ProductServiceModel createProduct(ProductServiceModel productServiceModel);

    List<ProductServiceModel> findAllProducts();

    ProductServiceModel findProductById(Long id);

    void deleteProduct(Long id);

    List<ProductServiceModel> findAllByCategory(String category);
}

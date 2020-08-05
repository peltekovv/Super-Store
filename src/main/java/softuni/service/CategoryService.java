package softuni.service;

import softuni.model.entity.Category;
import softuni.model.service.CategoryServiceModel;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel);

    CategoryServiceModel findCategoryById(Long id);

    void deleteCategoryById(Long id);

    CategoryServiceModel findCategoryByName(String category);

}

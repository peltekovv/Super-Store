package softuni.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.model.entity.Category;
import softuni.model.service.CategoryServiceModel;
import softuni.repository.CategoryRepository;
import softuni.service.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }

    @Override
    public CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel) {
        Category category = this.modelMapper.map(categoryServiceModel,Category.class);
        this.categoryRepository.saveAndFlush(category);
        return categoryServiceModel;
    }

    @Override
    public CategoryServiceModel findCategoryById(Long id) {
        Optional<Category> category = this.categoryRepository.findById(id);

        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setId(category.get().getId());
        categoryServiceModel.setName(category.get().getName());


        return categoryServiceModel;
    }

    @Override
    public void deleteCategoryById(Long id) {
        this.categoryRepository.deleteById(id);
    }

    @Override
    public CategoryServiceModel findCategoryByName(String category) {
        return this.modelMapper.map(this.categoryRepository.findFirstByName(category),CategoryServiceModel.class);
    }


}

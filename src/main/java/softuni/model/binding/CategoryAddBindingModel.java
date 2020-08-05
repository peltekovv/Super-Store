package softuni.model.binding;

import org.hibernate.validator.constraints.Length;

public class CategoryAddBindingModel {
    private String name;

    public CategoryAddBindingModel() {
    }

    @Length(min = 3, max = 20 , message = "Name length must be between 3 and 20 characters (inclusive 3 and 20).")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

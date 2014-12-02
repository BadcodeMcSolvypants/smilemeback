package smilemeback.com.smilememack.storage;

/**
 * Category name class.
 */
public class CategoryName {
    protected String name;

    public CategoryName(String name) {
        this.name = name;
    }

    public CategoryName(CategoryName catname) {
        this.name = catname.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}

package inn.cafe.service;

import inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

    ResponseEntity<List<Category>> getAllCategory(String categoryType);

    ResponseEntity<String> updateCategory(Map<String, String> requestMap);

}

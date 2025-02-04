package inn.cafe.rest;

import inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/category")
public interface CategoryRest {

    @PostMapping("/add")
    ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap);

    @GetMapping("/get")
    ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String categoryType);

}

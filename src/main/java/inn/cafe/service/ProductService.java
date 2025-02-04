package inn.cafe.service;

import inn.cafe.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProducts();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Integer id);

}

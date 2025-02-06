package inn.cafe.rest;

import inn.cafe.POJO.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/bill")
public interface BillRest {

    @PostMapping("/generate-bill")
    ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap);

    @GetMapping("/get-bills")
    ResponseEntity<List<Bill>> getBills();

    @PostMapping("/get-pdf")
    ResponseEntity<byte[]> getPDF(@RequestBody Map<String, Object> requestMap);
}

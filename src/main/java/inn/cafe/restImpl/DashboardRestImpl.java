package inn.cafe.restImpl;

import inn.cafe.rest.DashboardRest;
import inn.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestImpl implements DashboardRest {

    @Autowired
    private DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getDetails() {
        return dashboardService.getDetails();
    }
}

package POHandling.controller;

import POHandling.models.ProductOrder;
import POHandling.repository.ProductOrderRepository;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;


@Controller
@RequestMapping("/api/rules")
public class RuleController {
    @Autowired
    private ProductOrderRepository productOrderRepository;
    private final KieContainer kieContainer;

    public RuleController() {
        KieServices kieServices = KieServices.Factory.get();
        kieContainer = kieServices.getKieClasspathContainer();
    }

    @PostMapping("/{orderId}/workingDayRule")
    public ResponseEntity<?> executeWorkingDayRule(@PathVariable Integer orderId, @RequestParam Set<String> workingDays,  @RequestParam Set<String> holidays) {
        Optional<ProductOrder> productOrder = productOrderRepository.findById(orderId);
        if (productOrder.isPresent()){
            KieSession kieSession = kieContainer.newKieSession("ksession-rules");
            kieSession.setGlobal("workingDays", workingDays);
            kieSession.setGlobal("holidays", holidays);
            kieSession.insert(productOrder.get());
            kieSession.fireAllRules();
            kieSession.dispose();
            productOrderRepository.save(productOrder.get());
            return new ResponseEntity<>(productOrder, HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Product Order not found", HttpStatus.BAD_REQUEST);
        }
    }
}

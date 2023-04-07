package POHandling.controller;

import POHandling.models.Address;
import POHandling.models.Client;
import POHandling.models.Item;
import POHandling.models.ProductOrder;
import POHandling.repository.AddressRepository;
import POHandling.repository.ClientRepository;
import POHandling.repository.ItemRepository;
import POHandling.repository.ProductOrderRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping(path="/productOrder")
public class ProductOrderController {
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ItemRepository itemRepository;
    @PostMapping(path = "/add")
    public ResponseEntity<?> addProductOrder(@RequestBody ProductOrder productOrder,
                                             @RequestParam(required = false) Integer clientId,
                                             @RequestParam(required = false) Integer deliveryAddressId,
                                             @RequestParam(required = false) List<Integer> itemIds,
                                             @RequestBody(required = false) Map<Integer, Integer> itemQuantities) {

        Date currentDate = new Date();
        productOrder.setOrderDate(currentDate);
        productOrder.setOrderProcessDate(productOrder.getOrderProcessDate());

        if (clientId != null) {
            Optional<Client> clientOptional = clientRepository.findById(clientId);
            if (clientOptional.isPresent()) {
                productOrder.setClient(clientOptional.get());
            } else {
                return new ResponseEntity<>("Client not found", HttpStatus.BAD_REQUEST);
            }
        }

        if (deliveryAddressId != null) {
            Optional<Address> addressOptional = addressRepository.findById(deliveryAddressId);
            if (addressOptional.isPresent()) {
                productOrder.setDeliveryAddress(addressOptional.get());
            } else {
                return new ResponseEntity<>("Delivery Address not found", HttpStatus.BAD_REQUEST);
            }
        }

        if (itemIds != null) {
            List<Item> items = new ArrayList<>();
            for (Integer itemId : itemIds) {
                Optional<Item> itemOptional = itemRepository.findById(itemId);
                if (itemOptional.isPresent()) {
                    Item item = itemOptional.get();
                    item.setProductOrder(productOrder); // Set the ProductOrder field in each Item object

                    if (itemQuantities != null && itemQuantities.containsKey(itemId)) {
                        int quantity = itemQuantities.get(itemId);
                        productOrder.getItemsQuantity().put(item, quantity); // Set the quantity for each item in the ProductOrder
                    }

                    items.add(item);
                } else {
                    return new ResponseEntity<>("Item not found", HttpStatus.BAD_REQUEST);
                }
            }
            productOrder.setItems(items);
        }

        ProductOrder savedProductOrder = productOrderRepository.save(productOrder);
        return new ResponseEntity<>(savedProductOrder, HttpStatus.OK);
    }


    @GetMapping(path = "/all")
    public ResponseEntity<Iterable<ProductOrder>> getAllProductOrders() {
        return new ResponseEntity<>(productOrderRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductOrder> getProductOrderById(@PathVariable Integer id) {
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findById(id);
        if (productOrderOptional.isPresent()) {
            return new ResponseEntity<>(productOrderOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{productOrderId}")
    public ResponseEntity<?> updateProductOrder(@PathVariable Integer productOrderId,
                                                     @RequestBody ProductOrder updatedProductOrder,
                                                     @RequestParam(required = false) Integer clientId,
                                                     @RequestParam(required = false) Integer deliveryAddressId) {
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findById(productOrderId);
        if (!productOrderOptional.isPresent()) {
            return new ResponseEntity<>("ProductOrder not found", HttpStatus.NOT_FOUND);
        }

        ProductOrder productOrder = productOrderOptional.get();
        productOrder.setOrderDate(updatedProductOrder.getOrderDate());
        productOrder.setOrderProcessDate(updatedProductOrder.getOrderProcessDate());

        if (clientId != null) {
            Optional<Client> clientOptional = clientRepository.findById(clientId);
            System.out.println(clientOptional);
            if (clientOptional.isPresent()) {
                productOrder.setClient(clientOptional.get());
            } else {
                return new ResponseEntity<>("Client not found", HttpStatus.BAD_REQUEST);
            }
        }

        if (deliveryAddressId != null) {
            Optional<Address> addressOptional = addressRepository.findById(deliveryAddressId);
            if (addressOptional.isPresent()) {
                productOrder.setDeliveryAddress(addressOptional.get());
            } else {
                return new ResponseEntity<>("Delivery Address not found", HttpStatus.BAD_REQUEST);
            }
        }

        if (updatedProductOrder.getItems() != null) {
            productOrder.setItems(updatedProductOrder.getItems());
        }

        ProductOrder savedProductOrder = productOrderRepository.save(productOrder);
        return new ResponseEntity<>(savedProductOrder, HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProductOrder(@PathVariable Integer id) {
        Optional<ProductOrder> productOrderOptional = productOrderRepository.findById(id);
        if (productOrderOptional.isPresent()) {
            productOrderRepository.deleteById(id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ProductOrder not found", HttpStatus.NOT_FOUND);
        }
    }
}

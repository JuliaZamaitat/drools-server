package POHandling.controller;

import POHandling.models.Item;
import POHandling.models.ProductOrder;
import POHandling.repository.ItemRepository;
import POHandling.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping(path = "/item")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @PostMapping(path = "/add")
    public ResponseEntity<Item> addNewItem(@RequestBody Item item, @RequestParam(required = false) Integer productOrderId) {
        if (productOrderId != null) {
            ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProductOrder not found"));
            item.setProductOrder(productOrder);
        }
        Item savedItem = itemRepository.save(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Iterable<Item>> getAllItems() {
        Iterable<Item> items = itemRepository.findAll();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Integer id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            return new ResponseEntity<>(item, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Integer itemId, @RequestBody Item updatedItem, @RequestParam(required = false) Integer productOrderId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setName(updatedItem.getName());
            item.setPrice(updatedItem.getPrice());
            item.setQuantity(updatedItem.getQuantity());

            if (productOrderId != null) {
                ProductOrder productOrder = productOrderRepository.findById(productOrderId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ProductOrder not found"));
                item.setProductOrder(productOrder);
            } else {
                item.setProductOrder(null);
            }

            Item savedItem = itemRepository.save(item);
            return new ResponseEntity<>(savedItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path="/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Integer id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            itemRepository.deleteById(id);
            return new ResponseEntity<>("Item deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
        }
    }
}

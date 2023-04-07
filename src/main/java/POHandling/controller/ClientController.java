package POHandling.controller;
import POHandling.models.Address;
import POHandling.models.Client;
import POHandling.repository.AddressRepository;
import POHandling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "/client")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AddressRepository addressRepository;


    @PostMapping(path = "/test")
    public ResponseEntity<?> addNewClient(@RequestBody Client client) {
//        Address billingAddress = client.getBillingAddress();
//        if (billingAddress != null) {
//            addressRepository.save(billingAddress);
//        }
//        Client savedClient = clientRepository.save(client);
//        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }


    @GetMapping(path = "/all")
    public ResponseEntity<Iterable<Client>> getAllClients() {
        Iterable<Client> clients = clientRepository.findAll();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Integer id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.getProductOrders().size(); // Access the productOrders field to force JPA to load the related ProductOrder objects
            return new ResponseEntity<>(client, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Integer id, @RequestBody Client updatedClient) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (!clientOptional.isPresent()) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
        }
        Client client = clientOptional.get();
        client.setFirstName(updatedClient.getFirstName());
        client.setLastName(updatedClient.getLastName());
        client.setEmail(updatedClient.getEmail());

        Address updatedBillingAddress = updatedClient.getBillingAddress();
        if (updatedBillingAddress != null) {
            addressRepository.save(updatedBillingAddress);
        }
        client.setBillingAddress(updatedBillingAddress);

        Client savedClient = clientRepository.save(client);
        return new ResponseEntity<>(savedClient, HttpStatus.OK);
    }


    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Integer id) {
        clientRepository.deleteById(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}

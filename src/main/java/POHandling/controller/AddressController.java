package POHandling.controller;

import POHandling.models.Address;
import POHandling.repository.AddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "/address")
public class AddressController {
    @Autowired
    private AddressRepository addressRepository;

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewAddress(@RequestBody Address address) {
        addressRepository.save(address);
        return new ResponseEntity<>("Address added successfully", HttpStatus.CREATED);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Iterable<Address>> getAllAddresses() {
        Iterable<Address> addresses = addressRepository.findAll();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Integer id) {
        Optional<Address> addressOptional = addressRepository.findById(id);
        if (addressOptional.isPresent()) {
            Address address = addressOptional.get();
            return new ResponseEntity<>(address, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Address not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(path = "/update/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer addressId,
                                                @RequestBody Address updatedAddress) {
        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (!addressOptional.isPresent()) {
            return new ResponseEntity<>("Address not found", HttpStatus.NOT_FOUND);
        }

        Address address = addressOptional.get();
        address.setCity(updatedAddress.getCity());
        address.setCountry(updatedAddress.getCountry());
        address.setStreet(updatedAddress.getStreet());
        address.setHouseNumber(updatedAddress.getHouseNumber());
        address.setPostalCode(updatedAddress.getPostalCode());
        address.setAdditionalInformation(updatedAddress.getAdditionalInformation());
        addressRepository.save(address);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer addressId) {
        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (!addressOptional.isPresent()) {
            return new ResponseEntity<>("Address not found", HttpStatus.NOT_FOUND);
        }

        addressRepository.delete(addressOptional.get());
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.OK);
    }
}

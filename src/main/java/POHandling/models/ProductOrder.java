package POHandling.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class ProductOrder {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private Client client;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_address_id")
    private Address deliveryAddress;
    @OneToMany(mappedBy = "productOrder")
    private List<Item> items;
    private Date orderDate;
    private Date orderProcessDate;

    public Date getOrderProcessDate() {
        return orderProcessDate;
    }

    public void setOrderProcessDate(Date orderProcessDate) {
        this.orderProcessDate = orderProcessDate;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}


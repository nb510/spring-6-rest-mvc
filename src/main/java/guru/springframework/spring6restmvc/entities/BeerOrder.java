package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
public class BeerOrder {

    public BeerOrder(UUID id, Integer version, LocalDateTime createdDate, LocalDateTime lastModifiedDate,
                     Customer customer, Set<BeerOrderLine> beerOrderLines, String customerRef, BigDecimal paymentAmount,
                     BeerOrderShipment beerOrderShipment) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.customer = customer;
        this.setBeerOrderLines(beerOrderLines);
        this.customerRef = customerRef;
        this.paymentAmount = paymentAmount;
        this.setBeerOrderShipment(beerOrderShipment);
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @JdbcTypeCode(Types.CHAR)
    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.PERSIST)
    private Set<BeerOrderLine> beerOrderLines;

    private String customerRef;

    @Column(precision = 19, scale = 2)
    private BigDecimal paymentAmount;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "beer_order_shipment_id", unique = true)
    private BeerOrderShipment beerOrderShipment;

    public void setBeerOrderShipment(BeerOrderShipment beerOrderShipment) {
        this.beerOrderShipment = beerOrderShipment;
        if (beerOrderShipment != null) {
            beerOrderShipment.setBeerOrder(this);
        }
    }

    public void setBeerOrderLines(Set<BeerOrderLine> beerOrderLines) {
        if (beerOrderLines != null) {
            beerOrderLines.forEach(line -> line.setBeerOrder(this));
        }
        this.beerOrderLines = beerOrderLines;
    }
}

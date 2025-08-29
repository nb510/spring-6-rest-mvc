package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

    @EntityGraph(attributePaths = {"beerOrderShipment", "beerOrderLines", "beerOrderLines.beer"})
    @Query("select o from BeerOrder o")
    Page<BeerOrder> findAllWithShipment(Pageable pageable);

    @EntityGraph(attributePaths = {"beerOrderShipment", "beerOrderLines", "beerOrderLines.beer"})
    @Query("select distinct o from BeerOrder o where o.id = :id")
    Optional<BeerOrder> findByIdWithShipmentAndOrderLines(@Param("id") UUID id);
}

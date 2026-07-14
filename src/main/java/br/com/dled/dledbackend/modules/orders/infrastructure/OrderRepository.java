package br.com.dled.dledbackend.modules.orders.infrastructure;

import br.com.dled.dledbackend.modules.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select distinct o from Order o join fetch o.company left join fetch o.products p order by o.id asc, p.id asc")
    List<Order> findAllWithCompanyAndProducts();

    @Query("select distinct o from Order o join fetch o.company left join fetch o.products where o.id = :orderId")
    Optional<Order> findByIdWithCompanyAndProducts(Long orderId);
}

package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Custom query methods can be added here if needed
    // For example, to find a cart by user ID or to delete a cart by user ID

}

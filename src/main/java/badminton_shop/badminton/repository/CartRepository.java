package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.Cart;
import badminton_shop.badminton.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}

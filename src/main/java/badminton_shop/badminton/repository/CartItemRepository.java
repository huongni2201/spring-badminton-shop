package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteByCartId(Long cardId);
}

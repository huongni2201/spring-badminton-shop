package badminton_shop.badminton.repository;

import badminton_shop.badminton.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

}

package enterpriseapplication.productservice.repository;

import enterpriseapplication.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

package ssafy.StackFlow.Domain.RT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssafy.StackFlow.Domain.product.entity.Product;
import ssafy.StackFlow.Domain.RT.entity.RT;

import java.util.List;

@Repository
public interface RtRepository extends JpaRepository<RT, Long>{

        List<Product> findByProdCodeContaining(String keyword);
    }


package softuni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.model.entity.Offer;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer,Long> {

    Optional<Offer> findByProduct_Id(Long id);
}

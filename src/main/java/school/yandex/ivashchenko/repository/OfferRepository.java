package school.yandex.ivashchenko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.yandex.ivashchenko.entity.Category;
import school.yandex.ivashchenko.entity.Offer;

import java.util.ArrayList;

public interface OfferRepository extends JpaRepository<Offer, String> {
    @Transactional
    @Query(value = "select parent_id from offer where id = :id", nativeQuery = true)
    String selectParentId(@Param("id") String id);

    Offer findEntityById(String id);

    ArrayList<Offer> findAllByCategory(Category category);
}
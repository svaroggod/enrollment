package school.yandex.ivashchenko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import school.yandex.ivashchenko.entity.Category;

import java.util.ArrayList;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Modifying
    @Transactional
    @Query(value = "update category set price = :price where id = :id", nativeQuery = true)
    int updateCategoryPrice(@Param("price") int price, @Param("id") String parentId);

    @Transactional
    @Query(value = "select parent_id from category where id = :id", nativeQuery = true)
    String selectParentId(@Param("id") String id);

    @Transactional
    @Query(value = "select price from category c where id = :id", nativeQuery = true)
    int selectPrice(@Param("id") String id);

    @Transactional
    @Query(value = "select id from category c where parent_id = :parentId", nativeQuery = true)
    ArrayList<String> selectIdByParentId(@Param("parentId") String parentId);

    Category findEntityById(String id);

    ArrayList<Category> findAllByParentId(String parentId);
}
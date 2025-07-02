package newblogproject.example.newproject.repo;

import newblogproject.example.newproject.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface BlogRepo extends JpaRepository<Blog,Integer> {
//    @Query("SELECT p from Blog p WHERE "+
//            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
//   public List<Blog> searchProducts(String keyword);
    @Query("SELECT p from Blog p WHERE "+
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
List<Blog> searchByKeyword(@RequestParam("keyword") String keyword);

}

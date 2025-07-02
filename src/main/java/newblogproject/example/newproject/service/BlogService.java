package newblogproject.example.newproject.service;

import newblogproject.example.newproject.models.Blog;
import newblogproject.example.newproject.repo.BlogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BlogService {
    @Autowired
BlogRepo repo;


    public Blog createpost(Blog blogo, MultipartFile imageFile) throws IOException {
        blogo.setImageData(imageFile.getBytes());
        blogo.setImageType(imageFile.getContentType());
        blogo.setImageName(imageFile.getOriginalFilename());
        return repo.save(blogo);
    }

    public List<Blog> getposts() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }


    public Blog updatepostbyid(int editingId,Blog blogo,MultipartFile imageFile) throws IOException {
//      Blog blog1=repo.findById(editingId).orElse(null);
        blogo.setImageData(imageFile.getBytes());
        blogo.setImageType(imageFile.getContentType());
        blogo.setImageName(imageFile.getOriginalFilename());
      return repo.save(blogo);
    }


    public void deletepost(int id, Blog blogo) {
        repo.delete(blogo);
    }

//    public List<Blog> searchproduct(String keyword) {
//        return repo.searchProducts(keyword);
//    }
public List<Blog> searchByKeyword(String keyword) {
    return repo.searchByKeyword("%" + keyword + "%");
}

}

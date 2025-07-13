package newblogproject.example.newproject.service;

import jakarta.transaction.Transactional;
import newblogproject.example.newproject.models.Blog;
import newblogproject.example.newproject.models.Users;
import newblogproject.example.newproject.repo.BlogRepo;
import newblogproject.example.newproject.repo.UserRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class BlogService {
    @Autowired
BlogRepo repo;
    @Autowired
    UserRepo userRepo;


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


public List<Blog> searchByKeyword(String keyword) {
    return repo.searchByKeyword("%" + keyword + "%");
}

    @Transactional
    public void likePost(String email, int blogId) {
        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Blog blog = repo.findById(blogId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found"));

        Hibernate.initialize(user.getLikedBlogs()); // ensure loaded

        boolean alreadyLiked = user.getLikedBlogs().stream()
                .anyMatch(b -> b.getId()==(blogId));

        if (alreadyLiked) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Blog already liked");

        }

        user.getLikedBlogs().add(blog);
        userRepo.save(user);
    }


    @Transactional
    public void unlikePost(String email, int blogId) {
        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Blog blog = repo.findById(blogId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog not found"));

        Hibernate.initialize(user.getLikedBlogs());

        boolean wasLiked = user.getLikedBlogs().removeIf(b -> b.getId()==(blogId));

        if (!wasLiked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blog was not liked by this user");
        }

        userRepo.save(user);
    }



    public int countLikes(int postId) {
        return userRepo.countByLikedBlogs_Id(postId);
    }

}

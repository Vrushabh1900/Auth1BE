package newblogproject.example.newproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import newblogproject.example.newproject.models.Blog;
import newblogproject.example.newproject.repo.BlogRepo;
import newblogproject.example.newproject.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class BlogController {
    @Autowired
    BlogRepo repo;
@Autowired
    BlogService bs;

@GetMapping("/")
public String first(HttpServletRequest req)
{
    return "hello"+ req.getSession().getId();
}

@GetMapping("/csrfshi")
public CsrfToken gettoken(HttpServletRequest request)
{
return (CsrfToken) request.getAttribute("_csrf");
}

    @PostMapping("/posts")
    public ResponseEntity<?> showblogs(@RequestPart Blog blogo, @RequestPart MultipartFile imagefile)
    {
        try
        { Blog blogo1= bs.createpost(blogo,imagefile);
return new ResponseEntity<>(blogo1, HttpStatus.OK);}
        catch(Exception e)
        {
return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping ("/posts")
    public ResponseEntity<List<Blog>> showblogs()
    {
        return new ResponseEntity<>(bs.getposts(), HttpStatus.OK);

    }
    @GetMapping("/posts/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        Blog blog = repo.findById(id).orElse(null);
        if (blog == null || blog.getImageData() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf(blog.getImageType())); // e.g., "image/jpeg"
//        return new ResponseEntity<>(blog.getImageData(), headers, HttpStatus.OK);

//byte[] nigger= blog.getImageData();
//        return ResponseEntity.ok()
//                .contentType(MediaType.valueOf(blog.getImageType()))
//                .body(blog.getImageData());

        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.valueOf(blog.getImageType()));
        return new ResponseEntity<>(blog.getImageData(),headers,HttpStatus.OK);

    }

    @PostMapping("/posts/{editingId}")
    public ResponseEntity<String> updateproduct(@PathVariable int editingId,@RequestPart Blog blogo, @RequestPart MultipartFile imagefile)
    { Blog blog1=null;
        blog1=repo.findById(editingId).orElse(null);
          try
            { blog1=bs.updatepostbyid(editingId,blogo,imagefile);
        return new ResponseEntity<>("Updated", HttpStatus.OK);}
catch(Exception e)
            {return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    }
        @DeleteMapping("/posts/{id}")
        public ResponseEntity<?> deleteposts(@PathVariable int id)
        {
Blog blogo=repo.findById(id).orElse(null);
            if(blogo!=null)
            { bs.deletepost(id,blogo);
                return new ResponseEntity<>("Deleted", HttpStatus.OK);}
            else
            {return new ResponseEntity<>("Not deleted you nigger",HttpStatus.BAD_REQUEST);
        }



}
//@GetMapping("/blogs/search")
//    public ResponseEntity<?> search(@RequestParam String keyword)
//{
//    List<Blog> blogo=bs.searchproduct(keyword);
//    return new ResponseEntity<>(blogo,HttpStatus.OK);
//}
@GetMapping("/posts/search")
public List<Blog> searchPosts(@RequestParam("keyword") String keyword) {
    return bs.searchByKeyword(keyword);
}


}


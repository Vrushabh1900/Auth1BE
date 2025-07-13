package newblogproject.example.newproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore; // ✅ Prevents infinite recursion in REST JSON

import lombok.EqualsAndHashCode;

import lombok.ToString;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String content;
    private String author;
    private byte[] ImageData;
    private String ImageType;
    private String ImageName;

    @JsonIgnore // if you expose this via REST
    @ToString.Exclude // if you use Lombok
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "likedBlogs")
    private Set<Users> likedBy = new HashSet<>();
}

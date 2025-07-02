package newblogproject.example.newproject.repo;


import newblogproject.example.newproject.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Integer> {

    public Optional<Users> findByEmail(String username);
    public boolean existsByEmail(String email);
    public Optional<Users> findByPhonenumber(String phonenumber);
}

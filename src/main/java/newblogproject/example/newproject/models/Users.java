package newblogproject.example.newproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private String password;
    @Column(unique = true)
    private String phonenumber;
    private String verifyOtp;
    private long verifyOtpExpireAt;
    private String resetOtp;
    private Long resetOtpExpireAt;
    private Boolean isAccountVerified = false;
    private Boolean is2FAEnabled = false;
    private String twoFAMethod; // "EMAIL" or "PHONE"
    private LocalDateTime lastLoginAt;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

}

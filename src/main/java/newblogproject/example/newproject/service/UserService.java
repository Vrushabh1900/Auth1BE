package newblogproject.example.newproject.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import newblogproject.example.newproject.IO.ProfileRequest;
import newblogproject.example.newproject.models.Users;
import newblogproject.example.newproject.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    @Autowired
    UserRepo repo;
    @Autowired
    JWTservice jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
@Autowired
    EmailService es;

    public String createuser(@Valid ProfileRequest profileRequest) {
        Users user=converttoUsers(profileRequest);
        repo.save(user);
        if(!repo.existsByEmail(user.getEmail()))
        {  repo.save(user);
            return "New user is saved";
            }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
    }

    private Users converttoUsers(ProfileRequest request) {
        return Users.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();

    }

    public void resetPassword(String email,  String otp, String npassword) {
        Users user= repo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Email not found:"+email));
        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP Expired");
        }

        if(user.getPassword().equals(npassword)){
            throw new RuntimeException("New password cant be old password:");
        }

        user.setPassword(passwordEncoder.encode(npassword));
        user.setResetOtp(null);
        user.setResetOtpExpireAt(0L);

       repo.save(user);
    }

    public void sendresetotp(String email) {
        Users user=repo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("email not found"+email));
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);
        user.setResetOtp(otp);
        user.setResetOtpExpireAt(expiryTime);
        repo.save(user);
        try{
            es.sendResetOtpEmail(user.getEmail(), otp);
        } catch(Exception ex) {
            throw new RuntimeException("Unable to send email");
        }

    }


    public void sendOtp(String method, String contact) {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));// generate 6-digit OTP
        Optional<Users> userOpt;

        if ("EMAIL".equalsIgnoreCase(method)) {
            userOpt = repo.findByEmail(contact);
        } else if ("PHONE".equalsIgnoreCase(method)) {
            userOpt = repo.findByPhonenumber(contact);
        } else {
            throw new RuntimeException("Invalid 2FA method");
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with given contact: " + contact);
        }

        Users user = userOpt.get();
        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(System.currentTimeMillis() + 5 * 60 * 1000); // 5 min
        repo.save(user);

        // Send OTP
        if ("EMAIL".equalsIgnoreCase(method)) {
            es.sendEmailVerificationOtp(user.getEmail(), otp);
        } else {
            es.sendPhoneVerificationOtp(user.getPhonenumber(), otp); // even Twilio can be called from this
        }
    }


    public boolean verifyOtp(String method, String contact, String otp) {
        Optional<Users> userOpt;

        if ("EMAIL".equalsIgnoreCase(method)) {
            userOpt = repo.findByEmail(contact);
        } else if ("PHONE".equalsIgnoreCase(method)) {
            userOpt = repo.findByPhonenumber(contact);
        } else {
            throw new RuntimeException("Invalid method");
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Users user = userOpt.get();

        if (user.getVerifyOtp() == null ||
                !user.getVerifyOtp().equals(otp) ||
                user.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            return false;
        }

        user.setIsAccountVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(0L);
        repo.save(user);
        return true;
    }

}

package newblogproject.example.newproject.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Getter;
import newblogproject.example.newproject.IO.AuthRequest;
import newblogproject.example.newproject.IO.ProfileRequest;
import newblogproject.example.newproject.IO.ResetOtpRequest;
import newblogproject.example.newproject.models.Users;
import newblogproject.example.newproject.repo.UserRepo;
import newblogproject.example.newproject.service.JWTservice;
import newblogproject.example.newproject.service.MyUserDetailsService;
import newblogproject.example.newproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService service;
    @Autowired
    UserRepo repo;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    BCryptPasswordEncoder SHA;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    JWTservice jwTservice;
    @Autowired
    ResetOtpRequest resetOtpRequest;
    @PostMapping("/register")
    public ResponseEntity<?> registeruser(@Valid @RequestBody ProfileRequest profileRequest)
    {
try{
    return new ResponseEntity<>(service.createuser(profileRequest),HttpStatus.OK);
} catch (Exception e) {
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
}

    }
//
//    @GetMapping("/checkuser")
//    public ResponseEntity<List<Users>> givsuser()
//    {
//
//    }

@PostMapping("/login")
public ResponseEntity<?> loginpage(@RequestBody AuthRequest authRequest)
{
    try {
        authenticate(authRequest.getEmail(),authRequest.getPassword());
        UserDetails userDetails=myUserDetailsService.loadUserByUsername(authRequest.getEmail());

        String JwtToken=jwTservice.generateToken(userDetails.getUsername());
        ResponseCookie RC=ResponseCookie.from("jwt",JwtToken)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, RC.toString())
                .body("");
    }

    catch(BadCredentialsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Email or password is incorrect");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    } catch(DisabledException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Account is disabled");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }catch(Exception ex) {

        ex.printStackTrace(); // Add this
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Authentication failed: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

}

    private void authenticate(String email,String password)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

    @PostMapping("/send-reset-otp")
    public ResponseEntity<?> sendresetotp(@CurrentSecurityContext(expression ="authentication?.name")String email)
    {
        try
        {
           service.sendresetotp(email);
            return new ResponseEntity<>("email is sent",HttpStatus.OK);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetOtpRequest resetOtpRequest)
    {
        try
        {
           service.resetPassword(resetOtpRequest.getEmail(),resetOtpRequest.getOtp(),resetOtpRequest.getNpassword());
            return new ResponseEntity<>("Password was rest",HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/2fa/send")
    public ResponseEntity<?> send2FAOtp(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("contact"); // can be email or phone
        String method = request.get("method"); // "EMAIL" or "PHONE"

        try {
            service.sendOtp(method, emailOrPhone);
            return ResponseEntity.ok("OTP sent via " + method);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/2fa/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String contact = request.get("contact");
        String method = request.get("method"); // "EMAIL" or "PHONE"
        String otp = request.get("otp");

        try {
            boolean valid = service.verifyOtp(method, contact, otp);
            if (valid) {
                return ResponseEntity.ok("OTP verified");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}







package com.ccsd.KAretail.Users;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/Users")
public class UserController {

    @Autowired
    private UserServices userService;
    // private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public User addUser(@RequestBody User user) {
        
        return userService.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        User user = userService.findByEmail(email);
        if (userService.findByEmail(user.getEmail()) != null) {
            if (password.equals(user.getPassword()) && user != null) {
                // Store user info in session
                session.setAttribute("email", user.getEmail());
                session.setAttribute("role", user.getRole());
                String role = user.getRole();

                return ResponseEntity.ok(role);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email != null) {
            User user = userService.findByEmail(email);

            if (user != null) {
                Map<String, Object> response = Map.of(
                    "user", user,
                    "role", session.getAttribute("role")
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized access"));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();  // Clear session data
        return ResponseEntity.ok("Logout successful");
    }

    @PutMapping("/email/{email}")
    public ResponseEntity<User> updateUserByEmail(@PathVariable String email, @RequestBody User userDetails) {
        User updatedUser = userService.updateUserByEmail(email, userDetails);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    // @PutMapping("/email/{email}")
    // public ResponseEntity<User> updateUserByEmail(@PathVariable String email, @RequestBody User userDetails) {
    //    // User updatedUser = userService.updateUserByEmail(email, userDetails);
    //     if (updatedUser != null) {
    //         return ResponseEntity.ok(updatedUser);
    //     }
    //     return ResponseEntity.notFound().build();
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //Login-------------------------------------------------

    // @GetMapping("/dashboard")
    // public ResponseEntity<String> dashboard(HttpSession session) {
    //     if (session.getAttribute("userId") == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized. Please log in.");
    //     }

    //     // int role = (int) session.getAttribute("role");
    //     String role = (String) session.getAttribute("role");

    //     if(role.equalsIgnoreCase("Customer")){
    //         return ResponseEntity.ok("Welcome Customer");
    //     }else if(role.equalsIgnoreCase("Staff")){
    //         return ResponseEntity.ok("Welcome Staff");
    //     }else{
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    //     }
    // }
    


    // //Forgot Password---------------------------------------
    // @PostMapping("/forgot-password")
    // public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
    //     String email = request.get("email");
    //     User user = userRepository.findByEmail(email);
        
    //     if (user != null) {
    //         // Allow the user to directly reset the password
    //         return ResponseEntity.ok("Email verified. You can reset your password.");
    //     }
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
    // }

    // //Reset Password----------------------------------------
    // @PostMapping("/reset-password")
    // public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request){
    //     String email = request.get("email");
    //     String newPassword = request.get("newPassword");

    //     User user = userRepository.findByEmail(email);
    //     if(user != null){
    //         user.setPassword(newPassword);
    //         userRepository.save(user);
    //         return ResponseEntity.ok("Password updated");

    //     }
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
    // }

}
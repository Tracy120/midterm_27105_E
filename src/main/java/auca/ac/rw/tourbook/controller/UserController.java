package auca.ac.rw.tourbook.controller;

import auca.ac.rw.tourbook.model.User;
import auca.ac.rw.tourbook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        String result = userService.registerUser(user);
        if (result.startsWith("Error:")) {
            if (result.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            if (result.toLowerCase().contains("already exists")
                    || result.toLowerCase().contains("ambiguous")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/user/search/name/{provinceName}")
    public ResponseEntity<List<User>> getUsersByProvinceName(@PathVariable("provinceName") String provinceName) {
        List<User> users = userService.getUsersByProvinceName(provinceName);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/search/code/{provinceCode}")
    public ResponseEntity<List<User>> getUsersByProvinceCode(@PathVariable("provinceCode") String provinceCode) {
        List<User> users = userService.getUsersByProvinceCode(provinceCode);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/search/location")
    public ResponseEntity<List<User>> getUsersByLocation(@RequestParam(value = "provinceCode", required = false) String provinceCode,
                                                         @RequestParam(value = "districtCode", required = false) String districtCode,
                                                         @RequestParam(value = "sectorCode", required = false) String sectorCode,
                                                         @RequestParam(value = "cellCode", required = false) String cellCode,
                                                         @RequestParam(value = "villageCode", required = false) String villageCode) {
        List<User> users = userService.getUsersByLocation(provinceCode, districtCode, sectorCode, cellCode, villageCode);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        String result = userService.deleteUser(id);
        return result.startsWith("Error:")
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
                : ResponseEntity.ok(result);
    }

    @GetMapping("/users/all")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, sortBy));
    }
}

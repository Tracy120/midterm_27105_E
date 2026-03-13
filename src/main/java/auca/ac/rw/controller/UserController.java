package auca.ac.rw.controller;

import auca.ac.rw.dto.PageResponse;
import auca.ac.rw.dto.UserPatchRequest;
import auca.ac.rw.dto.UserRequest;
import auca.ac.rw.dto.UserResponse;
import auca.ac.rw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @PatchMapping("/{id}")
    public UserResponse patchUser(@PathVariable Long id, @Valid @RequestBody UserPatchRequest request) {
        return userService.patchUser(id, request);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public PageResponse<UserResponse> getUsers(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                               @RequestParam(defaultValue = "asc") String direction) {
        return userService.getUsers(page, size, sortBy, direction);
    }

    @GetMapping("/by-province")
    public List<UserResponse> getUsersByProvince(@RequestParam(required = false) String provinceCode,
                                                 @RequestParam(required = false) String provinceName) {
        return userService.getUsersByProvince(provinceCode, provinceName);
    }

    @GetMapping("/by-location")
    public List<UserResponse> getUsersByLocation(@RequestParam(required = false) String provinceCode,
                                                 @RequestParam(required = false) String districtCode,
                                                 @RequestParam(required = false) String sectorCode,
                                                 @RequestParam(required = false) String cellCode,
                                                 @RequestParam(required = false) String villageCode) {
        return userService.getUsersByLocation(provinceCode, districtCode, sectorCode, cellCode, villageCode);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

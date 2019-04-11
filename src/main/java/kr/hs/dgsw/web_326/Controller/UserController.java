package kr.hs.dgsw.web_326.Controller;

import kr.hs.dgsw.web_326.Domain.User;
import kr.hs.dgsw.web_326.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/user/add")
    public User add(@RequestBody User user) {
        return userService.add(user);
    }
    @PostMapping("/user/login")
    public User login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword());
    }
    @PutMapping("/user/update/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user);
    }
    @DeleteMapping("/user/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return userService.delete(id);
    }
    @GetMapping("/user/view/{id}")
    public User view(@PathVariable Long id) {
        return userService.view(id);
    }
    @GetMapping("/user/list")
    public List<User> list() {
        return userService.list();
    }
}

package indi.zqc.dynamic.datasource.controller;

import indi.zqc.dynamic.datasource.model.User;
import indi.zqc.dynamic.datasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/8.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping
    public void insertUser(@RequestBody User user) {
        userService.insertUser(user);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @PostMapping("operate")
    public void operateUser(@RequestBody User user) {
        userService.insertUser(user);
        userService.updateUser(user);
    }
}

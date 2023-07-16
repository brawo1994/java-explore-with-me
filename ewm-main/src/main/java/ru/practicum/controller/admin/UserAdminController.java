package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.user.UserService;
import ru.practicum.util.Pagination;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Received POST request /admin/users with body: {}", userDto.toString());
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("userId") Long userId) {
        log.info("Received DELETE request /admin/users/{}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false, name = "ids") List<Long> userIdList,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received GET request /admin/users with params: ids {}, from {}, size {}", userIdList, from,
                size);
        return userService.getUsers(userIdList, new Pagination(from, size, Sort.by(ASC, "id")));
    }
}

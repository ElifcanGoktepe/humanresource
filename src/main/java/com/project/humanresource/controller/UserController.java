package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.User;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.service.UserService;
import com.project.humanresource.dto.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.project.humanresource.config.RestApis.CREATEUSER;
import static com.project.humanresource.config.RestApis.LOGIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtManager jwtManager;

    @PostMapping(CREATEUSER)
    public ResponseEntity<BaseResponseShort<User>> createUser(@RequestBody @Valid AddUserRequestDto dto) {
        if(!dto.password().equals(dto.rePassword()))
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        return ResponseEntity.ok(BaseResponseShort.<User>builder()
                        .data(userService.createUser(dto))
                        .code(200)
                        .message("User created successfully.")
                .build());
    }
    @PostMapping(LOGIN)
    public ResponseEntity<BaseResponseShort<String>> login(@RequestBody @Valid LoginRequestDto dto){
        Optional<User> optionalUser = userService.findByEmailPassword(dto);
        if(optionalUser.isEmpty())
            throw  new HumanResourceException(ErrorType.EMAIL_PASSWORD_ERROR);
        return ResponseEntity.ok(BaseResponseShort.<String>builder()
                .code(200)
                .data(jwtManager.createToken(optionalUser.get().getId()))
                .message("You have successfully signed in.")
                .build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<BaseResponse<User>> getUserByEmail(@RequestParam(name = "email") String email) {
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(new BaseResponse<>(false, "User not found", null));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "User found", user));
    }
} 
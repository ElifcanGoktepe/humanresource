package com.project.humanresource.controller;

import com.project.humanresource.service.ShiftBreakService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin("*")
@SecurityRequirement(name = "bearerAuth")
public class ShiftBreakController {

    private final ShiftBreakService shiftBreakService;



}

package com.project.humanresource.controller;

import com.project.humanresource.dto.request.SetPersonalFileRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.PersonalFileResponseDto;
import com.project.humanresource.repository.PersonelFileRepository;
import com.project.humanresource.service.PersonelFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personel-file")
@CrossOrigin("*")
//@SecurityRequirement(name = "bearerAuth")
public class PersonelFileController {

    private final PersonelFileService personelFileService;
    private final PersonelFileRepository personelFileRepository;

    @PostMapping("/save")
    public ResponseEntity<BaseResponseShort<Boolean>> saveOrUpdateOwnPersonelFile(@RequestBody @Valid SetPersonalFileRequestDto dto){
        personelFileService.saveOrUpdateOwnPersonelFile(dto);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Success")
                        .data(true)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponseShort<Boolean>> getPersonelFile(){
        PersonalFileResponseDto dto=personelFileService.getPersonelFile();
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .data(true)
                        .message("Personal information was brought.")
                .build());

    }
}

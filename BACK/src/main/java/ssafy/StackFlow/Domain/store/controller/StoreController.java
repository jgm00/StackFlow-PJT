package ssafy.StackFlow.Domain.store.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ssafy.StackFlow.Domain.store.DTO.StoreDto;
import ssafy.StackFlow.Domain.store.DTO.StoreResponseDto;
import ssafy.StackFlow.Domain.store.service.StoreService;
import ssafy.StackFlow.global.docs.StoreApiSpecification;
import ssafy.StackFlow.global.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController implements StoreApiSpecification {
    private final StoreService storeService;

    //매장 등록
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponseDto>> registerStore(@RequestBody StoreDto storeDto) {
        StoreResponseDto registeredStore = storeService.registerStore(storeDto);
        return ResponseEntity.ok(ApiResponse.success(registeredStore));
    }

    // 모든 매장 조회
    @GetMapping("/list/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StoreResponseDto>>> getAllStores() {
        List<StoreResponseDto> userDtoList = storeService.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(userDtoList));
    }


}

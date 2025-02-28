package ssafy.StackFlow.Domain.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties({"products"})  // 🚀 무한 순환 방지
public class SizeDto {
    private Long id;
    private String size;
}

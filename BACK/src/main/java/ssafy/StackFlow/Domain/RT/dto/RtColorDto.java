package ssafy.StackFlow.Domain.RT.dto;

import lombok.Data;
import ssafy.StackFlow.Domain.product.entity.Color;

@Data
public class RtColorDto {
    private String color_code;

    public RtColorDto(Color color) {
        color_code = color.getColorCode();
    }
}

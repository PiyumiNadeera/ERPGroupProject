package enterpriseapplication.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    private String category;

    @NotNull(message = "unitPrice is required")
    @Positive(message = "unitPrice must be positive")
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "stock cannot be negative")
    private Integer stock;
}

package com.campus.trade.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ItemRequest {
    @NotBlank(message = "物品名称不能为空")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "类别不能为空")
    private String category;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @Size(max = 2000)
    private String description;

    private String imageUrl;
    private String status;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

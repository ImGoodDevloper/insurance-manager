package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.util.IdGenerator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Entity
@Table(name = "Customer")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
@Schema(name = "Customer", description = "客戶資訊")
public class Customer {

	@Id
    @Column(name = "customer_id")
    @Schema(description = "客戶系統唯一識別碼（系統自動賦值）", example = "2026032212304501", accessMode = Schema.AccessMode.READ_ONLY)
    private Long customerId;

	@NotBlank(message = "客戶姓名不可為空")
    @Size(max = 50, message = "姓名長度不可超過 50 個字")
    @Column(name = "name", length = 50, nullable = false)
    @Schema(description = "客戶姓名")
    private String name;

	@NotBlank(message = "身分證字號不可為空")
    @Pattern(regexp = "^[A-Z][12]\\d{8}$", message = "身分證字號格式不正確")
    @Column(name = "id_no", length = 20, nullable = false, unique = true)
    @Schema(description = "身分證字號（唯一）", example = "A123456789")
    private String idNo;

	@Email(message = "電子信箱格式不正確")
    @Size(max = 100, message = "Email 長度不可超過 100 個字")
    @Column(name = "email", length = 100)
    @Schema(description = "電子信箱", example = "xiaomingWang@example.com")
    private String email;

	@Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確 (需為 09 開頭之 10 位數字)")
    @Column(name = "phone", length = 20)
    @Schema(description = "聯絡電話", example = "0912345678")
    private String phone;

    @Column(name = "status", length = 10)
    @Schema(description = "客戶狀態(ACTIVE / INACTIVE)", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;

    @Column(name = "created_at", updatable = false)
    @Schema(description = "建立時間(系統自動賦值)", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    public void initialize() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
        
        if (this.customerId == null) {
            this.customerId = IdGenerator.generateId(this.idNo);
        }
    }
}

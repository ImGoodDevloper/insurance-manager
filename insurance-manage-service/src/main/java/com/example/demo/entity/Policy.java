package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.example.demo.util.IdGenerator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "Policy")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
@Schema(name = "Policy", description = "保單契約資訊")
public class Policy {

	@Id
	@Column(name = "policy_id")
	@Schema(description = "保單系統唯一識別碼 (系統自動賦值)", example = "2026032212304501", accessMode = Schema.AccessMode.READ_ONLY)
	private Long policyId;
	
	@NotNull(message = "客戶 ID 不可為空")
	@Column(name = "customer_id")
	@Schema(description = "所屬客戶 ID", example = "2026010100000001")
	private Long customerId;

	@NotBlank(message = "保單號碼不可為空")
	@Column(name = "policy_no", length = 30, nullable = false, unique = true)
	@Schema(description = "保單號碼（唯一）", example = "LIFE-20260001")
	private String policyNo;

	@NotBlank(message = "險種不可為空")
	@Column(name = "policy_type", length = 20)
	@Schema(description = "險種（LIFE / HEALTH / CAR）", allowableValues = {"LIFE", "HEALTH", "CAR"}, example = "LIFE")
	private String policyType;

	@NotNull(message = "保費金額不可為空")
	@DecimalMin(value = "0.0", inclusive = true, message = "保費金額不可為負數")
	@Column(name = "premium", precision = 12, scale = 2)
	@Schema(description = "保費金額 (TWD)", example = "15000.00")
	private BigDecimal premium;

	@NotNull(message = "起始日期不可為空")
	@FutureOrPresent(message = "保單起始日不可早於今日")
	@Column(name = "start_date")
	@Schema(description = "保單起始日", example = "2026-03-22")
	private LocalDate startDate;

	@NotNull(message = "終止日期不可為空")
	@Column(name = "end_date")
	@Schema(description = "保單終止日", example = "2026-03-22")
	private LocalDate endDate;

	@Column(name = "status", length = 20)
	@Schema(description = "保單狀態（PENDING → ACTIVE → EXPIRED/CANCELLED）", allowableValues = {"PENDING", "ACTIVE", "EXPIRED", "CANCELLED"}, defaultValue = "PENDING")
	private String status;

	@Column(name = "created_at")
	@Schema(description = "保單建立時間 (系統自動產生)", accessMode = Schema.AccessMode.READ_ONLY)
	private LocalDateTime createdAt;

	public void initialize() {
		this.createdAt = LocalDateTime.now();
		if (StringUtils.isBlank(this.status)) {
			this.status = "PENDING";
		}
		if (this.policyId == null) {
			this.policyId = IdGenerator.generateId(this.policyNo);
		}
	}
}

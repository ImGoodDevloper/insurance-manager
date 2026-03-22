package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Policy;
import com.example.demo.service.CustomerService;
import com.example.demo.service.InsuranceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "保單管理", description = "保單之新增、核保、查詢與狀態異動")
@SecurityRequirement(name = "Bearer Authentication")
public class InsuranceController {

	@Autowired
	private InsuranceService is;

	@Autowired
	private CustomerService us;

	/**
	 * 新增客戶
	 * @param customer
	 * @return
	 */
	@Operation(summary = "客戶開戶", description = "建立新的客戶資料，需符合身分證與 Email 格式規則")
	@ApiResponse(responseCode = "201", description = "客戶建立成功")
	@ApiResponse(responseCode = "400", description = "輸入資料格式錯誤")
	@PostMapping("/customers")
	public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer) {
		us.insertCustomer(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	}

	/**
	 * 查詢指定客戶的所有保單
	 * @param customerId
	 * @return
	 */
	@Operation(summary = "查詢客戶保單", description = "根據客戶 ID 取得其名下所有保單契約。")
    @ApiResponse(responseCode = "200", description = "查詢成功")
	@GetMapping("/customers/{customerId}/policies")
	public List<Policy> getPolicies(@PathVariable("customerId") Long customerId) {
		return is.queryPolicies(customerId);
	}

	/**
	 * 新增保單契約
	 * @param policy
	 * @return
	 */
	@Operation(summary = "新增保單", description = "建立新的保單，系統將根據 policyNo 自動產生 policyId。")
    @ApiResponse(responseCode = "201", description = "保單建立成功")
	@ApiResponse(responseCode = "400", description = "輸入資料格式錯誤，保單已存在等")
	@PostMapping("/policies")
	public ResponseEntity<Policy> addPolicy(@Valid @RequestBody Policy policy) {
		is.insertPolicy(policy);
		return ResponseEntity.status(HttpStatus.CREATED).body(policy);
	}
	
	/**
	 * 更新保單狀態
	 * @param policyId
	 * @param requestBody
	 * @return
	 */
	@Operation(summary = "變更保單狀態", description = "將保單狀態更新為 ACTIVE (生效) 或 EXPIRED (到期)。")
    @ApiResponse(responseCode = "200", description = "狀態更新成功")
    @ApiResponse(responseCode = "404", description = "找不到該保單")
	@PutMapping("/policies/{policyId}/status")
	public ResponseEntity<?> updatePolicy(@PathVariable("policyId") Long policyId, @RequestBody Policy policy) {
		String newStatus = policy.getStatus();
	    
	    if (newStatus == null) {
	        return ResponseEntity.badRequest().body("缺少 status 參數");
	    }

	    int updatedRows = is.updatePolicyStatus(policyId,newStatus);

	    return ResponseEntity.ok("更新成功");
	}

}

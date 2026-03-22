package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Policy;
import com.example.demo.exception.BusinessLogicException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.InsuranceMapper;


/**
 * <pre>
 * 保單業務邏輯服務
 * <pre>
 */
@Service
public class InsuranceService {
	@Autowired
	private InsuranceMapper im;

	/**
	 * 建立新保單契約
	 * @param policy
	 * @return
	 */
	@Transactional
	public int insertPolicy(Policy policy) {
		List<Policy> existingPolicy = im.findByPolicyNo(policy.getPolicyNo());
		if (existingPolicy != null && !existingPolicy.isEmpty()) {
			throw new BusinessLogicException("保單已存在，請勿重複建立，保單號碼: " + policy.getPolicyNo());
		}
		policy.initialize();
		return im.insertPolicy(policy);
	}

	/**
	 * 查詢客戶名下的所有保單
	 * @param customerId
	 * @return
	 */
	public List<Policy> queryPolicies(Long customerId) {
		return im.findByCustomerId(customerId);
	}

	/**
	 * 變更保單狀態
	 * 
	 * @param policyId
	 * @param status
	 * @return
	 */
	@Transactional
	public int updatePolicyStatus(Long policyId, String status) {

		Policy policy = null;
		List<Policy> policies = im.findByPolicyId(policyId);
		if (policies != null) {
			policy = policies.get(0);
			policy.setStatus(status);
		} else {
			throw new NotFoundException("保單不存在");
		}
		return im.updatePolicyStatus(policy);
	}
}

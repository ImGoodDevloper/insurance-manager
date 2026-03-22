package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.entity.Policy;

/**
 * <pre>
 * 保單資料存取介面
 * </pre>
 */
@Mapper
public interface InsuranceMapper {
    
    @Insert("INSERT INTO Policy (policy_id, customer_id, policy_no, policy_type, premium, start_date, end_date, status, created_at) " +
            "VALUES (#{policyId}, #{customerId}, #{policyNo}, #{policyType}, #{premium}, #{startDate}, #{endDate}, #{status}, #{createdAt})")
    int insertPolicy(Policy policy);
    
    @Select("SELECT * FROM Policy WHERE customer_id = #{customerId}")
    List<Policy> findByCustomerId(Long customerId);
    
    @Select("SELECT * FROM Policy WHERE policy_id = #{policyId}")
    List<Policy> findByPolicyId(Long policyId);
    
    @Select("SELECT * FROM Policy WHERE policy_no = #{policyNo}")
    List<Policy> findByPolicyNo(String policyNo);
    
    @Update("UPDATE Policy SET status = #{status} WHERE policy_id = #{policyId}")
    int updatePolicyStatus(Policy policy);
}

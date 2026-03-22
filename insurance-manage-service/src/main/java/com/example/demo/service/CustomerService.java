package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Policy;
import com.example.demo.exception.BusinessLogicException;
import com.example.demo.mapper.CustomerMapper;


/**
 * <pre>
 * 使用者與客戶業務邏輯服務
 * <pre>
 */
@Service
public class CustomerService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
	@Autowired
	private CustomerMapper cm;
	
    
	/**
	 * 新增客戶資料
	 * @param costomer
	 * @return
	 */
	@Transactional
	public int insertCustomer(Customer costomer) {
		List<Policy> existingCustomer = cm.findCustomerByIdNo(costomer.getIdNo());
		if (existingCustomer != null && !existingCustomer.isEmpty()) {
			throw new BusinessLogicException("該身分證字號已被使用:" + costomer.getIdNo());
		}
		costomer.initialize();
		return cm.insertCustomer(costomer);
	}
    
	/**
	 * 使用者身分驗證
	 * @param user
	 * @param userPwd
	 */
	public void userAuth(UserDetails user, String userPwd) {
        if (!passwordEncoder.matches(userPwd, user.getPassword())) {
            throw new BadCredentialsException("帳號或密碼錯誤");
        }
	}
}

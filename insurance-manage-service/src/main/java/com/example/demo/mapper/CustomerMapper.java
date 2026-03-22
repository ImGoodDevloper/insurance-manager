package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Policy;

/**
 * <pre>
 * 客戶資料存取介面
 * </pre>
 */
@Mapper
public interface CustomerMapper {

    @Insert("INSERT INTO Customer (customer_id, name, id_no, email, phone, status, created_at) " +
            "VALUES (#{customerId}, #{name}, #{idNo}, #{email}, #{phone}, #{status}, #{createdAt})")
    int insertCustomer(Customer customer);
    
    @Select("SELECT * FROM Customer WHERE id_no = #{IdNo}")
    List<Policy> findCustomerByIdNo(String IdNo);
}

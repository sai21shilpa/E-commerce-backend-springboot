package com.ecommerce.backend.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.OrderDetail;
import com.ecommerce.backend.entity.User;


@Repository
public interface OrderDetailDao extends CrudRepository<OrderDetail, Integer>{
	
	public List<OrderDetail> findByUser(User user);
	
	public List<OrderDetail> findByOrderStatus(String status);

}

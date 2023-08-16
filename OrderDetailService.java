package com.ecommerce.backend.service;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.configuration.JwtRequestFilter;
import com.ecommerce.backend.dao.CartDao;
import com.ecommerce.backend.dao.OrderDetailDao;
import com.ecommerce.backend.dao.ProductDao;
import com.ecommerce.backend.dao.UserDao;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.OrderDetail;
import com.ecommerce.backend.entity.OrderInput;
import com.ecommerce.backend.entity.OrderProductQuantity;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.TransactionDetails;
import com.ecommerce.backend.entity.User;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;



@Service
public class OrderDetailService {
	
	private static final String ORDER_PLACED = "Placed";  
	
	private static final String KEY="rzp_test_M5K6zUPae5Sfpg";
	
	private static final String SECRET_KEY="oTT72b4sgXAK8aOgevTbLusO";
	
	private static final String CURRENCY="USD";
	
	
	@Autowired
	private OrderDetailDao orderDetailDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private UserDao userDao;
	
	

	@Autowired
	private CartDao cartDao;
	
    public List<OrderDetail> getAllOrderDetails(String status){
		List<OrderDetail> orderDetails = new ArrayList<>();
		
		if(status.equals("All"))
		{
		  orderDetailDao.findAll().forEach(
				  e -> orderDetails.add(e));
		}
		else {
		  orderDetailDao.findByOrderStatus(status).forEach(
				  a->orderDetails.add(a));
		}
		
		return orderDetails;
	}
	
    
	public List<OrderDetail> getOrderDetails() {
		String currentUser = JwtRequestFilter.CURRENT_USER;
		User user = userDao.findById(currentUser).get();
		
		return orderDetailDao.findByUser(user);
	}
	
	public void placeOrder(OrderInput orderInput, boolean isSingleProductCheckout) {
		List<OrderProductQuantity> productQuantityList = orderInput.getOrderProductQuantityList();
		
		for(OrderProductQuantity o: productQuantityList) {
			
			Product product = productDao.findById(o.getProductId()).get();
			
			String currentUser = JwtRequestFilter.CURRENT_USER;
			User user= userDao.findById(currentUser).get();
			
			OrderDetail orderDetail = new OrderDetail(
					orderInput.getFullName(),
					orderInput.getFullAddress(),
					orderInput.getContactNumber(),
					orderInput.getAlternateContactNumber(),
					ORDER_PLACED,
					product.getProductDiscountedPrice()*o.getQuantity(),
					product,
					user,
					orderInput.getTransactionId()
				);	
			
			if(!isSingleProductCheckout) {
				List<Cart> carts= cartDao.findByUser(user);
				//to clear the cart after placing order
				carts.stream().forEach(x -> cartDao.deleteById(x.getCartId()));			
			}
			orderDetailDao.save(orderDetail);
		}
	}
	
	public void markOrderAsDelivered(Integer orderId)
	{
		OrderDetail orderDetail=orderDetailDao.findById(orderId).get();
		
		if(orderDetail !=null)
		{
			orderDetail.setOrderStatus("Delivered");
			orderDetailDao.save(orderDetail);
		}
	}
	
	public TransactionDetails createTransaction(Double amount)
	{
		try{
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("amount", (amount * 100));
			jsonObject.put("currency", CURRENCY);
			
			RazorpayClient razorPay=new RazorpayClient(KEY,SECRET_KEY);
			Order order = razorPay.orders.create(jsonObject);
			System.out.println(order);
			TransactionDetails transactionDetails= prepareTransactionDetails(order);
			return transactionDetails;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}	
		return null;
	}
	
	private TransactionDetails prepareTransactionDetails(Order order)
	{
		String orderId=order.get("id");
		String currency=order.get("currency");
		Integer amount=order.get("amount");
		
		TransactionDetails transactionDetails= new TransactionDetails(orderId,currency,amount,KEY);
		return transactionDetails;
	}
	
	

}

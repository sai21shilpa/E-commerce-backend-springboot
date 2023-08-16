package com.ecommerce.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.configuration.JwtRequestFilter;
import com.ecommerce.backend.dao.CartDao;
import com.ecommerce.backend.dao.ProductDao;
import com.ecommerce.backend.dao.UserDao;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;


@Service
public class CartService {
	
	@Autowired
	private CartDao cartDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private UserDao userDao;
	
	public void deleteCartItem(Integer cartId) {
		cartDao.deleteById(cartId);
	}
	
	public Cart addToCart(Integer productId) {
		
		Product product = productDao.findById(productId).get();
		
		String username = JwtRequestFilter.CURRENT_USER;
		
		User user = null;
		
		if(username != null) {
			user = userDao.findById(username).get();
			
		}
		
		List<Cart> cartList = cartDao.findByUser(user);
		//filtering the products if that particular prodID is already added into the cart,else add to list if not ignore
		List<Cart> filteredList = cartList.stream().filter(x -> x.getProduct().getProductId() == productId).collect(Collectors.toList());
		
		//checking if productId exists into the cart
		if(filteredList.size() > 0) {
			return null;
		}
		
		
		if(product != null && user != null) {
			Cart cart = new Cart(product, user);
			return cartDao.save(cart);
		}
		return null;
	}
	
	public List<Cart> getCartDetails(){
		String username = JwtRequestFilter.CURRENT_USER;
		User user = userDao.findById(username).get();
		return cartDao.findByUser(user);
		
	}
	
	

}

package com.apress.prospringmvc.bookstore.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.apress.prospringmvc.bookstore.domain.Customer;
import com.apress.prospringmvc.bookstore.domain.Order;

@Repository("orderRepository")
public class JpaOrderRepository implements OrderRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Order save(Order order) {
		// The order is always a transient object, since we are creating an order, so normally persist is sufficient.
		// However, the Customer, Book and Category are objects that already exist and are in detached state.
		// Persisting these objects (indirectly via the cascading) will trigger an exception.
		// By calling merge we can save transient objects and re-attach detached objects automatically. 
		return this.entityManager.merge(order);
	}

	@Override
	public List<Order> findByCustomer(Customer customer) {
		String hql = "select o from Order o where o.customer=:customer";
		TypedQuery<Order> query = this.entityManager.createQuery(hql, Order.class);
		query.setParameter("customer", customer);
		return query.getResultList();
	}

}
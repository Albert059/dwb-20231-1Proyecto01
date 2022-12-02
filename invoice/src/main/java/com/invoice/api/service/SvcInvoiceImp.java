package com.invoice.api.service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.entity.Cart;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice repo;
	
	@Autowired
	RepoItem repoItem;
	
	@Autowired
	RepoCart repoCart;
	
	@Autowired
	ProductClient productCl;
	
	@Override
	public List<Invoice> getInvoices(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	@Override
	public ApiResponse generateInvoice(String rfc) {
		List<Cart> carts = repoCart.findByRfcAndStatus(rfc, 1);

		if(carts.isEmpty())
			throw new ApiException(HttpStatus.NOT_FOUND, "cart has no items");

		LinkedList<Item> items = new LinkedList<Item>();

		for(Cart cart : carts) {
			Item item = new Item();
			item.setGtin(cart.getGtin());
			item.setQuantity(cart.getQuantity());
			Double price = productCl.getProduct(cart.getGtin()).getBody().getPrice();
			item.setUnit_price(price);
			double total = cart.getQuantity() * price;
			item.setTotal(total);
			item.setTaxes(total * .16);
			item.setSubtotal(total - item.getTaxes());
			item.setStatus(1);
			items.add(item);
		}

		Invoice invoice = new Invoice();
		double total = 0;
		double taxes = 0;
		double subtotal = 0;

		for(Item item : items) {
			total += item.getTotal();
			taxes += item.getTaxes();
			subtotal += item.getSubtotal();
		}

		invoice.setRfc(rfc);
		invoice.setTotal(total);
		invoice.setTaxes(taxes);
		invoice.setSubtotal(subtotal);
		invoice.setStatus(1);
		LocalDateTime time = LocalDateTime.now();
		invoice.setCreated_at(time);
		int invoiceId = repo.save(invoice).getInvoice_id();

		for(Item item : items) {
			productCl.updateProductStock(item.getGtin(), item.getQuantity());
			item.setId_invoice(invoiceId);
		}
		repoItem.saveAll(items);
		repoCart.clearCart(rfc);
		return new ApiResponse("invoice generated");
	}

}

/* Copyright (C) 2012 cloudbase.io
 
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License, version 2, as published by
 the Free Software Foundation.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; see the file COPYING.  If not, write to the Free
 Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA.
 */
package com.cloudbase;

import java.util.ArrayList;
import java.util.Hashtable;

/***
 * This is the bill object for the PayPal digital goods payments APIs. A bill object must contain
 * at least one BillItem.
 *
 * The description of the bill should also contain the total amount as PayPal does not always display
 * the amount in the checkout page.
 */
public class CBPayPalBill {
	/***
	 * a name for the purchase
	 */
	private String name;
	/***
	 * a description of the bill item.
	 * this should also contain the price for the bill as PayPal will not always display the amount field.
	 */
	private String description;
	/***
	 * this is a user generated unique identifier for the transaction.
	 */
	private String invoiceNumber;
	/***
	 * this is a list of BillItems. Each CBPayPalBill must have at least one BillItem
	 */
	private ArrayList<CBPayPalBillItem> items;
	/***
	 * The 3 letter ISO code for the transaction currency. If not specified this will automatically
	 * be USD
	 */
	private String currency;
	/***
	 * This is the code of a CloudFunction to be executed once the payment is completed
	 */
	private String paymentCompletedFunction;
	/***
	 * This is the name of a CloudFunction to be executed if the payment is cancelled
	 */
	private String paymentCancelledFunction;
	/**
	 * By default the express checkout process will return to the cloudbase APIs. if you want to override 
	 * this behaviour and return to a page you own once the payment is completed set this property to the url
	 */
	private String paymentCompletedUrl;
	/**
	 * By default the express checkout process will return to the cloudbase APIs. if you want to override
	 * this behaviour and return to a page you own once the payment has been cancelled set this property to the url
	 */
	private String paymentCancelledUrl;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPaymentCompletedFunction() {
		return paymentCompletedFunction;
	}
	public void setPaymentCompletedFunction(String paymentCompletedFunction) {
		this.paymentCompletedFunction = paymentCompletedFunction;
	}
	public String getPaymentCancelledFunction() {
		return paymentCancelledFunction;
	}
	public void setPaymentCancelledFunction(String paymentCancelledFunction) {
		this.paymentCancelledFunction = paymentCancelledFunction;
	}
	public String getPaymentCompletedUrl() {
		return paymentCompletedUrl;
	}
	public void setPaymentCompletedUrl(String paymentCompletedUrl) {
		this.paymentCompletedUrl = paymentCompletedUrl;
	}
	public String getPaymentCancelledUrl() {
		return paymentCancelledUrl;
	}
	public void setPaymentCancelledUrl(String paymentCancelledUrl) {
		this.paymentCancelledUrl = paymentCancelledUrl;
	}
	/***
	 * Adds a new item to this PayPalBill
	 * 
	 * @param newItem A populated CBPayPalBillItem object
	 */
	public void addNewItem(CBPayPalBillItem newItem) {
		if (this.items == null)
			this.items = new ArrayList<CBPayPalBillItem>();
		
		this.items.add(newItem);
	}
	/***
	 * This method is used internally to generate the NSMutableDictionary to be serialised
	 * for the calls to the cloudbase.io APIs
	 *
	 * @return The Hashtable representation of the Bill object
	 */
	public Hashtable<String, Object> serializePurchase() {
		if (this.items == null || this.items.size() == 0)
			return null;
		
		double totalPrice = 0.0;
		ArrayList<Hashtable<String, String>> items = new ArrayList<Hashtable<String, String>>();
		for (CBPayPalBillItem curItem : this.items) {
			Hashtable<String, String> serializedItem = new Hashtable<String, String>();
			serializedItem.put("item_name", curItem.getName());
			serializedItem.put("item_description", curItem.getDescription());
			serializedItem.put("item_amount", Double.toString(curItem.getAmount()));
			serializedItem.put("item_tax", Double.toString(curItem.getTax()));
			serializedItem.put("item_quantity", Integer.toString(curItem.getQuantity()));
			
			totalPrice += curItem.getAmount() + (curItem.getTax() <= 0?0:curItem.getTax());
			
			items.add(serializedItem);
		}
		
		Hashtable<String, Object> purchase = new Hashtable<String, Object>();
		purchase.put("name", this.getName());
		purchase.put("description", this.getDescription());
		purchase.put("amount", Double.toString(totalPrice));
		purchase.put("invoice_number", this.getInvoiceNumber());
		purchase.put("items", items);
		
		return purchase;
	}
}

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
/***
 * this object represents a single item within a CBPayPalBill object.
 */
public class CBPayPalBillItem {
	/***
	 * The name of the item for the transaction
	 */
	private String name;
	/***
	 * An extended description of the item. This should also contain the amount as
	 * PayPal does not always display it.
	 */
	private String description;
	/***
	 * The amount of the transaction
	 */
	private double amount;
	/***
	 * additional taxes to be added to the amount
	 */
	private double tax;
	/***
	 * a quantity representing the number of items involved in the transaction.
	 * for example 100 poker chips
	 */
	private int quantity;
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
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}

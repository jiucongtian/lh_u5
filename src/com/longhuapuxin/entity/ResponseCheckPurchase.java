package com.longhuapuxin.entity;

public class ResponseCheckPurchase extends ResponseDad {
	private boolean PurchaseConfirmed;

	public boolean isPurchaseConfirmed() {
		return PurchaseConfirmed;
	}

	public void setPurchaseConfirmed(boolean purchaseConfirmed) {
		PurchaseConfirmed = purchaseConfirmed;
	}
}

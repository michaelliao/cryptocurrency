package com.itranswarp.bitcoin.wallet.pay;

import static org.junit.Assert.*;

import org.junit.Test;

import com.itranswarp.bitcoin.util.HashUtils;

public class PaymentTest {

	@Test
	public void testPayment() {
		Payment payment = new Payment();
		payment.alloc("Kwzgi66fi76Jokst1W6RNVL21ZvKVnNcJ1m8C3uFHjoY8jbTwrGu",
				"ff9d2016f137b9eb5dde6ebc9c5602d6d04e11b580a8c2d9ba0a4ae98dcf47fc", 0, 1498570L,
				HashUtils.toBytes("76a914a8803a2aa01b5bdc3380604e516d62fdfbce77f888ac"));
		payment.alloc("KxPUbT4ifFj7vHTsczVi5BkxE68Sys5yXxeCgvknXdxwSa7wu73R",
				"ff9d2016f137b9eb5dde6ebc9c5602d6d04e11b580a8c2d9ba0a4ae98dcf47fc", 1, 498570L,
				HashUtils.toBytes("76a914272dd573f529bf7d24b1a71c0e9c5c5eff343f1088ac"));
		payment.payTo("1NW3LnKh3pBSfdEJdohGS5wWarvGfKQpw2", 497570L);
		payment.payTo("143RDGMuZjNkcotCE3CkGeE4mu9HnNahit", 1497570L);
		byte[] tx = payment.buildTransaction();
		System.out.println(HashUtils.toHexString(tx));
	}

}

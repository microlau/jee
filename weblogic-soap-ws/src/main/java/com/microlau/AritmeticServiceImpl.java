package com.microlau;

import javax.ejb.Stateless;
import javax.jws.WebService;

@Stateless
@WebService(endpointInterface = "com.microlau.AritmeticWS")
public class AritmeticServiceImpl implements AritmeticWS {

	@Override
	public int sum(int a, int b) {
		return a + b;
	}

}

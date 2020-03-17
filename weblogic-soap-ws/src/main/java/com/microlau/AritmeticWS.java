package com.microlau;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface AritmeticWS {

	@WebMethod
	public int sum(int a, int b);
}

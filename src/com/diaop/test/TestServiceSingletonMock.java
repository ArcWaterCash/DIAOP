package com.diaop.test;

import com.diaop.annotation.Pointcut;

public class TestServiceSingletonMock extends TestServiceSingleton {

	@Pointcut(TestInterceptor.class)
	public String test(String caller) {
		return caller;
	}
}

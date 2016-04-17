package com.diaop.test;

import com.diaop.annotation.Pointcut;

public class TestServiceSingletonMock extends TestServiceSingleton {

	@Pointcut(TestInterceptor.class)
	public void test() {
		System.out.println(this.getClass().getName());
	}
}

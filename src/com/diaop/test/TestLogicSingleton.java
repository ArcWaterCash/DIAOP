package com.diaop.test;

import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;
import com.diaop.annotation.Pointcut;

@CreatePattern(CreatePatternType.SINGLETON)
public class TestLogicSingleton {

	@Pointcut
	public void test() {
		System.out.println(this.getClass().getName());
	}
}

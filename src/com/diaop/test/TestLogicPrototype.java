package com.diaop.test;

import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;
import com.diaop.annotation.Pointcut;

@CreatePattern(CreatePatternType.PROTOTYPE)
public class TestLogicPrototype {
	
	@Pointcut
	public String test(String callerName) {
		System.out.println(this.getClass().getName());
		return callerName;
	}
}

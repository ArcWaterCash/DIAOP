package com.diaop.test;


import com.diaop.annotation.AutoInjection;
import com.diaop.annotation.Pointcut;
import com.diaop.exception.DiaopException;
import com.diaop.util.DiUtil;

public final class TestDriver extends Thread {

	@AutoInjection
	private TestServicePrototype serviceProt;
	
	@AutoInjection
	private TestServiceSingleton serviceSingle;
	
	@AutoInjection
	private TestServiceThreadLocal serviceThread;
	
	public static void main(String[] args) throws InterruptedException {
		try {
			test1();
			test2();
			test3();
			test4();
		} catch (DiaopException e) {
			e.printStackTrace();
		}

	}
	
	public static void test1() throws DiaopException {
		System.out.println("===test1:start===");
		DiUtil.initializeDI();
		TestDriver driver = DiUtil.getInstanceInjectedProperties(TestDriver.class);
		driver.test();
		DiUtil.finalizeDI();
		System.out.println("===test1:end===");
	}
	
	public static void test2() throws DiaopException {
		System.out.println("===test2:start===");
		DiUtil.initializeDI();
		DiUtil.setTestMode();
		TestDriver driver = DiUtil.getInstanceInjectedProperties(TestDriver.class);
		driver.test();
		DiUtil.finalizeDI();
		System.out.println("===test2:end===");
	}
	
	public static void test3() throws DiaopException {
		System.out.println("===test3:start===");
		DiUtil.initializeDI();
		TestDriver driver = DiUtil.getInstanceInjectedProperties(TestDriver.class);
		driver.display(driver);

		TestDriver driver2 = DiUtil.getInstanceInjectedProperties(TestDriver.class);
		driver2.display(driver2);
		
		DiUtil.finalizeDI();
		System.out.println("===test3:end===");
	}
	
	public static void test4() throws DiaopException, InterruptedException {
		System.out.println("===test4:start===");
		DiUtil.initializeDI();
		TestDriver driver = new TestDriver();
		driver.start();
		TestDriver driver2 = new TestDriver();
		driver2.start();
		
		driver.join();
		driver2.join();
		System.out.println("===test4:end===");
	}
	
	@Pointcut(TestInterceptor.class)
	public void test() {
		System.out.println(serviceProt.test(this.getClass().getName()));
		System.out.println(serviceSingle.test(this.getClass().getName()));
		System.out.println(serviceThread.test(this.getClass().getName()));
	}
	
	public void run() {
		try {
			TestDriver driver = DiUtil.getInstanceInjectedProperties(TestDriver.class);
			display(driver);
		} catch (DiaopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void display(TestDriver driver) {
		System.out.println(TestServicePrototype.class.getSimpleName()+":"+driver.serviceProt.hashCode());
		System.out.println(TestServiceSingleton.class.getSimpleName()+":"+driver.serviceSingle.hashCode());
		System.out.println(TestServiceThreadLocal.class.getSimpleName()+":"+driver.serviceThread.hashCode());
	}
}

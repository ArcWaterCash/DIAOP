package com.diaop.interceptor;

import com.diaop.util.DiUtil;

public class ScriptCreator {
	private ScriptCreator() {

	}

	public static final String PRESCRIPT = "{";
	public static final String POSTSCRIPT = "}";
	
	public static final String DO_INJECT = DiUtil.class.getName()+".inject($0);";
	private static String GET_POINTCUT = InterceptorBase.class.getName()+" _i = "+InterceptUtil.class.getName()+".getPointcut($class,replacestr);";
	public static final String DO_BEFORE = "if(_i!=null){_i.before();}";
	public static final String DO_AFTER = "if(_i!=null){_i.after();}";
	
	public static String GET_POINTCUT(String methodname, String... paramClassnames) {
		String dq = "\"";
		StringBuilder builder = new StringBuilder();
		builder.append(dq).append(methodname).append(dq);
		builder.append(",");
		
		if (paramClassnames.length == 0) {
			builder.append("null");
		} else {
			builder.append("new String[]{");
			boolean isFirst = true;
			for (String name : paramClassnames) {
				if (!isFirst) {
					builder.append(",");
				}
				isFirst = false;
				builder.append(dq).append(name).append(dq);
			}
			builder.append("}");
		}
		
		
		String paramstr = builder.toString();
		System.out.println(paramstr);
		return GET_POINTCUT.replaceAll("replacestr", paramstr);
	}
}

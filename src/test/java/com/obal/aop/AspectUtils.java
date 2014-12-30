package com.obal.aop;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectUtils {
	private static final Logger logger = LoggerFactory.getLogger(AspectUtils.class);
	 
    private AspectUtils() {

    }

    public static Logger getLogger(org.aspectj.lang.JoinPoint joinPoint) {
            try {
                    @SuppressWarnings("rawtypes")
                    Class declaringType = joinPoint.getSignature().getDeclaringType();
                    Field loggerField = declaringType.getDeclaredField("logger");
                    loggerField.setAccessible(true);
                    return (Logger) loggerField.get(null);
            } catch (NoSuchFieldException e) {

            } catch (Exception e) {

            }
            return logger;
    }

    public static void logParamValues(StringBuilder logLine,
                    String[] paramNames, Object[] paramValues) {
            for (int i = 0; i < paramValues.length; i++) {
                    logLine.append(paramNames[i]).append("=")
                                    .append(toString(paramValues[i]));
                    if (i < paramValues.length - 1)
                            logLine.append(", ");
            }
    }

    @SuppressWarnings("rawtypes")
    public static String toString(Object object) {
            if (object == null)
                    return "<null>";
            else if (object instanceof String) {
                    if(((String) object).length() > 100)
                            return ((String) object).substring(0, 100) + "...[more]";
                    else return (String) object;
            }
            else if (object instanceof Long)
                    return ((Long) object).toString();
            else if (object instanceof Boolean)
                    return ((Boolean) object).toString();
            else if (object instanceof Double)
                    return ((Double) object).toString();
            else if (object instanceof Integer)
                    return ((Integer) object).toString();
            else if (object instanceof List)
                    return "items{" + ((List) object).size() + "}";
            else
                    return "object";
    }
}

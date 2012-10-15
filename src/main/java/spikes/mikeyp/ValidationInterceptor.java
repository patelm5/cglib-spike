package spikes.mikeyp;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import ch.lambdaj.function.matcher.Predicate;

public class ValidationInterceptor implements MethodInterceptor {

	private Object real;
	private Map<Class, Set<Predicate>> validationRules = new HashMap<Class, Set<Predicate>>();

	public ValidationInterceptor(Object obj, Map<Class, Set<Predicate>> validationRules) {
		this.real = obj;
		this.validationRules = validationRules;
	}
	
	public ValidationInterceptor(Object obj) {
		this.real = obj;
	}

	public Object intercept(Object arg0, Method method, Object[] objects, MethodProxy proxy) throws Throwable {

		if (real instanceof Map && "put".equals(method.getName())) {
			boolean valid = false;
			if (objects != null) {
				valid = true;
				Object o = objects[1]; 
					Set<Predicate> rules = validationRules.get(o.getClass());
					if (rules != null) {
						for (Predicate p : rules) {
							if (!p.apply(o)) {
								valid = false;
							}
						}
					}
				

			}

			if (valid) {
				return method.invoke(real, objects);
			} else {
				throw new MapValidationException("Tried to put an invalid value in map" +objects);
			}

		} else {
			return method.invoke(real, objects);
		}
	}

}

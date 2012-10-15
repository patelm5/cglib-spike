package spikes.mikeyp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

import org.junit.Test;

import ch.lambdaj.function.matcher.Predicate;

public class ValidationInterceptorTest {

	
	private ValidationInterceptor interceptor ; 
	
	private Map<String,Object> map = new HashMap<String,Object> () ; 
	
	private Map<Class,Set<Predicate>> rules= new HashMap<Class,Set<Predicate>> () ;
	
	private Enhancer enhancer = new Enhancer () ;
	
	
	@Test
	public void noPredicatesSet(){
		interceptor = new ValidationInterceptor(map); 
		Map<String,Object> proxyObject = createProxy(); 
		assertNull( proxyObject.put("test","testKey")); 
	}


	
	@Test
	public void calledDifferentMethod(){
		interceptor = new ValidationInterceptor(map); 
		Map<String,Object> proxyObject = createProxy(); 
		assertNull( proxyObject.get("test")); 
	}
	
	@Test
	public void matchesPredicate(){
		buildPredicateSet(); 
		interceptor = new ValidationInterceptor(map , rules); 
		Map<String,Object> proxyObject = createProxy(); 
		assertNull( proxyObject.put("test",600)); 
		assertEquals(600,proxyObject.get("test")); 
	}



	
	@Test(expected=MapValidationException.class)
	public void doesntMatchPredicate(){
		buildPredicateSet(); 
		interceptor = new ValidationInterceptor(map , rules); 
		Map<String,Object> proxyObject = createProxy(); 
		proxyObject.put("test",300); 
	}
	

	

	private void buildPredicateSet() {
		Set<Predicate> predicateSet = new HashSet<Predicate>() ; 
		predicateSet.add(buildIntegerPredicate()); 
		rules.put(Integer.class,predicateSet);
	}
	
	private Predicate<Integer> buildIntegerPredicate(){
		return new Predicate<Integer>() {

			@Override
			public boolean apply(Integer item) {
				if( item != null && item > 500)
				{
					return true; 
				}
				return false; 
			}
		};
	}
	
	private Map<String, Object> createProxy() {
		enhancer.setSuperclass(map.getClass()); 
		enhancer.setCallback(interceptor); 
		return  (Map<String, Object>) enhancer.create();
	}
}

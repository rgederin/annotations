package main;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import annotations.Init;
import annotations.Service;

public class AnnotationProcessor {

	/**
	 * Service Name -> Reference to corresponding service
	 */
	private static Map<String, Object> servicesMap = new HashMap<String, Object>();

	public static void main(String[] args) {
		/*
		 * inspectService(SimpleService.class);
		 * inspectService(LazyService.class); inspectService(String.class);
		 */
		loadService("services.SimpleService");
		loadService("services.LazyService");
		loadService("java.lang.String");

		System.out.println("Map size after loading services: "
				+ servicesMap.size());
	//	printMap(servicesMap);

	
		initService(servicesMap.get("SimpleServiceName"));
		initService(servicesMap.get("LazyServiceName"));
	}

	private static void printMap(Map mp) {
		Iterator it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private static void loadService(String className) {
		try {
			Class<?> serviceClass = Class.forName(className);
			if (serviceClass.isAnnotationPresent(Service.class)) {
				Object serviceObject = serviceClass.newInstance();
				Service serviceAnnotation = serviceClass
						.getAnnotation(Service.class);
				servicesMap.put(serviceAnnotation.name(), serviceObject);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static void initService(Object serviceObject) {
		Class<?> serviceClass = serviceObject.getClass();
		if (serviceClass.isAnnotationPresent(Service.class)) {
			Service serviceAnnotation = serviceClass
					.getAnnotation(Service.class);
			if (serviceAnnotation.lazyLoad()) {
				System.out
						.println("Service "
								+ serviceClass.getCanonicalName()
								+ " should be Init directly through it's support lazy initialization");
				return;
			}

			Method[] methods = serviceClass.getMethods();

			for (Method method : methods) {
				if (method.isAnnotationPresent(Init.class)) {
					try {
						method.invoke(serviceObject);
					} catch (Exception e) {
						Init initAnnotation = method.getAnnotation(Init.class);
						if (initAnnotation.suppressException()) {
							System.err.println(e.getMessage());
						} else {
							throw new RuntimeException();
						}
					}
				}
			}
		}
	}

	private static void inspectService(Class<?> service) {
		if (service.isAnnotationPresent(Service.class)) {
			Service annotation = service.getAnnotation(Service.class);
			System.out.println("Annotation found for class: "
					+ service.getName());
			System.out.println("Name: " + annotation.name());
			System.out.println("Lazy load: " + annotation.lazyLoad());
			inspectServiceMethods(service);
		} else {
			System.out.println("Annotations was not found for class: "
					+ service.getName());
		}
		System.out.println();
	}

	private static void inspectServiceMethods(Class<?> service) {
		Method[] methods = service.getDeclaredMethods();
		System.out.println("\tInspect Methods Annotation:");
		if (methods == null || methods.length == 0) {
			System.out.println("\tMethods not found in class: "
					+ service.getName());
			return;
		}

		for (Method method : methods) {
			if (method.isAnnotationPresent(Init.class)) {
				System.out.println("\tSuppress exception: "
						+ method.getAnnotation(Init.class).suppressException());
			}

		}
	}
}

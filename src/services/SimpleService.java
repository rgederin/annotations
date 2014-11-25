package services;

import annotations.Init;
import annotations.Service;

@Service(name = "SimpleServiceName")
public class SimpleService {
	@Init
	public void initService() {
		System.out.println("Simple Service: Service init");
	}
	
}

package services;

import annotations.Init;
import annotations.Service;

@Service(name = "LazyServiceName", lazyLoad=true)
public class LazyService {
	@Init
	public void lazyInit() throws Exception {
		System.out.println("Lazy Service: lazy init");
	}
}

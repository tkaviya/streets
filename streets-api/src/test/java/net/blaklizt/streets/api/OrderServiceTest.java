//package net.blaklizt.streets.api;
//
//import net.blaklizt.streets.api.service.OrderService;
//import net.blaklizt.streets.api.service.impl.OrderServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.support.AnnotationConfigContextLoader;
//
///**
// * Created by photon on 2015/12/30.
// */
//
//@RunWith(SpringJUnit4ClassRunner.class)
//// ApplicationContext will be loaded from the static inner ContextConfiguration class
//@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
//public class OrderServiceTest {
//
//	@Configuration
//	static class ContextConfiguration {
//
//		// this bean will be injected into the OrderServiceTest class
//		@Bean
//		public OrderService userService() {
//			OrderService userService = new OrderServiceImpl();
//			// set properties, etc.
//			return userService;
//		}
//	}
//
//	@Autowired
//	private OrderService userService;
//
//	@Test
//	public void testOrderService() {
//		// test the userService
//	}
//}
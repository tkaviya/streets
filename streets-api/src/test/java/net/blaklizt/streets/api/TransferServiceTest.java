//package net.blaklizt.streets.api;
//
//import net.blaklizt.streets.api.configuration.JndiDataConfig;
//import net.blaklizt.streets.api.configuration.StandaloneDataConfig;
//import net.blaklizt.streets.api.configuration.TransferServiceConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.support.AnnotationConfigContextLoader;
//
///**
// * Created by photon on 2015/12/30.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
//	classes={TransferServiceConfig.class, StandaloneDataConfig.class, JndiDataConfig.class})
//@ActiveProfiles("dev")
//public class TransferServiceTest {
//
//	@Autowired
//	private TransferService transferService;
//
//	@Test
//	public void testTransferService() {
//		// test the transferService
//	}
//}
//package net.blaklizt.streets.api.configuration;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
///**
// * Created by photon on 2015/12/30.
// */
//
//@Configuration
//public class TransferServiceConfig {
//
//	@Autowired DataSource dataSource;
//
//	@Bean
//	public TransferService transferService() {
//		return new DefaultTransferService(accountRepository(), feePolicy());
//	}
//
//	@Bean
//	public AccountRepository accountRepository() {
//		return new JdbcAccountRepository(dataSource);
//	}
//
//	@Bean
//	public FeePolicy feePolicy() {
//		return new ZeroFeePolicy();
//	}
//}

package com.example.study;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TestStudyConfiguration {

//    @Bean(name = "sessionFactory")
//    public SessionFactory getSessionFactory() throws Exception {
//        JdbcDataSource ds = new JdbcDataSource();
//        ds.setURL("jdbc:h2:˜/test");
//        ds.setUser("sa");
//        ds.setPassword("sa");
//        Context ctx = new InitialContext();
//        ctx.bind("jdbc/dsName", ds);
//
//        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
//
//        // Package contain entity classes
//        factoryBean.setPackagesToScan(new String[] { "" });
//        factoryBean.setDataSource(ds);
//        factoryBean.afterPropertiesSet();
//        //
//        SessionFactory sf = factoryBean.getObject();
//        System.out.println("## getSessionFactory: " + sf);
//        return sf;
//    }

//    @Bean(name = "customerRepository")
//    public CustomerRepository customerRepository() throws Exception {
//        CustomerRepository customerRepository = new CustomerRepository(getSessionFactory());
//        return customerRepository;
//    }
}

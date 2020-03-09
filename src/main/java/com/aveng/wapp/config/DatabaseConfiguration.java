package com.aveng.wapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * All DB related configs goes here
 *
 * @author apaydin
 */
@Configuration
@EnableJpaRepositories("com.aveng.wapp.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {

}

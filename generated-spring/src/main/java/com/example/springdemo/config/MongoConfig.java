package com.example.springdemo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.uri", havingValue = "mongodb://", matchIfMissing = false)
@EnableMongoRepositories(basePackages = {"com.example.springdemo.repository", "com.example.springdemo.comments"})
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri:}")
    private String connectionString;

    @Value("${spring.data.mongodb.database:GitTasker}")
    private String databaseName;

    @Override
    @NonNull
    protected String getDatabaseName() {
        logger.info("MongoConfig getDatabaseName called with: {}", databaseName);
        return databaseName;
    }

    @Override
    @NonNull
    public MongoClient mongoClient() {
        logger.info("MongoConfig mongoClient called with connectionString: {}", 
                   connectionString != null && !connectionString.isEmpty() ? "***" : "NULL/EMPTY");
        logger.info("MongoConfig mongoClient called with databaseName: {}", databaseName);
        
        if (connectionString == null || connectionString.trim().isEmpty()) {
            throw new IllegalArgumentException("MongoDB connection string is null or empty! Check spring.data.mongodb.uri property.");
        }
        
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}

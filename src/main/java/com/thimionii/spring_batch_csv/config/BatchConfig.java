package com.thimionii.spring_batch_csv.config;

import com.thimionii.spring_batch_csv.entity.Customer;
import com.thimionii.spring_batch_csv.repo.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    private final CustomerRepository customerRepository;
    private final CsvFileConfig csvFileConfig;

    // Inject CsvFileConfig
    @Autowired
    public BatchConfig(CustomerRepository customerRepository, CsvFileConfig csvFileConfig) {
        this.customerRepository = customerRepository;
        this.csvFileConfig = csvFileConfig;
    }

    /**
     * Configures the CSV file reader.
     */
    @Bean
    public FlatFileItemReader<Customer> itemReader() {
        FlatFileItemReader<Customer> fileItemReader = new FlatFileItemReader<>();
        try {
            String csvFilePath = csvFileConfig.getPath();
            log.info("Initializing CSV file reader with path: {}", csvFilePath);

            // Check if file exists
            File file = new File(csvFilePath);
            if (!file.exists()) {
                log.error("CSV file does not exist at the provided path: {}", csvFilePath);
                throw new RuntimeException("CSV file not found at " + csvFilePath);
            }

            fileItemReader.setResource(new FileSystemResource(csvFilePath));
            fileItemReader.setName("csv-reader");
            fileItemReader.setLinesToSkip(1); // Skip the header row
            fileItemReader.setLineMapper(lineMapper());
        } catch (Exception e) {
            log.error("Error setting up the FlatFileItemReader: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure itemReader", e);
        }
        return fileItemReader;
    }

    /**
     * Configures the line mapper for parsing CSV data.
     */
    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        try {
            log.info("Setting up line mapper for CSV data.");
            DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setDelimiter(",");
            tokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

            BeanWrapperFieldSetMapper<Customer> mapper = new BeanWrapperFieldSetMapper<>();
            mapper.setTargetType(Customer.class);

            lineMapper.setLineTokenizer(tokenizer);
            lineMapper.setFieldSetMapper(mapper);
        } catch (Exception e) {
            log.error("Error setting up LineMapper: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure lineMapper", e);
        }
        return lineMapper;
    }

    /**
     * Configures the processor for transforming data.
     */
    @Bean
    public CustomerProcessor processor() {
        log.info("Initializing CustomerProcessor.");
        return new CustomerProcessor();
    }

    /**
     * Configures the writer for saving processed data to the database.
     */
    @Bean
    public RepositoryItemWriter<Customer> itemWriter() {
        RepositoryItemWriter<Customer> itemWriter = new RepositoryItemWriter<>();
        try {
            log.info("Setting up RepositoryItemWriter.");
            itemWriter.setRepository(customerRepository);
            itemWriter.setMethodName("save");
        } catch (Exception e) {
            log.error("Error setting up RepositoryItemWriter: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure itemWriter", e);
        }
        return itemWriter;
    }

    /**
     * Configures the batch step.
     */
    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("Configuring step: csv-step.");
        return new StepBuilder("csv-step", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(itemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    /**
     * Configures a task executor for parallel processing.
     */
    private TaskExecutor taskExecutor() {
        log.info("Setting up TaskExecutor for parallel processing.");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Batch-Thread-");
        executor.initialize();
        return executor;
    }

    /**
     * Configures the batch job.
     */
    @Bean
    public Job job(JobRepository repository, PlatformTransactionManager transactionManager) {
        log.info("Creating job: csv-job.");
        return new JobBuilder("csv-job", repository)
                .flow(step(repository, transactionManager))
                .end()
                .build();
    }
}

package com.juan.img.reader.batch;

import com.juan.img.reader.model.Image;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.configuration.annotation.StepScope;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job importJob(JobRepository jobRepository, Step importStep) {
        return new JobBuilder("importImageInformation", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(importStep)
                .end()
                .build();
    }

    @Bean
    public Step importStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           FlatFileItemReader<Image> reader,
                           JdbcBatchItemWriter<Image> writer) {
        return new StepBuilder("importStep", jobRepository)
                .<Image, Image>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public ItemProcessor<Image, Image> processor() {
        return item -> item;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Image> reader(@Value("#{jobParameters['file']}") String file) {
        FileSystemResource fs = new FileSystemResource(file);

        return new FlatFileItemReaderBuilder<Image>()
                .name("ImageReader")
                .resource(fs)
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .build();
    }

    @Bean
    public LineMapper<Image> lineMapper() {
        DefaultLineMapper<Image> mapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("name", "size", "address");
        tokenizer.setStrict(false);

        BeanWrapperFieldSetMapper<Image> fsm = new BeanWrapperFieldSetMapper<Image>() {
            @Override
            public void setTargetType(Class<? extends Image> type) { super.setTargetType(type); }
            @Override
            public Image mapFieldSet(org.springframework.batch.item.file.transform.FieldSet fs) {
                Image t = new Image();
                t.setName(fs.readString("name"));
                t.setSize(fs.readInt("size"));
                t.setAddress(fs.readString("address"));
                return t;
            }
        };
        fsm.setTargetType(Image.class);

        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fsm);
        return mapper;
    }

    @Bean
    public JdbcBatchItemWriter<Image> writer(NamedParameterJdbcTemplate template) {
        JdbcBatchItemWriter<Image> writer = new JdbcBatchItemWriter<>();
        writer.setJdbcTemplate(template);
        writer.setSql("insert into image (name, size, address) VALUES (:name, :size, :address)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }
}

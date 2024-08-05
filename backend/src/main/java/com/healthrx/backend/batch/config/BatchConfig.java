package com.healthrx.backend.batch.config;

import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.batch.DrugItemProcessor;
import com.healthrx.backend.batch.DrugItemWriter;
import com.healthrx.backend.batch.JobCompletionListener;
import com.healthrx.backend.batch.utils.DrugXml;
import com.healthrx.backend.repository.DrugPackRepository;
import com.healthrx.backend.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.MalformedURLException;

@Configuration
//@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public StaxEventItemReader<DrugXml> itemReader() throws MalformedURLException {
        Jaxb2Marshaller drugMarshaller = new Jaxb2Marshaller();
        drugMarshaller.setClassesToBeBound(DrugXml.class);

        return new StaxEventItemReaderBuilder<DrugXml>()
                .name("drugReader")
                .resource(new UrlResource("https://api.dane.gov.pl/resources/52520,wykaz-produktow-leczniczych-plik-w-formacie-xml/file"))
                .addFragmentRootElements("{http://rejestrymedyczne.ezdrowie.gov.pl/rpl/eksport-danych-v1.0}produktLeczniczy")
                .unmarshaller(drugMarshaller)
                .build();
    }

    @Bean
    public SynchronizedItemStreamReader<DrugXml> synchronizedItemReader() throws MalformedURLException {
        return new SynchronizedItemStreamReaderBuilder<DrugXml>()
                .delegate(itemReader())
                .build();
    }

    @Bean
    public ItemProcessor<DrugXml, Drug> processor() {
        return new DrugItemProcessor();
    }

    @Bean
    public ItemWriter<Drug> writer() {
        return new DrugItemWriter(drugRepository, drugPackRepository);
    }

    @Bean
    public Step step() throws Exception {
        return new StepBuilder("xmlImport", jobRepository)
                .<DrugXml, Drug>chunk(1000, transactionManager)
                .reader(synchronizedItemReader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runDrugJob() throws Exception {
        return new JobBuilder("importDrugs", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionListener)
                .flow(step())
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(20);
        return asyncTaskExecutor;
    }
}

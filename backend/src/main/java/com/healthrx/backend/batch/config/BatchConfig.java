package com.healthrx.backend.batch.config;

import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.batch.utils.DrugXml;
import com.healthrx.backend.repository.DrugPackRepository;
import com.healthrx.backend.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public StaxEventItemReader<DrugXml> itemReader() {
        Jaxb2Marshaller drugMarshaller = new Jaxb2Marshaller();
        drugMarshaller.setClassesToBeBound(DrugXml.class);

        return new StaxEventItemReaderBuilder<DrugXml>()
                .name("drugReader")
                .resource(new FileSystemResource("src/main/resources/leki.xml"))
                .addFragmentRootElements("{http://rejestrymedyczne.ezdrowie.gov.pl/rpl/eksport-danych-v1.0}produktLeczniczy")
                .unmarshaller(drugMarshaller)
                .build();
    }

    @Bean
    public ItemProcessor<DrugXml, Drug> processor() {
//        return new DrugItemProcessor();
        return drugXml -> {
            Drug drug = new Drug();
            drug.setId(drugXml.getId());
            drug.setName(drugXml.getName());
            drug.setPower(truncate(drugXml.getPower(), 512));
            drug.setPharmaceuticalFormName(truncate(drugXml.getPharmaceuticalFormName(), 512));
            drug.setCompany(drugXml.getCompany());
            drug.setProcedureType(drugXml.getProcedureType());
            drug.setPermitNumber(drugXml.getPermitNumber());
            drug.setPermitExpiration(drugXml.getPermitExpiration());
            drug.setInfo(truncate(drugXml.getInfo(), 1024));
            drug.setCharacteristic(truncate(drugXml.getCharacteristic(), 1024));
//            drug.setAtcCode(truncate(String.join(",", drugXml.getAtcCodes().getCodes()), 512));
            return drug;
        };
    }

    private String truncate(String value, int length) {
        return value != null && value.length() > length ? value.substring(0, length) : value;
    }

    @Bean
    public ItemWriter<Drug> writer() {
        RepositoryItemWriter<Drug> writer = new RepositoryItemWriter<>();
        writer.setRepository(drugRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step() throws Exception {
        return new StepBuilder("xmlImport", jobRepository)
                .<DrugXml, Drug>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job runDrugJob() throws Exception {
        return new JobBuilder("importDrugs", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step())
                .end()
                .build();
//                .start(step())
//                .build();
    }
}

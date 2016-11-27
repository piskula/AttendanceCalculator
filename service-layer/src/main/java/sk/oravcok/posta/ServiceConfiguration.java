package sk.oravcok.posta;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.*;
import sk.oravcok.posta.service.EmployeeServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * configuration of service layer
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
@Configuration
@EnableAspectJAutoProxy
@Import(PersistenceApplicationContext.class)
@ComponentScan(basePackages = "sk.oravcok.posta")
public class ServiceConfiguration {

    @Bean
    public Mapper dozer() {
        // this is needed to support Java 8 time api with Dozer
        List<String> mappingFiles = new ArrayList<>();
        mappingFiles.add("dozerJdk8Converters.xml");

        DozerBeanMapper dozer = new DozerBeanMapper();
        dozer.setMappingFiles(mappingFiles);
        return dozer;
    }

}

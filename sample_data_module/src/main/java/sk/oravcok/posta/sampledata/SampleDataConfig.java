package sk.oravcok.posta.sampledata;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import sk.oravcok.posta.ServiceConfiguration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Ondrej Oravcok
 * @version 25-Dec-16.
 */
@Configuration
@Import(ServiceConfiguration.class)
@ComponentScan(basePackageClasses = SampleDataFacade.class)
public class SampleDataConfig {

    @Inject
    SampleDataFacade sampleDataFacade;

    @PostConstruct
    public void loadData() throws IOException {
        sampleDataFacade.loadData();
    }

}

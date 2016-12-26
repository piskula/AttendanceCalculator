package sk.oravcok.posta.rest.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sk.oravcok.posta.ServiceConfiguration;
import sk.oravcok.posta.rest.controller.PlaceRestController;
import sk.oravcok.posta.sampledata.SampleDataConfig;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * REST service config
 *
 * @author Ondrej Oravcok
 * @version 25-Dec-16.
 */
@Configuration
@EnableWebMvc
@Import({ServiceConfiguration.class, SampleDataConfig.class})
@ComponentScan(basePackageClasses = PlaceRestController.class)
public class RestContextConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AllPrivilegesInterceptorAdapter());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH));

        //objectMapper.addMixIn(some DTO.class, some other DTOMixIn.class);

        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customJackson2HttpMessageConverter());
    }

}

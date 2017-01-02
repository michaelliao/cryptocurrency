package com.itranswarp.bitcoin.explorer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;

/**
 * Spring Boot Application using Pebble.
 * 
 * @author Michael Liao
 */
@EnableJpaRepositories
@SpringBootApplication
public class BitcoinExplorerApplication {

	@Value("${pebble.cache:false}")
	boolean pebbleCache;

	/**
	 * 使用Pebble作为ViewEngine
	 */
	@Bean
	public PebbleViewResolver pebbleViewResolver() {
		PebbleViewResolver viewResolver = new PebbleViewResolver();
		viewResolver.setPrefix("templates/");
		viewResolver.setSuffix(".html");
		viewResolver.setPebbleEngine(
				new PebbleEngine.Builder().cacheActive(pebbleCache).loader(new ClasspathLoader()).build());
		return viewResolver;
	}

	/**
	 * Customized JSON ObjectMapper.
	 */
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper mapper = new ObjectMapper();
		// disabled features:
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	@Autowired
	ObjectMapper objectMapper;

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurerAdapter() {
			/**
			 * Spring默认把静态资源文件/static/abc.js映射到/abc.js，不利于配置反向代理。配置为保留/static/前缀
			 */
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				super.addResourceHandlers(registry);
				registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
			}

			/**
			 * Json默认序列化设置，增加Java8 Time支持
			 */
			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
				converter.setObjectMapper(objectMapper);
				converters.add(converter);
				super.configureMessageConverters(converters);
			}
		};
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BitcoinExplorerApplication.class, args);
	}

}

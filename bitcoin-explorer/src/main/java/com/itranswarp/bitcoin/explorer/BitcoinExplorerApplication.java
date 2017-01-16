package com.itranswarp.bitcoin.explorer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;

/**
 * Spring Boot Application using Pebble.
 * 
 * @author Michael Liao
 */
@EnableScheduling
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
		viewResolver.setSuffix("");
		viewResolver.setPebbleEngine(new PebbleEngine.Builder().cacheActive(pebbleCache).loader(new ClasspathLoader())
				.extension(createExtension()).build());
		return viewResolver;
	}

	Extension createExtension() {
		return new AbstractExtension() {
			@Override
			public Map<String, Filter> getFilters() {
				Map<String, Filter> map = new HashMap<>();
				map.put("d", new DateFilter());
				map.put("dt", new DateTimeFilter());
				map.put("smartdt", new SmartDateTimeFilter());
				map.put("size", new SizeFilter());
				return map;
			}
		};
	}

	/**
	 * Customized JSON ObjectMapper.
	 */
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
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

class DateFilter implements Filter {

	final ZoneId ZONE_ID = ZoneId.systemDefault();
	final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		long n = (Long) input;
		Instant instant = Instant.ofEpochMilli(n);
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZONE_ID);
		return ldt.format(FORMATTER);
	}
}

class DateTimeFilter implements Filter {

	final ZoneId ZONE_ID = ZoneId.systemDefault();
	final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		long n = (Long) input;
		Instant instant = Instant.ofEpochMilli(n);
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZONE_ID);
		return ldt.format(FORMATTER);
	}
}

class SmartDateTimeFilter implements Filter {

	final ZoneId ZONE_ID = ZoneId.systemDefault();
	final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		long n = 1000 * (Long) input;
		long current = System.currentTimeMillis();
		long minutes = (current - n) / 60000L;
		if (minutes < 1) {
			return "1分钟前";
		}
		if (minutes < 60) {
			return minutes + "分钟前";
		}
		Instant instant = Instant.ofEpochMilli(n);
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZONE_ID);
		return ldt.format(FORMATTER);
	}
}

class SizeFilter implements Filter {
	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		long n = (Long) input;
		if (n < 1024) {
			return n + " bytes";
		}
		if (n < 1024 * 1024) {
			double d = n / 1024.0;
			return String.format("0.2f kB", d);
		} else {
			double d = n / (1024.0 * 1024.0);
			return String.format("0.2f MB", d);
		}
	}
}

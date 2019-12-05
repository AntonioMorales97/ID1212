package se.kth.id1212.currencyserv.currency.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * Loads all necessary configuration for the currency converter application. There are 
 * also some additional configurations in the file <code>application.properties</code>.
 * 
 * @author Antonio
 *
 */
@EnableTransactionManagement
@EnableWebMvc
@Configuration
public class CurrencyConfig implements WebMvcConfigurer, ApplicationContextAware{
	private ApplicationContext applicationContext;

	/**
	 * Sets the application context used by the running application.
	 * 
	 * @param appContext The application context used by the running application.
	 */
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		this.applicationContext = appContext;
	}

	/**
	 * This will set the running applications only view resolver. It will delegate
	 * all views to the template engine of thymeleaf.
	 * 
	 * @return the set <code>ThymeleafViewResolver</code>.
	 */
	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setContentType("text/html; charset=UTF-8");
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	/**
	 * This will create a thymeleaf template engine bean that will manage the
	 * integration between thymeleaf and Spring.
	 * 
	 * @return the created template engine.
	 */
	@Bean(name = "currencyTemplateEngine")
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		templateEngine.addDialect(new LayoutDialect());
		return templateEngine;
	}

	/**
	 * Creates the only template resolver in this application that will handle
	 * thymeleaf template integration with Spring.
	 * 
	 * @return the created template resolver.
	 */
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("classpath:/web-root/");
		templateResolver.setSuffix(".html");

		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(true);
		return templateResolver;
	}

	/**
	 * Sets configuration so the requests of static files will be
	 * directed to the right path etc.
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		int cachePeriodForStaticFilesInSecs = 1;
		String rootDirForStaticFiles = "classpath:/web-root/";

		registry.addResourceHandler("/**")
		.addResourceLocations(rootDirForStaticFiles)
		.setCachePeriod(cachePeriodForStaticFilesInSecs)
		.resourceChain(true).addResolver(new PathResourceResolver());
	}

}

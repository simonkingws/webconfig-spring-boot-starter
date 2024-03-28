package io.github.simonkingws.webconfig.core.config;

import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import io.github.simonkingws.webconfig.common.core.WebconfigProperies;
import io.github.simonkingws.webconfig.core.Interceptor.RequestContextInterceptor;
import com.taobao.arthas.agent.attach.ArthasAgent;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动装配
 *
 * @author: ws
 * @date: 2024/1/30 19:04
 */
@Configuration
@ConditionalOnClass(WebconfigProperies.class)
@EnableConfigurationProperties(WebconfigProperies.class)
@ComponentScan("io.github.simonkingws.webconfig")
public class WebconfigAutoConfigration implements WebMvcConfigurer {

    @Value("${spring.application.name:boot-server}")
    private String applicationName;

    @Autowired
    private WebconfigProperies webconfigProperies;
    @Autowired
    private RequestContextInterceptor requestContextIntercepter;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String excludePathPatterns = webconfigProperies.getExcludePathPatterns();
        String[] epps = {};
        if (StringUtils.isNotBlank(excludePathPatterns)) {
            epps = excludePathPatterns.split(SymbolConstant.COMMA);
        }

        registry.addInterceptor(requestContextIntercepter)
                .addPathPatterns(webconfigProperies.getInterceptorPathPatterns().split(SymbolConstant.COMMA))
                .excludePathPatterns(epps)
                .order(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * Validator 校验快速失败配置
     */
    @Override
    public Validator getValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(webconfigProperies.getValidFailFast())
                .buildValidatorFactory();

        return new SpringValidatorAdapter(validatorFactory.getValidator());
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @PostConstruct
    public void initArthasAgent() {
        if (BooleanUtils.isTrue(webconfigProperies.getArthasOpen())) {
            if (BooleanUtils.isTrue(webconfigProperies.getArthasIsSelfConfig())) {
                return;
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put("arthas.arthasIp", webconfigProperies.getArthasIp());
            configMap.put("arthas.agentId", webconfigProperies.getArthasAgentId());
            configMap.put("arthas.appName", applicationName);
            configMap.put("arthas.tunnelServer", webconfigProperies.getArthasTunnelServer());
            ArthasAgent.attach(configMap);

            return;
        }

        // 关闭arthas
        Map<String, String> configMap = new HashMap<>();
        configMap.put("arthas.telnetPort", "-1");
        configMap.put("arthas.httpPort", "-1");
        configMap.put("arthas.appName", applicationName);
        ArthasAgent.attach(configMap);
    }
}

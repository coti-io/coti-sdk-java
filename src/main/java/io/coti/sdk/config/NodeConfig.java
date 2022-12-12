package io.coti.sdk.config;

import io.coti.basenode.crypto.NodeCryptoHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(
        basePackages = {"io.coti.basenode.crypto", "io.coti.basenode.data", "io.coti.basenode.http"}
)
@EnableScheduling
@PropertySource("classpath:application.properties")
public class NodeConfig {

    @Value("#{'${node.private.key}'}")
    private String nodePrivateKey;

    @Bean
    public MethodInvokingFactoryBean nodePrivateKey() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod(NodeCryptoHelper.class.getName() + ".nodePrivateKey");
        methodInvokingFactoryBean.setArguments(nodePrivateKey);

        return methodInvokingFactoryBean;
    }
}
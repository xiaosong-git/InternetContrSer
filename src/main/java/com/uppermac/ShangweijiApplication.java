package com.uppermac;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.uppermac.data.Constants;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.utils.WebSocketUtil;


@SpringBootApplication
@EnableScheduling
public class ShangweijiApplication extends SpringBootServletInitializer {

	
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ShangweijiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(ShangweijiApplication .class);
        springApplication.run(args);
	}

	@Bean
	public WebSocketClient websocketClient() {
		try {
			//WebSocketUtil websocket = new WebSocketUtil(new URI(Constants.listenSocketURI));
			WebSocketUtil websocket = new WebSocketUtil(new URI("ws://47.99.209.40:8086/myHandler"));
			websocket.connect();
			return websocket;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
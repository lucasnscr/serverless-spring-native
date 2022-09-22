package com.lucasnscr.serverless.function2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;

@NativeHint(
		resources = {@ResourceHint(patterns = {
				"org.joda.time.tz.*"
		})})
@SpringBootApplication
public class Function2Application {

	public static void main(String[] args) {
		SpringApplication.run(Function2Application.class, args);
	}

}

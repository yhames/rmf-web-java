package com.rmf.apiserverjava.config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		Components components = new Components();
		Info info = apiInfo();
		return new OpenAPI().components(components).info(info);
	}

	private Info apiInfo() {
		return new Info()
			.title("Open-RMF API")
			.description("현대오토에버 로봇서비스 API 고도화 프로젝트 Open-RMF API");
	}

	@Bean
	public OpenApiCustomizer customiseLoginApi() {
		return openApi -> openApi.path("/login", new io.swagger.v3.oas.models.PathItem()
			.post(new io.swagger.v3.oas.models.Operation()
				.summary("Login")
				.operationId("login")
				.tags(List.of("Auth"))
				.description("로그인을 통해 사용자 인증을 수행합니다. 로그인에 성공하면 Access, Refresh JWT 토큰을 쿠키에 저장합니다.\n"
					+ " username: root / password: root 를 통해 관리자 계정으로 로그인할 수 있습니다.")
				.requestBody(new RequestBody()
					.description("로그인을 위한 사용자 정보를 입력합니다.")
					.content(
						new Content().addMediaType("application/x-www-form-urlencoded",
						new MediaType().schema(new ObjectSchema()
							.addProperty("username", new StringSchema()
								.description("유저의 아이디입니다.")._default("root"))
							.addProperty("password", new StringSchema()
								.description("유저의 패스워드 입니다.")._default("root")))))
					.required(true))
				.responses(new io.swagger.v3.oas.models.responses.ApiResponses()
					.addApiResponse("200", new ApiResponse().description("로그인에 성공하였습니다.."))
					.addApiResponse("401", new ApiResponse().description("로그인에 실패하였습니다.")))));
	}
}

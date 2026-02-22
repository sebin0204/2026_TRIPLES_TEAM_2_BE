package com.team2.fabackend.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
@Configuration
public class PromptConfig {

    // 환경변수가 없으면 classpath에서, 있으면 해당 경로(file:)에서 읽어옵니다.
    @Value("${prompt.advice.system:classpath:prompts/advice/generateAdviceSystem.st}")
    private Resource adviceSystemResource;

    @Value("${prompt.advice.user:classpath:prompts/advice/generateAdvice.st}")
    private Resource adviceResource;

    @Value("${prompt.aireport.system:classpath:prompts/aiReport/generateAiReportSystem.st}")
    private Resource aiReportSystemResource;

    @Value("${prompt.aireport.user:classpath:prompts/aiReport/generateAiReport.st}")
    private Resource aiReportResource;

    @Bean
    public PromptTemplate generateAdviceSystemPrompt() throws IOException {
        return createPromptTemplate(adviceSystemResource);
    }

    @Bean
    public PromptTemplate generateAdvicePrompt() throws IOException {
        return createPromptTemplate(adviceResource);
    }

    @Bean
    public PromptTemplate generateAiReportSystemPrompt() throws IOException {
        return createPromptTemplate(aiReportSystemResource);
    }

    @Bean
    public PromptTemplate generateAiReportPrompt() throws IOException {
        return createPromptTemplate(aiReportResource);
    }

    private PromptTemplate createPromptTemplate(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new PromptTemplate(content);
        }
    }
}
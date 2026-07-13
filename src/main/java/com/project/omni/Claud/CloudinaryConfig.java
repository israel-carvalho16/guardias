package com.project.omni.Claud; // Garanta que o pacote está correto conforme sua estrutura

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "SEU_CLOUD_NAME",
            "api_key", "SUA_API_KEY",
            "api_secret", "SUA_API_SECRET"
        ));
    }
}
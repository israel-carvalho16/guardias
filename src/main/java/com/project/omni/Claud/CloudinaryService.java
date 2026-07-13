package com.project.omni.Claud;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImagem(MultipartFile arquivo) throws IOException {
        // Envia o arquivo para o Cloudinary
        Map uploadResult = cloudinary.uploader().upload(arquivo.getBytes(), ObjectUtils.emptyMap());
        
        // Retorna a URL segura (https) da imagem armazenada
        return uploadResult.get("secure_url").toString();
    }
}
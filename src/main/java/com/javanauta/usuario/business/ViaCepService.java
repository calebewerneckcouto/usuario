package com.javanauta.usuario.business;

import com.javanauta.usuario.infrastructure.clients.ViaCepClient;
import com.javanauta.usuario.infrastructure.clients.ViaCepDTO;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient client;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ViaCepDTO buscarDadosEndereco(String cep) {

        try {
            String cepLimpo = processarCep(cep);
            String key = "enderecos" + cepLimpo;

            String dadosCacheRedis = redisTemplate.opsForValue().get(key);

            if (Objects.nonNull(dadosCacheRedis)) {
                return objectMapper.readValue(dadosCacheRedis, ViaCepDTO.class);
            }

            ViaCepDTO dto = client.buscaDadosEndereco(cepLimpo);
            if (Objects.nonNull(dto) && Objects.nonNull(dto.getCep())) {
                String stringDTO = objectMapper.writeValueAsString(dto);
                redisTemplate.opsForValue().set(key, stringDTO, Duration.ofDays(15));
            }

            return dto;

        } catch (Exception e) {
            throw new ResourceNotFoundException("Erro ao obter cep");
        }
    }

    private String processarCep(String cep) {
        if (cep == null) {
            throw new IllegalArgumentException("O cep contem caracteres invalidos");
        }

        String cepFormatado = cep.replace(" ", "").replace("-", "");

        if (!cepFormatado.matches("\\d{8}")) {
            throw new IllegalArgumentException("O cep contem caracteres invalidos");
        }

        return cepFormatado;
    }
}

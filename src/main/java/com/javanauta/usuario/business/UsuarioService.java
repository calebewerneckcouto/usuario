package com.javanauta.usuario.business;

import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.exceptions.ConflictException;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email ja cadastrado" + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email ja cadstrado", e.getCause());

        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email nao encontrado" + email));


    }


    @Transactional
    public void deletaUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email nao encontrado" + email));
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public UsuarioDTO atualizaDadosUsuario(UsuarioDTO usuarioDTO) {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuarioEntity = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Email nao localizado"));

        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().equals(emailLogado)
                && verificaEmailExistente(usuarioDTO.getEmail())) {
            throw new ConflictException("Email ja cadastrado: " + usuarioDTO.getEmail());
        }

        if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isBlank()) {
            usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        } else {
            usuarioDTO.setSenha(null);
        }

        usuarioConverter.updateUsuario(usuarioDTO, usuarioEntity);
        return usuarioConverter.paraUsuarioDTO(usuarioEntity);
    }

}

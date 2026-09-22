package com.javanauta.usuario.business;

import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.*;
import com.javanauta.usuario.infrastructure.entity.Endereco;
import com.javanauta.usuario.infrastructure.entity.Telefone;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.exceptions.ConflictException;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.infrastructure.repository.EnderecoRepository;
import com.javanauta.usuario.infrastructure.repository.TelefoneRepository;
import com.javanauta.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

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

    @Transactional(readOnly = true)
    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findFirstByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email nao encontrado: " + email));
        return usuarioConverter.paraUsuarioDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarTodosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioConverter::paraUsuarioDTO)
                .toList();
    }


    @Transactional
    public void deletaUsuarioPorEmail(String email) {
        if (!usuarioRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("Email nao encontrado" + email);
        }
        usuarioRepository.deleteByEmail(email);
    }

    @Transactional
    public UsuarioDTO atualizaDadosUsuario(UsuarioDTO usuarioDTO) {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuarioEntity = usuarioRepository.findFirstByEmail(emailLogado)
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


    @Transactional
    public EnderecoDTO atualizaEndereco(Long id, EnderecoDTO enderecoDTO) {
        Endereco entity = enderecoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado" + id));
        usuarioConverter.updateEndereco(enderecoDTO, entity);
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(entity));
    }

    @Transactional
    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO) {
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado" + idTelefone));
        usuarioConverter.updateTelefone(telefoneDTO, entity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(entity));
    }


    @Transactional
    public EnderecoDTO cadastraEndereco(EnderecoDTO enderecoDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findFirstByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado: " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(enderecoDTO, usuario.getId());
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    @Transactional
    public TelefoneDTO cadastraTelefone(TelefoneDTO telefoneDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findFirstByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado: " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(telefoneDTO, usuario.getId());
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    @Transactional
    public void deletaTelefone(Long id) {
        Telefone telefone = telefoneRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado" + id));
        telefoneRepository.delete(telefone);
    }

    @Transactional
    public void deletaEndereco(Long id) {
        Endereco endereco = enderecoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Id nao encontrado" + id));
        enderecoRepository.delete(endereco);
    }


    @Transactional
    public void alteraSenha(AlterarSenhaDTO dto) {
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha invalida");
        }

        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findFirstByEmail(emailLogado).orElseThrow(() -> new ResourceNotFoundException("Email não localizado"));
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public RecuperarSenhaDTO recuperarSenha(String email) {
        Usuario usuario = usuarioRepository.findFirstByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email não encontrado"));

        String senhaAleatoria = gerarSenhaAleatoria();
        usuario.setSenha(passwordEncoder.encode(senhaAleatoria));
        usuarioRepository.save(usuario);

        return RecuperarSenhaDTO.builder()
                .email(usuario.getEmail())
                .senha(senhaAleatoria)
                .build();

    }

    private String gerarSenhaAleatoria() {
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            senha.append(random.nextInt(10));
        }

        return senha.toString();
    }


}

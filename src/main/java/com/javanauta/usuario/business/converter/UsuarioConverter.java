package com.javanauta.usuario.business.converter;

import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Endereco;
import com.javanauta.usuario.infrastructure.entity.Telefone;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UsuarioConverter {

    public Usuario paraUsuario(UsuarioDTO usuarioDTO) {
        return Usuario.builder()
                .nome(usuarioDTO.getNome())
                .email(usuarioDTO.getEmail())
                .senha(usuarioDTO.getSenha())
                .enderecos(paraListaEndereco(usuarioDTO.getEnderecos()))
                .telefones(paraListaTelefone(usuarioDTO.getTelefones()))
                .build();
    }

    public UsuarioDTO paraUsuarioDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .enderecos(paraListaEnderecoDTO(usuario.getEnderecos()))
                .telefones(paraListaTelefoneDTO(usuario.getTelefones()))
                .build();
    }

    public List<Endereco> paraListaEndereco(List<EnderecoDTO> enderecoDTOS) {
        if (enderecoDTOS == null) {
            return Collections.emptyList();
        }
        return enderecoDTOS.stream().map(this::paraEndereco).toList();
    }

    public Endereco paraEndereco(EnderecoDTO enderecoDTO) {
        return Endereco.builder()
                .rua(enderecoDTO.getRua())
                .numero(enderecoDTO.getNumero())
                .cidade(enderecoDTO.getCidade())
                .complemento(enderecoDTO.getComplemento())
                .cep(enderecoDTO.getCep())
                .estado(enderecoDTO.getEstado())
                .build();
    }

    public List<EnderecoDTO> paraListaEnderecoDTO(List<Endereco> enderecos) {
        if (enderecos == null) {
            return Collections.emptyList();
        }
        return enderecos.stream().map(this::paraEnderecoDTO).toList();
    }

    public EnderecoDTO paraEnderecoDTO(Endereco endereco) {
        return EnderecoDTO.builder()
                .id(endereco.getId())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .cidade(endereco.getCidade())
                .complemento(endereco.getComplemento())
                .cep(endereco.getCep())
                .estado(endereco.getEstado())
                .build();
    }

    public List<Telefone> paraListaTelefone(List<TelefoneDTO> telefoneDTOS) {
        if (telefoneDTOS == null) {
            return Collections.emptyList();
        }
        return telefoneDTOS.stream().map(this::paraTelefone).toList();
    }

    public Telefone paraTelefone(TelefoneDTO telefoneDTO) {
        return Telefone.builder()
                .id(telefoneDTO.getId())
                .numero(telefoneDTO.getNumero())
                .ddd(telefoneDTO.getDdd())
                .build();
    }

    public List<TelefoneDTO> paraListaTelefoneDTO(List<Telefone> telefones) {
        if (telefones == null) {
            return Collections.emptyList();
        }
        return telefones.stream().map(this::paraTelefoneDTO).toList();
    }

    public TelefoneDTO paraTelefoneDTO(Telefone telefone) {
        return TelefoneDTO.builder()
                .id(telefone.getId())
                .numero(telefone.getNumero())
                .ddd(telefone.getDdd())
                .build();
    }


    public void updateUsuario(UsuarioDTO usuarioDTO, Usuario entity) {
        if (usuarioDTO.getNome() != null) {
            entity.setNome(usuarioDTO.getNome());
        }

        if (usuarioDTO.getEmail() != null) {
            entity.setEmail(usuarioDTO.getEmail());
        }

        if (usuarioDTO.getSenha() != null) {
            entity.setSenha(usuarioDTO.getSenha());
        }

        if (usuarioDTO.getEnderecos() != null) {
            if (entity.getEnderecos() == null) {
                entity.setEnderecos(new ArrayList<>());
            } else {
                entity.getEnderecos().clear();
            }
            entity.getEnderecos().addAll(paraListaEndereco(usuarioDTO.getEnderecos()));
        }

        if (usuarioDTO.getTelefones() != null) {
            if (entity.getTelefones() == null) {
                entity.setTelefones(new ArrayList<>());
            } else {
                entity.getTelefones().clear();
            }
            entity.getTelefones().addAll(paraListaTelefone(usuarioDTO.getTelefones()));
        }
    }

    public void updateEndereco(EnderecoDTO enderecoDTO, Endereco endereco) {
        if (enderecoDTO.getRua() != null) {
            endereco.setRua(enderecoDTO.getRua());
        }
        if (enderecoDTO.getNumero() != null) {
            endereco.setNumero(enderecoDTO.getNumero());
        }
        if (enderecoDTO.getCidade() != null) {
            endereco.setCidade(enderecoDTO.getCidade());
        }
        if (enderecoDTO.getCep() != null) {
            endereco.setCep(enderecoDTO.getCep());
        }
        if (enderecoDTO.getComplemento() != null) {
            endereco.setComplemento(enderecoDTO.getComplemento());
        }
        if (enderecoDTO.getEstado() != null) {
            endereco.setEstado(enderecoDTO.getEstado());
        }
    }

    public void updateTelefone(TelefoneDTO telefoneDTO, Telefone entity) {
        if (telefoneDTO.getDdd() != null) {
            entity.setDdd(telefoneDTO.getDdd());
        }
        if (telefoneDTO.getNumero() != null) {
            entity.setNumero(telefoneDTO.getNumero());
        }
    }

    public Endereco paraEnderecoEntity(EnderecoDTO enderecoDTO, Long idUsuario) {
        return Endereco.builder()
                .rua(enderecoDTO.getRua())
                .cidade(enderecoDTO.getCidade())
                .cep(enderecoDTO.getCep())
                .complemento(enderecoDTO.getComplemento())
                .estado(enderecoDTO.getEstado())
                .numero(enderecoDTO.getNumero())
                .usuario_id(idUsuario)
                .build();

    }

    public Telefone paraTelefoneEntity(TelefoneDTO telefoneDTO, Long idUsuario){
        return Telefone.builder()
                .numero(telefoneDTO.getNumero())
                .ddd(telefoneDTO.getDdd())
                .usuario_id(idUsuario)
                .build();
    }


}

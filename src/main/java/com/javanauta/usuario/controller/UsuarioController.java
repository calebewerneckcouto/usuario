package com.javanauta.usuario.controller;

import com.javanauta.usuario.business.UsuarioService;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Endpoints de cadastro, login e consulta de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Cadastrar usuario", description = "Cria um novo usuario no sistema")
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Retorna o token JWT. Copie o campo 'token' e cole no botao Authorize do Swagger."
    )
    public ResponseEntity<Map<String, String>> login(@RequestBody UsuarioDTO usuarioDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), usuarioDTO.getSenha())
        );

        String token = jwtUtil.generateToken(authentication.getName());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "authorization", "Bearer " + token
        ));
    }

    @GetMapping
    @Operation(summary = "Buscar usuario por email")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Usuario> buscaUsuarioPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Deletar usuario por email")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable("email") String email) {
        usuarioService.deletaUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }


    @PutMapping
    @Operation(summary = "Atualizar usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UsuarioDTO> atualizaDadoUsuario(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(dto));
    }

}

package com.javanauta.usuario.infrastructure.repository;

import com.javanauta.usuario.infrastructure.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    Optional<Usuario> findByEmail(String email);

    void deleteByEmail(String email);
}

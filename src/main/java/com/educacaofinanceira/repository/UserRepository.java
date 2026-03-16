package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);

    List<User> findByFamilyIdAndRole(UUID familyId, UserRole role);

    /**
     * Busca usuário por email com EAGER fetch do Family
     * Evita LazyInitializationException em contextos multi-transacionais
     */
    @Query("SELECT u FROM User u JOIN FETCH u.family WHERE u.email = :email")
    Optional<User> findByEmailWithFamily(@Param("email") String email);

    /**
     * Busca usuário por username com EAGER fetch do Family
     * Evita LazyInitializationException em contextos multi-transacionais
     */
    @Query("SELECT u FROM User u JOIN FETCH u.family WHERE u.username = :username")
    Optional<User> findByUsernameWithFamily(@Param("username") String username);
}

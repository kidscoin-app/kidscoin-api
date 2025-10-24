package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.UserXP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserXPRepository extends JpaRepository<UserXP, UUID> {

    Optional<UserXP> findByUserId(UUID userId);
}

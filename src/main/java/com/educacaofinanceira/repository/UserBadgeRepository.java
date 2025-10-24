package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {

    boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId);

    List<UserBadge> findByUserId(UUID userId);
}

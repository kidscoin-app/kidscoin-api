package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardRepository extends JpaRepository<Reward, UUID> {

    List<Reward> findByFamilyId(UUID familyId);

    List<Reward> findByFamilyIdAndIsActive(UUID familyId, Boolean isActive);
}

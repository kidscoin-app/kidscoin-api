package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.Redemption;
import com.educacaofinanceira.model.enums.RedemptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, UUID> {

    List<Redemption> findByStatus(RedemptionStatus status);

    List<Redemption> findByChildId(UUID childId);

    long countByChildIdAndStatus(UUID childId, RedemptionStatus status);
}

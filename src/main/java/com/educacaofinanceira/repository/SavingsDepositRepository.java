package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.SavingsDeposit;
import com.educacaofinanceira.model.enums.SavingsDepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsDepositRepository extends JpaRepository<SavingsDeposit, UUID> {

    List<SavingsDeposit> findBySavingsIdAndStatusOrderByDepositedAtAsc(UUID savingsId, SavingsDepositStatus status);

    List<SavingsDeposit> findBySavingsIdOrderByDepositedAtDesc(UUID savingsId);
}

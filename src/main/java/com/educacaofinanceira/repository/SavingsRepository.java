package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, UUID> {

    Optional<Savings> findByChildId(UUID childId);

    List<Savings> findAllByBalanceGreaterThan(Integer balance);
}

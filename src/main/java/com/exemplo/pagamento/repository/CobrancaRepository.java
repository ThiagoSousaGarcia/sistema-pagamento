package com.exemplo.pagamento.repository;

import com.exemplo.pagamento.domain.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, String> {
    Optional<Cobranca> findByCodigo(String codigo);
}

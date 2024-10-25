package com.exemplo.pagamento.repository;

import com.exemplo.pagamento.domain.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, String> {
    boolean existsByCodigo(String codigo);
}

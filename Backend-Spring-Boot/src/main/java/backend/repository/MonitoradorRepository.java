package backend.repository;

import backend.entitie.Monitorador;
import backend.enums.TipoPessoa;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface MonitoradorRepository extends JpaRepository<Monitorador, Long>, JpaSpecificationExecutor<Monitorador> {

    List<Monitorador> findByTipoPessoa(TipoPessoa tipoPessoa);

    default List<Monitorador> filtrar(Monitorador filtro) {
        return findAll((Specification<Monitorador>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getNome() != null && !filtro.getNome().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("nome"), "%" + filtro.getNome() + "%"));
            }
            if (filtro.getRazaoSocial() != null && !filtro.getRazaoSocial().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("razaoSocial"), "%" + filtro.getRazaoSocial() + "%"));
            }
            if (filtro.getCpf() != null && !filtro.getCpf().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cpf"), "%" + filtro.getCpf() + "%"));
            }
            if (filtro.getCnpj() != null && !filtro.getCnpj().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cnpj"), "%" + filtro.getCnpj() + "%"));
            }
            if (filtro.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filtro.getStatus()));
            }
            if (filtro.getTipoPessoa() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoPessoa"), filtro.getTipoPessoa()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

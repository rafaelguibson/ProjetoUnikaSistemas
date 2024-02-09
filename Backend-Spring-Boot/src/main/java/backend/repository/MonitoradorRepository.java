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

    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);

    default List<Monitorador> filtrar(Monitorador filtro) {
        return findAll((Specification<Monitorador>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getNome() != null && !filtro.getNome().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + filtro.getNome().toLowerCase() + "%"));
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
            if (filtro.getRg() != null && !filtro.getRg().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("rg")), "%" + filtro.getRg().toLowerCase() + "%"));
            }
            if (filtro.getInscricaoEstadual() != null && !filtro.getInscricaoEstadual().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("inscricaoEstadual")), "%" + filtro.getInscricaoEstadual().toLowerCase() + "%"));
            }

            if (filtro.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filtro.getStatus()));
            }
            if (filtro.getTipoPessoa() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoPessoa"), filtro.getTipoPessoa()));
            }
            if (filtro.getDataNascimento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("dataNascimento"), filtro.getDataNascimento()));
            }
            if (filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
                predicates.add(criteriaBuilder.between(root.get("dataCadastro"), filtro.getDataInicial(), filtro.getDataFinal()));
            } else if (filtro.getDataInicial() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataCadastro"), filtro.getDataInicial()));
            } else if (filtro.getDataFinal() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataCadastro"), filtro.getDataFinal()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

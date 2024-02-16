package Backend.repository;

import Backend.entitie.Monitorador;
import Backend.enums.TipoPessoa;

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



    boolean existsByCpfCnpj(String cpfCnpj);


    default List<Monitorador> filtrar(Monitorador filtro) {
        return findAll((Specification<Monitorador>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getNomeRazaoSocial() != null && !filtro.getNomeRazaoSocial().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nomeRazaoSocial")),
                        "%" + filtro.getNomeRazaoSocial().toLowerCase() + "%"));
            }
            if (filtro.getCpfCnpj() != null && !filtro.getCpfCnpj().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cpfCnpj"), "%" + filtro.getCpfCnpj() + "%"));
            }
            if (filtro.getRgIe() != null && !filtro.getRgIe().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("rgIe"), "%" + filtro.getRgIe() + "%"));
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

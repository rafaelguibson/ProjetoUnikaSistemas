package backend.repository;
import backend.entitie.Monitorador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public interface MonitoradorRepository extends JpaRepository<Monitorador, Long> {
    List<Monitorador> findByTipoPessoa(String tipoPessoa);
    @Autowired
    EntityManager entityManager = null; // Autowire o EntityManager

    default List<Monitorador> findByFilter(Monitorador filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Monitorador> cq = cb.createQuery(Monitorador.class);

        Root<Monitorador> monitorador = cq.from(Monitorador.class);
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getNome() != null && !filter.getNome().isEmpty()) {
            predicates.add(cb.like(monitorador.get("nome"), "%" + filter.getNome() + "%"));
        }
        if (filter.getCpf() != null && !filter.getCpf().isEmpty()) {
            predicates.add(cb.equal(monitorador.get("cpf"), filter.getCpf()));
        }
        if (filter.getRg() != null && !filter.getRg().isEmpty()) {
            predicates.add(cb.equal(monitorador.get("rg"), filter.getRg()));
        }
        if (filter.getDataNascimento() != null) {
            predicates.add(cb.equal(monitorador.get("dataNascimento"), filter.getDataNascimento()));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Monitorador> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}


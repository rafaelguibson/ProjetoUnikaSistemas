package backend.repository;
import backend.entitie.Monitorador;
import backend.enums.TipoMonitorador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoradorRepository extends JpaRepository<Monitorador, Long> {
    List<Monitorador> findByTipoPessoa(String tipoPessoa);
}


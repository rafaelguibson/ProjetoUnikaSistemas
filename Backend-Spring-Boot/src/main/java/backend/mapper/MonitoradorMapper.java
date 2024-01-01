package backend.mapper;
import backend.dto.MonitoradorDTO;
import backend.entitie.Monitorador;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

public class MonitoradorMapper {

    public static MonitoradorDTO toDTO(Monitorador monitorador) {
        MonitoradorDTO dto = new MonitoradorDTO();
        try {
            BeanUtils.copyProperties(dto, monitorador);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return dto;
    }

    public static Monitorador toEntity(MonitoradorDTO dto) {
        Monitorador monitorador = new Monitorador();
        try {
            BeanUtils.copyProperties(monitorador, dto);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return monitorador;
    }
}

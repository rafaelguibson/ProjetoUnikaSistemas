package backend.mapper;

import backend.dto.EnderecoDTO;
import backend.entitie.Endereco;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

public class EnderecoMapper {

    public static EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        EnderecoDTO dto = new EnderecoDTO();
        try {
            BeanUtils.copyProperties(dto, endereco);
            dto.setMonitoradorId(endereco.getMonitorador().getId()); // Exemplo de mapeamento manual
        } catch (IllegalAccessException | InvocationTargetException e) {
            // Handle the exceptions
        }
        return dto;
    }

    public static Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        Endereco endereco = new Endereco();
        try {
            BeanUtils.copyProperties(endereco, dto);
        } catch (IllegalAccessException | InvocationTargetException e) {

        }
        return endereco;
    }
}


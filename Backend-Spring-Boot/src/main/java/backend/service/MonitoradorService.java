package backend.service;

import backend.entitie.Endereco;
import backend.entitie.Monitorador;
import backend.enums.TipoPessoa;
import backend.repository.EnderecoRepository;
import backend.repository.MonitoradorRepository;
import backend.validators.CampoObrigatorioException;
import backend.validators.CpfCnpjInvalidoException;
import backend.validators.EnderecoInvalidaException;
import backend.validators.NomeRazaoSocialInvalidaException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class MonitoradorService {

    @Autowired
    private final MonitoradorRepository monitoradorRepository;

    @Autowired
    private final EnderecoRepository enderecoRepository;
    private static final Pattern CPF_PATTERN =
            Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}");

    @Autowired
    public MonitoradorService(MonitoradorRepository monitoradorRepository, EnderecoRepository enderecoRepository) {
        this.monitoradorRepository = monitoradorRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public Monitorador saveMonitorador(@Valid Monitorador monitorador) {


        return monitoradorRepository.save(removerMascaras(monitorador));
    }

    @Transactional
    public Monitorador salvarMonitoradorComEnderecos(@Valid Monitorador monitorador) {
        //validação monitorador
        validarMonitorador(monitorador);

        //salva o monitorador
        Monitorador monitoradorSalvo = monitoradorRepository.save(monitorador);

        //associa os endereços ao monitorador e salve os endereços
        List<Endereco> enderecos = monitorador.getEnderecos();
        if (enderecos != null && !enderecos.isEmpty()) {
            for (Endereco endereco : enderecos) {
                endereco.setMonitorador(monitoradorSalvo);
                enderecoRepository.save(endereco);
            }
        }

        // a o monitorador salvo com os endereços associados
        return monitoradorSalvo;
    }


    public Optional<Monitorador> getMonitoradorById(Long id) {
        return monitoradorRepository.findById(id);
    }

    public List<Monitorador> getAllMonitoradores() {
        return monitoradorRepository.findAll();
    }

    public Monitorador updateMonitorador(Monitorador monitorador) {
        return monitoradorRepository.save(monitorador);
    }

    public void deleteMonitorador(Long id) {
        monitoradorRepository.deleteById(id);
    }

    public List<Monitorador> buscarPF() {
        return monitoradorRepository.findByTipoPessoa(TipoPessoa.PF);
    }

    public List<Monitorador> buscarPJ() {
        return monitoradorRepository.findByTipoPessoa(TipoPessoa.PJ);
    }

    public List<Monitorador> filtrar(Monitorador filtro) {
        return monitoradorRepository.filtrar(filtro);
    }

    public void deleteAllMonitoradores(List<Monitorador> list) {
        monitoradorRepository.deleteAll(list);
    }
    public Monitorador removerMascaras(Monitorador monitorador) {
        if(monitorador.getTipoPessoa().equals(TipoPessoa.PF)) {
            monitorador.setCpf(monitorador.getCpf().replaceAll("\\D", ""));
        }
        if(monitorador.getTipoPessoa().equals(TipoPessoa.PJ)) {
            monitorador.setCnpj(monitorador.getCnpj().replaceAll("\\D", ""));
        }
        monitorador.setTelefone(monitorador.getTelefone().replaceAll("\\D", ""));
        return monitorador;
    }

    public void validarMonitorador(Monitorador monitorador) {
        if (monitorador.getTipoPessoa() == TipoPessoa.PF) {
            validarCampoObrigatorio(monitorador.getNome(), "Nome");
            validarCampoObrigatorio(monitorador.getCpf(), "CPF");
            validarCPF(monitorador.getCpf());
            validarCampoObrigatorio(monitorador.getRg(), "RG");
            validarCampoObrigatorio(monitorador.getTelefone(), "Telefone");
            validarCampoObrigatorio(monitorador.getDataNascimento(), "Data de Nascimento");
            validarCampoObrigatorio(monitorador.getStatus(), "Status");
        } else if (monitorador.getTipoPessoa() == TipoPessoa.PJ) {
            validarCampoObrigatorio(monitorador.getRazaoSocial(), "Razão Social");
            validarCampoObrigatorio(monitorador.getCnpj(), "CNPJ");
            validarCampoObrigatorio(monitorador.getInscricaoEstadual(), "Inscrição Estadual");
            validarCampoObrigatorio(monitorador.getTelefone(), "Telefone");
            validarCampoObrigatorio(monitorador.getStatus(), "Status");
        }

        validarCampoObrigatorio(monitorador.getEnderecos(), "Endereços");
    }

    private void validarCampoObrigatorio(Object campo, String nomeCampo) {
        if (Objects.isNull(campo) || (campo instanceof String && ((String) campo).isEmpty())
                || (campo instanceof Collection && ((Collection<?>) campo).isEmpty())) {
            throw new CampoObrigatorioException(nomeCampo);
        }
    }

    public void validarCPF(String cpf) throws IllegalArgumentException {
        if (!CPF_PATTERN.matcher(cpf).matches()) {
            throw new IllegalArgumentException("CPF inválido. Formato esperado: XXX.XXX.XXX-XX");
        }
        // Aqui você pode adicionar lógica adicional para validar os dígitos verificadores do CPF, se necessário.
    }

}


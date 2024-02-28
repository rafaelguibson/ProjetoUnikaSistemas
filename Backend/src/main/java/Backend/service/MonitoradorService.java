package Backend.service;

import Backend.entitie.Endereco;
import Backend.entitie.Monitorador;
import Backend.enums.TipoPessoa;
import Backend.exceptions.CampoObrigatorioException;
import Backend.exceptions.DataNascimentoException;
import Backend.exceptions.FileSizeException;
import Backend.exceptions.RegistroDuplicadoException;
import Backend.repository.EnderecoRepository;
import Backend.repository.MonitoradorRepository;
import Backend.service.ExcelService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MonitoradorService {
    private static final Pattern CPF_PATTERN =
            Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private final MonitoradorRepository monitoradorRepository;
    private final ExcelService excelService;
    private final ReportService reportService;
    private final EnderecoRepository enderecoRepository;

    @Autowired
    public MonitoradorService(MonitoradorRepository monitoradorRepository, ExcelService excelService, ReportService reportService, EnderecoRepository enderecoRepository) {
        this.monitoradorRepository = monitoradorRepository;
        this.excelService = excelService;
        this.reportService = reportService;
        this.enderecoRepository = enderecoRepository;
    }

    public Monitorador saveMonitorador(@Valid Monitorador monitorador) {
        return monitoradorRepository.save(monitorador);
    }

    public List<Monitorador> saveAllMonitorador(@Valid List<Monitorador> monitorador) {
        validarDuplicadosLista(monitorador);
        for(Monitorador item: monitorador) {
            salvarMonitoradorComEnderecos(item);
        }
        return monitorador;
    }

    public Monitorador getMonitoradorById(Long id) {
        return monitoradorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Monitorador não encontrado com o ID: " + id));
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


    public List<Monitorador> buscarPF() {
        return monitoradorRepository.findByTipoPessoa(TipoPessoa.PF);
    }

    public List<Monitorador> buscarPJ() {
        return monitoradorRepository.findByTipoPessoa(TipoPessoa.PJ);
    }

    public List<Monitorador> filtrar(Monitorador filtro) {
        if (todosOsCamposVazios(filtro) && filtro.getTipoPessoa().equals(TipoPessoa.PJ)) {
            return monitoradorRepository.filtrar(filtro);
        } else if (todosOsCamposVazios(filtro) && filtro.getTipoPessoa().equals(TipoPessoa.PF)) {
            return monitoradorRepository.filtrar(filtro);
        } else {
            return monitoradorRepository.filtrar(filtro);
        }

    }

    public void deleteAllMonitoradores(List<Monitorador> list) {
        monitoradorRepository.deleteAll(list);
    }

    @Transactional
    public Monitorador updateMonitorador(Long id, Monitorador monitorador) {
        Monitorador oldMonitorador = findById(id);
        oldMonitorador.setCpfCnpj(monitorador.getCpfCnpj());
        oldMonitorador.setNomeRazaoSocial(monitorador.getNomeRazaoSocial());
        oldMonitorador.setRgIe(monitorador.getRgIe());
        oldMonitorador.setEmail(monitorador.getEmail());
        oldMonitorador.setTelefone(monitorador.getTelefone());
        oldMonitorador.setTipoPessoa(monitorador.getTipoPessoa());
        oldMonitorador.setDataNascimento(monitorador.getDataNascimento());
        oldMonitorador.setStatus(monitorador.getStatus());
        return monitoradorRepository.save(oldMonitorador);
    }

    public Monitorador findById(Long id) {
        Optional<Monitorador> obj = monitoradorRepository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(id, Monitorador.class.getName()));
    }
    public void validarMonitorador(Monitorador monitorador) {
        if (monitorador.getTipoPessoa() == TipoPessoa.PF) {
            validarCampoObrigatorio(monitorador.getNomeRazaoSocial(), "Nome");
            validarCampoObrigatorio(monitorador.getCpfCnpj(), "CPF");
            validarCPF(monitorador.getCpfCnpj());
            validarCampoObrigatorio(monitorador.getRgIe(), "RG");
            validarCampoObrigatorio(monitorador.getTelefone(), "Telefone");
            validarCampoObrigatorio(monitorador.getDataNascimento(), "Data de Nascimento");
            validarIdade(monitorador.getDataNascimento());
            validarCampoObrigatorio(monitorador.getStatus(), "Status");
        } else if (monitorador.getTipoPessoa() == TipoPessoa.PJ) {
            validarCampoObrigatorio(monitorador.getNomeRazaoSocial(), "Razão Social");
            validarCampoObrigatorio(monitorador.getCpfCnpj(), "CNPJ");
            validarCampoObrigatorio(monitorador.getRgIe(), "Inscrição Estadual");
            validarCampoObrigatorio(monitorador.getStatus(), "Status");
        }
        validarCampoObrigatorio(monitorador.getTelefone(), "Telefone");
        validarCampoObrigatorio(monitorador.getEmail(), "Email");
        validarEmail(monitorador.getEmail());
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
    public void validarEmail(String email) throws IllegalArgumentException {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
    }
    public void validarDuplicadosLista(List<Monitorador> monitoradorList) throws RegistroDuplicadoException {
        // Verifique se os es monitorador já existem pelo ID ou outro campo único, por exemplo, CPF ou CNPJ
        for(Monitorador item: monitoradorList) {
            if (item.getTipoPessoa().equals(TipoPessoa.PF)) {
                if (item.getCpfCnpj() != null && monitoradorRepository.existsByCpfCnpj(item.getCpfCnpj())) {
                    throw new RegistroDuplicadoException("O monitorador  CPF " + item.getCpfCnpj() + " já existe.");
                }
            }
            if (item.getTipoPessoa().equals(TipoPessoa.PJ)) {
                if (item.getCpfCnpj() != null && monitoradorRepository.existsByCpfCnpj(item.getCpfCnpj())) {
                    throw new RegistroDuplicadoException("O monitorador  CNPJ " + item.getCpfCnpj() + " já existe.");
                }
            }
        }
    }

    public static void validarIdade(Date dataNascimento) throws DataNascimentoException {
        // Converte Date para LocalDate
        LocalDate dataNascLocalDate = dataNascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Obtem a data atual
        LocalDate dataAtual = LocalDate.now();

        // Verifica se a data de nascimento é maior que a data atual
        if (dataNascLocalDate.isAfter(dataAtual)) {
            throw new DataNascimentoException("A data de nascimento não pode ser no futuro!");
        }

        // Calcula a diferença em anos
        int idade = Period.between(dataNascLocalDate, dataAtual).getYears();

        // Verifica se a idade é menor que 18
        if (idade < 18) {
            throw new DataNascimentoException("É preciso ter mais de 18 anos para se cadastrar !");
        }
    }



    private boolean todosOsCamposVazios(Monitorador filtro) {
        if (filtro == null) {
            return true;
        }

        return (isNullOrEmpty(filtro.getNomeRazaoSocial()) &&
                isNullOrEmpty(filtro.getCpfCnpj()) &&
                isNullOrEmpty(filtro.getTelefone()) &&
                isNullOrEmpty(filtro.getEmail()) &&
                isNullOrEmpty(filtro.getRgIe()) &&
                Objects.isNull(filtro.getDataNascimento()) &&
                Objects.isNull(filtro.getTipoPessoa())  &&
                Objects.isNull(filtro.getStatus()));
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


    public byte[] exportMonitoradoresToExcel() throws IOException {
        return excelService.exportMonitoradoresToExcel(monitoradorRepository.findAll());
    }


    public List<Monitorador> getAllMonitoradores() {
        return monitoradorRepository.findAll();
    }

    public void deleteMonitorador(Long id) {
        monitoradorRepository.deleteById(id);
    }

    public byte[] exportReport() throws JRException {
        return reportService.exportReport(getAllMonitoradores());
    }

    public List<Monitorador> gerarLista(MultipartFile fis) throws IOException {
        if(fis.getSize()  > 20971520 ) {
            throw new FileSizeException(fis.getName());
        }
        return excelService.gerarLista(fis);
    }
}

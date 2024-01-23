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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class MonitoradorService {

    @Autowired
    private final MonitoradorRepository monitoradorRepository;

    @Autowired
    private final EnderecoRepository enderecoRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    private static final Pattern CPF_PATTERN =
            Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}");

    @Autowired
    public MonitoradorService(MonitoradorRepository monitoradorRepository, EnderecoRepository enderecoRepository,PdfGeneratorService pdfGeneratorService) {
        this.monitoradorRepository = monitoradorRepository;
        this.enderecoRepository = enderecoRepository;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public Monitorador saveMonitorador(@Valid Monitorador monitorador) {


        return monitoradorRepository.save(removerMascaras(monitorador));
    }

    public List<Monitorador> saveAllMonitorador(@Valid List<Monitorador> monitorador) {
        return monitoradorRepository.saveAll(monitorador);
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

    public byte[] exportMonitoradoresToExcel() throws IOException {
        List<Monitorador> monitoradores= getAllMonitoradores();

        String[] headers = {"ID", "Tipo Pessoa", "CPF/CNPJ", "Nome/Razão Social", "Telefone", "Email", "RG/I.E","Data de Nascimento", "Status"};

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Monitoradores");

        // Criar estilos de célula
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyleWithBorder = createCellStyleWithBorder(workbook);

        // Adicionar linha de título
        addTitleRow(sheet, headerStyle, headers);

        // Cabeçalhos
        addHeaderRow(sheet, headers, headerStyle);

        // Preenchendo dados
        fillDataRows(sheet, monitoradores, cellStyleWithBorder, headers);

        // Auto dimensionar colunas
        autoSizeColumns(sheet, headers.length);

        // Escrever para um byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }


    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        return headerStyle;
    }

    private CellStyle createCellStyleWithBorder(Workbook workbook) {
        CellStyle cellStyleWithBorder = workbook.createCellStyle();
        cellStyleWithBorder.setBorderBottom(BorderStyle.THIN);
        cellStyleWithBorder.setBorderTop(BorderStyle.THIN);
        cellStyleWithBorder.setBorderLeft(BorderStyle.THIN);
        cellStyleWithBorder.setBorderRight(BorderStyle.THIN);
        return cellStyleWithBorder;
    }

    private void addTitleRow(Sheet sheet, CellStyle headerStyle,String[] headers) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO MONITORADORES");
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
    }

    private void addHeaderRow(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBottomBorderColor(IndexedColors.WHITE.getIndex());
            headerStyle.setTopBorderColor(IndexedColors.WHITE.getIndex());
            headerStyle.setLeftBorderColor(IndexedColors.WHITE.getIndex());
            headerStyle.setRightBorderColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFillForegroundColor(IndexedColors.BLACK1.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Sheet sheet, List<Monitorador> monitoradores, CellStyle cellStyleWithBorder, String[] headers) {
        int rowIdx = 2;
        for (Monitorador monitorador : monitoradores) {
            Row row = sheet.createRow(rowIdx++);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                switch (i) {
                    case 0:
                        cell.setCellValue(monitorador.getId());
                        break;
                    case 1:
                        cell.setCellValue(monitorador.getTipoPessoa().getTipoPessoa());
                        break;
                    case 2:
                        String cpfOuCnpj = monitorador.getTipoPessoa() == TipoPessoa.PF ? monitorador.getCpf() : monitorador.getCnpj();
                        cell.setCellValue(cpfOuCnpj);
                        break;
                    case 3:
                        String nomeOuRazaoSocial = monitorador.getTipoPessoa() == TipoPessoa.PF ? monitorador.getNome() : monitorador.getRazaoSocial();
                        cell.setCellValue(nomeOuRazaoSocial);
                        break;
                    case 4:
                        cell.setCellValue(monitorador.getTelefone());
                        break;
                    case 5:
                        cell.setCellValue(monitorador.getEmail());
                        break;
                    case 6:
                        String rgOuInscricaoEstadual = monitorador.getTipoPessoa() == TipoPessoa.PF ? monitorador.getRg() : monitorador.getInscricaoEstadual();
                        cell.setCellValue(rgOuInscricaoEstadual);
                        break;
                    case 7:
                        if (monitorador.getDataNascimento() != null) {
                            cell.setCellValue(monitorador.getDataNascimento());
                            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                            CreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
                            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
                            cell.setCellStyle(cellStyle);
                        } else {
                            cell.setCellValue("");
                        }
                        break;
                    case 8:
                        cell.setCellValue(monitorador.getStatus().getStatus());
                        break;
                }
                cell.setCellStyle(cellStyleWithBorder);
            }
        }
    }

    private void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private boolean todosOsCamposVazios(Monitorador filtro) {
        if (filtro == null) {
            return true;
        }

        return (isNullOrEmpty(filtro.getNome()) &&
                isNullOrEmpty(filtro.getCpf()) &&
                isNullOrEmpty(filtro.getCnpj()) &&
                isNullOrEmpty(filtro.getTelefone()) &&
                isNullOrEmpty(filtro.getEmail()) &&
                isNullOrEmpty(filtro.getRg()) &&
                isNullOrEmpty(filtro.getInscricaoEstadual()) &&
                filtro.getDataNascimento() == null &&
                filtro.getTipoPessoa() == null &&
                filtro.getStatus() == null);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public byte[] generateMonitoradorPDF() throws Exception {
        List<Monitorador> monitoradores = getAllMonitoradores();
        return pdfGeneratorService.exportMonitoradorPDF(monitoradores);
    }
}


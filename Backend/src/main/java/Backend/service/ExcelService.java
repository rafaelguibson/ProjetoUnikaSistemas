package Backend.service;

import Backend.entitie.Monitorador;
import Backend.enums.TipoPessoa;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelService {
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    public byte[] exportMonitoradoresToExcel(List<Monitorador> monitoradores) throws IOException {
        String[] headers = {"ID", "Tipo Pessoa", "CPF/CNPJ", "Nome/Razão Social", "Telefone", "Email", "RG/I.E", "Data de Nascimento", "Status"};

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Monitoradores");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyleWithBorder = createCellStyleWithBorder(workbook);
        CellStyle centeredCellStyle = createCenteredCellStyle(workbook);

        addTitleRow(sheet, headerStyle, headers);
        addHeaderRow(sheet, headers, headerStyle);
        fillDataRows(sheet, monitoradores, cellStyleWithBorder, centeredCellStyle, headers);
        autoSizeColumns(sheet, headers.length);

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

    private CellStyle createCenteredCellStyle(Workbook workbook) {
        CellStyle centeredCellStyle = workbook.createCellStyle();
        centeredCellStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return centeredCellStyle;
    }

    private void addTitleRow(Sheet sheet, CellStyle headerStyle, String[] headers) {
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

    private void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void fillDataRows(Sheet sheet, List<Monitorador> monitoradores, CellStyle cellStyleWithBorder, CellStyle centeredCellStyle, String[] headers) {
        int rowIdx = 2;
        for (int i = 0; i < monitoradores.size(); i++) {
            Row row = sheet.createRow(i + 2);
            Monitorador monitoradorDTO = monitoradores.get(i);

            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(getCellValue(monitoradorDTO, j));
                cell.setCellStyle(centeredCellStyle);
            }
        }
    }

    private String getCellValue(Monitorador monitoradorDTO, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return monitoradorDTO.getTipoPessoa().getDescricao();
            case 1:
                return monitoradorDTO.getCpfCnpj();
            case 2:
                return monitoradorDTO.getNomeRazaoSocial();
            case 3:
                return monitoradorDTO.getTelefone();
            case 4:
                return monitoradorDTO.getEmail();
            case 5:
                return monitoradorDTO.getRgIe();
            case 6:
                return Objects.isNull(monitoradorDTO.getDataNascimento()) ? "" : formato.format(monitoradorDTO.getDataNascimento());
            case 7:
                return monitoradorDTO.getStatus() ? "Ativo" : "Inativo";
            default:
                return "";
        }
    }

    public List<Monitorador> gerarLista(FileInputStream file) throws IOException {
        List<Monitorador> monitoradores = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0); // Supondo que a planilha desejada está na primeira aba

            Iterator<Row> rowIterator = sheet.iterator();

            // Ignora as duas primeiras linhas
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Primeira linha
            }
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Segunda linha
            }

            // Percorre todas as linhas da planilha
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Monitorador monitorador = criarMonitoradorDaLinha(row);
                if (monitorador != null) {
                    monitoradores.add(monitorador);
                }
            }
        }

        return monitoradores;
    }

    private Monitorador criarMonitoradorDaLinha(Row row) {
        try {
            Cell cellTipoPessoa = row.getCell(1); // Coluna do Tipo Pessoa

            String tipoPessoa = cellTipoPessoa.getStringCellValue().toLowerCase(); // Converter para minúsculas para comparar

            Monitorador monitorador = new Monitorador();

            // Verificar o tipo de pessoa e preencher os campos apropriados
            if ("física".equalsIgnoreCase(tipoPessoa)) {
                monitorador.setTipoPessoa(TipoPessoa.PF);
                monitorador.setNomeRazaoSocial(row.getCell(3).getStringCellValue()); // Nome
                monitorador.setCpfCnpj(row.getCell(2).getStringCellValue()); // CPF
                monitorador.setRgIe(row.getCell(6).getStringCellValue()); // RG
                monitorador.setTelefone(row.getCell(4).getStringCellValue()); // Telefone
                monitorador.setEmail(row.getCell(5).getStringCellValue()); // Email
                monitorador.setDataNascimento(new Date()); // Data de Nascimento
                monitorador.setStatus("Ativo".equals(row.getCell(8).getStringCellValue().toUpperCase())); // Status
            } else if ("jurídica".equalsIgnoreCase(tipoPessoa)) {
                monitorador.setTipoPessoa(TipoPessoa.PJ);
                monitorador.setNomeRazaoSocial(row.getCell(3).getStringCellValue()); //Razão Social
                monitorador.setCpfCnpj(row.getCell(2).getStringCellValue()); //CNPJ
                monitorador.setTelefone(row.getCell(4).getStringCellValue()); // Telefone
                monitorador.setEmail(row.getCell(5).getStringCellValue()); // Email
                monitorador.setRgIe(row.getCell(6).getStringCellValue()); // Inscrição Estadual
                monitorador.setStatus("Ativo".equals(row.getCell(8).getStringCellValue().toUpperCase())); // Status
            } else {
                // Trate casos em que o tipo de pessoa não é reconhecido
                return null;
            }

            return monitorador;
        } catch (Exception e) {
            // Se houver algum erro ao ler a linha, retorna null
            e.printStackTrace();
            return null;
        }
    }
}

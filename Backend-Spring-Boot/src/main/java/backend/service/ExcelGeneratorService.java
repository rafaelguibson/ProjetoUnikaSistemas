package backend.service;

import backend.entitie.Monitorador;
import backend.enums.TipoPessoa;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelGeneratorService {
    public byte[] exportMonitoradoresToExcel(List<Monitorador> monitoradores) throws IOException {


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

}

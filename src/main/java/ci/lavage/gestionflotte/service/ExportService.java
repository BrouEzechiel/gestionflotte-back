package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.response.EtatProprietaireResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    // ==========================================
    // 1. GÉNÉRATION EXCEL
    // ==========================================
    public byte[] genererEtatsExcel(List<EtatProprietaireResponse> etats) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("États Financiers");

            // Style pour l'en-tête (Gras + Fond gris)
            CellStyle headerStyle = workbook.createCellStyle();

            // CORRECTION ICI : On précise qu'on veut le Font de POI (Excel)
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Création de l'en-tête (Le Row de POI est importé proprement en haut)
            Row headerRow = sheet.createRow(0);
            String[] colonnes = {"Nom", "Prénoms", "Total Versements (CFA)", "Total Dépenses (CFA)", "Gain Net (CFA)"};
            for (int i = 0; i < colonnes.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colonnes[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplissage des données
            int rowIdx = 1;
            for (EtatProprietaireResponse etat : etats) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(etat.nom());
                row.createCell(1).setCellValue(etat.prenoms());
                row.createCell(2).setCellValue(etat.totalVersements().doubleValue());
                row.createCell(3).setCellValue(etat.totalDepenses().doubleValue());
                row.createCell(4).setCellValue(etat.getGainNet().doubleValue());
            }

            // Ajustement automatique de la taille des colonnes
            for (int i = 0; i < colonnes.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel", e);
        }
    }

    // ==========================================
    // 2. GÉNÉRATION PDF
    // ==========================================
    public byte[] genererEtatsPdf(List<EtatProprietaireResponse> etats) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate()); // Mode Paysage
            PdfWriter.getInstance(document, out);
            document.open();

            // CORRECTION ICI : On précise qu'on veut le Font de OpenPDF
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("États Financiers des Propriétaires", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Tableau
            PdfPTable table = new PdfPTable(5); // 5 colonnes
            table.setWidthPercentage(100);

            // En-têtes du tableau PDF
            String[] headers = {"Nom", "Prénoms", "Versements", "Dépenses", "Gain Net"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Données du tableau
            for (EtatProprietaireResponse etat : etats) {
                table.addCell(etat.nom());
                table.addCell(etat.prenoms());
                table.addCell(etat.totalVersements().toString() + " CFA");
                table.addCell(etat.totalDepenses().toString() + " CFA");

                // Colorer le gain en rouge si négatif, sinon normal
                PdfPCell gainCell = new PdfPCell(new Phrase(etat.getGainNet().toString() + " CFA"));
                if (etat.getGainNet().signum() < 0) {
                    gainCell.getPhrase().getFont().setColor(java.awt.Color.RED);
                }
                table.addCell(gainCell);
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}
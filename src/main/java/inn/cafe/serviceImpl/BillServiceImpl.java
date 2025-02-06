package inn.cafe.serviceImpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import inn.cafe.JWT.JwtFilter;
import inn.cafe.POJO.Bill;
import inn.cafe.constants.CafeConstants;
import inn.cafe.dao.BillDao;
import inn.cafe.service.BillService;
import inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

    @Autowired
    private BillDao billDao;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport");

        try {
            String fileName;
            if (validateRequest(requestMap)) {
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

                String data = "Name: " + requestMap.get("name") + "\n" +
                        "Contact Number: " + requestMap.get("contactNumber") + "\n" +
                        "Email: " + requestMap.get("email") + "\n" +
                        "Payment Method: " + requestMap.get("paymentMethod") + "\n";

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "/" + fileName + ".pdf"));

                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph("Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n"
                        + "Thank you for Visiting. Please visit again!", getFont("Data"));
                document.add(footer);

                document.close();

                return new ResponseEntity<>("{\"uuid\" : \"" + fileName + "\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Data Not Found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");

        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");

        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();

                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(BaseColor.CYAN);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);

                    table.addCell(header);
                });
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");

        Rectangle rectangle = new Rectangle(577, 825, 18, 15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);

        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);

    }

    private Font getFont(String header) {
        log.info("Inside getFont");

        switch (header) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;

            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;

            default:
                return new Font();
        }
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());

            billDao.save(bill);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateRequest(Map<String, Object> requestMap) {
        log.info("Inside validateRequest");

        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        log.info("Inside getBills");

        List<Bill> bills = new ArrayList<>();
        try {
            if (jwtFilter.isAdmin()) {
                bills = billDao.getBillByUserName(jwtFilter.getCurrentUser());
                return new ResponseEntity<>(bills, HttpStatus.OK);
            }
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<byte[]> getPDF(Map<String, Object> requestMap) {
        log.info("Inside getPDF {}", requestMap);

        try {
            byte[] pdf = new byte[0];
            if (requestMap.containsKey("uuid") && validateRequest(requestMap)) {
                String filePath = CafeConstants.STORE_LOCATION + "/" + requestMap.get("uuid") + ".pdf";

                if (CafeUtils.isFileExists(filePath)) {
                    pdf = getByteArray(filePath);
                    return new ResponseEntity<>(pdf, HttpStatus.OK);
                } else {
                    requestMap.put("isGenerate", false);
                    generateReport(requestMap);
                    pdf = getByteArray(filePath);
                    return new ResponseEntity<>(pdf, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(pdf, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);

        byte[] byteArray = IOUtils.toByteArray(targetStream);

        targetStream.close();
        return byteArray;
    }
}

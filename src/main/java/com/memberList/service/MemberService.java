package com.memberList.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.memberList.dao.Member;

import com.memberList.AppConfig.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberService {

	private static WebClient client = null;
	private String baseUrl;
	int rowNum = 1;
	Workbook workbook = new XSSFWorkbook();
	Sheet sheet = workbook.createSheet("Basis Data");
	
	@Autowired
	Configuration conf;
	
	protected static WebClient getFirefoxClient() {
		WebClient ob = new WebClient(BrowserVersion.FIREFOX_60);
		setWebClientOptions(ob.getOptions());
		setWebClientPreferences(ob);
		return ob;
	}

	private static void setWebClientPreferences(WebClient ob) {
		ob.waitForBackgroundJavaScript(10000);
		ob.setJavaScriptTimeout(15000);

		ob.setAjaxController(new NicelyResynchronizingAjaxController());
		ob.getCookieManager().setCookiesEnabled(true);
	}

	private static void setWebClientOptions(WebClientOptions opts) {
		opts.setDoNotTrackEnabled(true);
		opts.setThrowExceptionOnScriptError(false);
		opts.setThrowExceptionOnFailingStatusCode(false);
		opts.setTimeout(30000);
		opts.setUseInsecureSSL(true);
	}

	public void scrap() throws IOException {
		
		String url = "https://basis.org.bd/index.php/site/memberByLetter/A";
		baseUrl = url.substring(0, 51);
		System.out.println(baseUrl);
		for (char c = 'A'; c <= 'Z'; ++c)
			memberList(baseUrl + c);
	}

	public void memberList(String url) throws IOException {
		client = getFirefoxClient();
		HtmlPage page = client.getPage(url);
		List<HtmlElement> jobList = page.getByXPath("//div[@class='col-xs-12 col-md-3']/a");

		jobList.forEach(el -> {
			try {
				memberDetails(el.getAttribute("href"));
			} catch (IOException e) {
				log.warn("error is " + e);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void memberDetails(String url) throws IOException, IndexOutOfBoundsException {
		Member m = new Member();
		client = getFirefoxClient();
		HtmlPage page = client.getPage(url);
		try {
			log.info("Save member informantion url :" + url);
//			List<HtmlElement> jobList = page.getByXPath("//tbody");
//			for (int j = 0; j < jobList.size(); j++) {
			List<HtmlElement> jobList = ((DomElement) page.getByXPath("//tbody").get(5)).getElementsByTagName("tr");
			for (int i = 0; i < jobList.size(); i++) {
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("Company Name"))
					m.setCompanyName(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());

				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("BASIS Membership No."))
					m.setBasisMembershipNo(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("Address"))
					m.setAddress(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("Contact No"))
					m.setContactNo(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("E-mai"))
					m.setEmai(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("Company website"))
					m.setCompanyWebsite(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim()
						.contains("Organization’s head in Bangladesh"))
					m.setOrganizationsHeadInBangladesh(
							jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("Designation"))
					m.setDesignation(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
				if (jobList.get(i).getElementsByTagName("td").get(0).asText().trim().contains("Mobile"))
					m.setMobile(jobList.get(i).getElementsByTagName("td").get(1).asText().trim());
			}

		} catch (Exception e) {
		}
		
//		System.out.println(m.getCompanyName());
//		System.out.println(m.getBasisMembershipNo());
//		System.out.println(m.getYearOfEstablishment());
		//System.out.println(m.getAddress());
//		System.out.println(m.getCity());
//		System.out.println(m.getPostcode());
//		System.out.println(m.getContactNo());
//		System.out.println(m.getEmai());
//		System.out.println(m.getCompanyWebsite());
//		System.out.println(m.getOtherWebsitesThatBelongsToTheCompany());
//		System.out.println(m.getOrganizationsHeadInBangladesh());
//		System.out.println(m.getDesignation());
//		System.out.println(m.getMobile());
//		System.out.println(m.getLegalStructureOfTheCompany());
		
		conf.getSessionFactory().save(m);
		conf.getTransaction().commit();
		conf.closeAll();
		
		excelWriter(m, rowNum++);
		log.info("Save member informantion no :" + rowNum);

	}

	public void excelWriter(Member m, int rowNum) throws IOException {
		String[] columns = { "Company Name", "Basis Membership No", "Address", "Contact No", "Email", "company Website",
				"organizations Head In Bangladesh", "Designation", "Mobile/Direct Phone" };

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with contacts data
		Row row = sheet.createRow(rowNum);
		row.createCell(0).setCellValue(m.getCompanyName());
		row.createCell(1).setCellValue(m.getBasisMembershipNo());
		row.createCell(2).setCellValue(m.getAddress());
		row.createCell(3).setCellValue(m.getContactNo());
		row.createCell(4).setCellValue(m.getEmai());
		row.createCell(5).setCellValue(m.getCompanyWebsite());
		row.createCell(6).setCellValue(m.getOrganizationsHeadInBangladesh());
		row.createCell(7).setCellValue(m.getDesignation());
		row.createCell(8).setCellValue(m.getMobile());

		// Resize all columns to fit the content size
		// insert record to excelsheet
		for (int i = 0; i < columns.length; i++) {
			// Row row = sheet.createRow(rowNum++);

			sheet.autoSizeColumn(i);

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(
					"C:\\Users\\iftekar.alam\\hotfolder\\" + "Basis_v12.xlsx");
			workbook.write(fileOut);
			fileOut.close();
		}
	}
}

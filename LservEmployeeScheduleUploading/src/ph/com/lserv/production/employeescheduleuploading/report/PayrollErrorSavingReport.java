package ph.com.lserv.production.employeescheduleuploading.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ph.com.lbpsc.production.accountsreceivablereport.report.GenerateArReport;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;

public class PayrollErrorSavingReport extends GenerateArReport<EmployeeScheduleUploadingMain> {
	List<String> headerList = new ArrayList<>();

	public PayrollErrorSavingReport(EmployeeScheduleUploadingMain mainApplication) {
		super(mainApplication);
		// TODO Auto-generated constructor stub
	}

	public void generateWorkbook(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.createSheet("Employees with error saving payroll.");
		sheet.setColumnWidth(0, 30);

		// DataFormat dataFormat = workbook.createDataFormat();
		this.initializeStyle(workbook, 10, "Courier New");

		headerCenterStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		headerCenterStyle.setBorderBottom((short) 1);
		headerCenterStyle.setBorderTop((short) 1);
		headerCenterStyle.setBorderLeft((short) 1);
		headerCenterStyle.setBorderRight((short) 1);

		defaultStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		defaultStyle.setBorderBottom((short) 1);
		defaultStyle.setBorderTop((short) 1);
		defaultStyle.setBorderLeft((short) 1);
		defaultStyle.setBorderRight((short) 1);

		int rowCount = 0;
		int columnCount = 0;

		this.createCell(sheet.createRow(rowCount), columnCount++, "Employees with error saving payroll",
				headerCenterStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		rowCount++;
		columnCount = 0;

		this.headerList.add("Employee ID");
		this.headerList.add("Full Name");
		this.headerList.add("Error message");

		this.createHeader(headerList, columnCount, sheet.createRow(rowCount), sheet, rowCount);
		rowCount++;
		columnCount = 0;

		for (Entry<EmploymentHistory, String> entry : this.mainApplication.getEmployeeErrorSavingPayrollHashMap()
				.entrySet()) {
			Integer employeeCode = entry.getKey().getEmployee().getEmployeeCode();
			String employeeFullName = entry.getKey().getEmployee().getEmployeeFullName();
			String errorMessage = entry.getValue();

			createDetails(employeeCode, employeeFullName, errorMessage, columnCount, sheet.createRow(rowCount++));
		}

		for (int i = 0; i < headerList.size(); i++) {
			sheet.autoSizeColumn(i, true);
		}

	}

	public void createHeader(List<String> headerList, int columnCount, Row rowDetails, XSSFSheet sheet, int rowCount) {
		for (String header : headerList) {
			this.createCell(rowDetails, columnCount++, header, headerCenterStyle);
		}
	}

	public void createDetails(Integer employeeCode, String employeeFullName, String errorMessage, int columnCount,
			Row rowDetails) {

		this.createCell(rowDetails, columnCount++, employeeCode, defaultStyle);
		this.createCell(rowDetails, columnCount++, employeeFullName, defaultStyle);
		this.createCell(rowDetails, columnCount++, errorMessage, defaultStyle);
	}

	@Override
	public void generateExcel(XSSFWorkbook workbook) throws Exception {
		// TODO Auto-generated method stub
		ProcessingMessage.showProcessingMessage(() -> this.generateWorkbook(workbook),
				this.mainApplication.getPrimaryStage(), "Generating excel file.");
	}

}

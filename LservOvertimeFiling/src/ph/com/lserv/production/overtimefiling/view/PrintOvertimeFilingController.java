package ph.com.lserv.production.overtimefiling.view;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ph.com.lbpsc.production.ReportMain;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.masterclass.MasterController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.ReportUtil;
import ph.com.lserv.production.overtimefiling.OvertimeFilingMain;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.timeconfigfoxpro.model.TimeConfigFoxPro;

public class PrintOvertimeFilingController extends MasterController<OvertimeFilingMain> {
	Stage stage;
	OvertimeFiling overtimeFiling;
	TimeConfigFoxPro timeConfigFoxPro;

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		// TODO Auto-generated method stub
	}

	public boolean isValidDateRange(LocalDate dateFrom, LocalDate dateTo) {
		if (dateFrom == null || dateTo == null) {
			return false;
		} else if (dateFrom.compareTo(dateTo) > 0) {
			return false;
		}
		return true;
	}

	@FXML
	public void handleReport() {

		if (!isValidDateRange(this.cutoffFromDateTimePicker.getValue(), this.cutoffToDateTimePicker.getValue())) {
			AlertUtil.showErrorAlert("Invalid cutoff date range.", this.getStage());
			this.setFieldInvalidStyle(true);
			return;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		String cutoffRange = dateFormat.format(DateFormatter.toDate(this.cutoffFromDateTimePicker.getValue())) + " - "
				+ dateFormat.format(DateFormatter.toDate(this.cutoffToDateTimePicker.getValue()));

		List<OvertimeFiling> overtimeFilingList = this.mainApplication.getOvertimeByCutoffDate(
				this.cutoffFromDateTimePicker.getValue().toString(), this.cutoffToDateTimePicker.getValue().toString());
		Map<String, Object> parameterReport = new HashMap<String, Object>();

		if (overtimeFilingList.size() != 0) {
			parameterReport.put(JRParameter.REPORT_CONNECTION, new ReportMain().getDatabaseConnection());
			parameterReport.put("cutoffRange", cutoffRange);

			InputStream report = ReportMain.class.getResourceAsStream("reports/OvertimeFilingReport.jasper");

			JRBeanCollectionDataSource jRBeanCollectionDataSource = new JRBeanCollectionDataSource(overtimeFilingList);

			ReportUtil.showReportBeanCollection(report, parameterReport, jRBeanCollectionDataSource);

			this.setFieldInvalidStyle(false);

		} else {
			AlertUtil.showDataNotFound(this.getStage());
		}

	}

	public void setFieldInvalidStyle(boolean set) {
		if (set) {
			this.cutoffFromDateTimePicker.setStyle("-fx-border-color: red");
			this.cutoffToDateTimePicker.setStyle("-fx-border-color: red");
		} else {
			this.cutoffFromDateTimePicker.setStyle("");
			this.cutoffToDateTimePicker.setStyle("");
		}
	}

	@FXML
	public void handleCancel() {
		this.getStage().close();
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private Button buttonPrint;
	@FXML
	private Button buttonCancel;
	@FXML
	private FormattedDatePicker cutoffFromDateTimePicker;
	@FXML
	private FormattedDatePicker cutoffToDateTimePicker;

}

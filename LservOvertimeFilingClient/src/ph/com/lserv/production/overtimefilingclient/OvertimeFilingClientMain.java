package ph.com.lserv.production.overtimefilingclient;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.overtimefiling.OvertimeFilingMain;
import ph.com.lserv.production.overtimefilingclient.data.OvertimeFilingClientDao;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;
import ph.com.lserv.production.overtimefilingclient.view.EditOvertimeFilingClientController;

public class OvertimeFilingClientMain extends MasterMain<OvertimeFilingClient> {
	private OvertimeFilingMain overtimeFilingMain;
	private EmployeeScheduleUploadingMain employeeScheduleUploadingMain;

	EmployeeScheduleUploading selectedBiometric = new EmployeeScheduleUploading();
	Timestamp timeInEntry;
	Timestamp timeOutEntry;

	Timestamp overtimeStart;
	Timestamp overtimeEnd;
	BigDecimal totalHrs;

	List<OvertimeFilingClient> overtimeFilingClientList;

	public OvertimeFilingClientMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(OvertimeFilingClient.class);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean createMainObject(OvertimeFilingClient overtimeFilingClient) {
		return new OvertimeFilingClientDao(sqlSessionFactory).createData(overtimeFilingClient) > 0;
	}

	@Override
	public boolean updateMainObject(OvertimeFilingClient overtimeFilingClient) {
		return new OvertimeFilingClientDao(sqlSessionFactory).updateData(overtimeFilingClient) > 0;
	}

	@Override
	public boolean deleteMainObject(OvertimeFilingClient overtimeFilingClient) {
		return new OvertimeFilingClientDao(sqlSessionFactory).deleteData(overtimeFilingClient) > 0;
	}

	public OvertimeFilingClient getDataById(int id) {
		return new OvertimeFilingClientDao(sqlSessionFactory).getDataById(id);
	}

	public List<OvertimeFilingClient> getDataByEmpIdAndOvertimeDateFrom(int employeeCode, Date overtimeDateStart) {
		return new OvertimeFilingClientDao(sqlSessionFactory).getDataByEmpIdAndOvertimeDateFrom(employeeCode,
				overtimeDateStart);
	}

	public List<OvertimeFilingClient> getDataByEmpIdAndOvertimeDateTo(int employeeCode, Date overtimeDateEnd) {
		return new OvertimeFilingClientDao(sqlSessionFactory).getDataByEmpIdAndOvertimeDateTo(employeeCode,
				overtimeDateEnd);
	}

	public List<OvertimeFilingClient> getAllData() {
		return new OvertimeFilingClientDao(sqlSessionFactory).getAllData();
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public OvertimeFilingClient createOvertimeFilingClient(Timestamp dateTimeFrom, Timestamp dateTimeTo,
			BigDecimal totalOtHours) {
		OvertimeFilingClient overtimeFilingClient = new OvertimeFilingClient();

		overtimeFilingClient.setEmploymentHistory(this.selectedBiometric.getEmploymentHistory());
		overtimeFilingClient.setClient(this.selectedBiometric.getClient());
		overtimeFilingClient.setDepartment(this.selectedBiometric.getDepartment());

		overtimeFilingClient.setDateTimeFrom(dateTimeFrom);
		overtimeFilingClient.setDateTimeTo(dateTimeTo);
		overtimeFilingClient.setTotalOtHours(totalOtHours);

		overtimeFilingClient.setUser(this.getUser());
		overtimeFilingClient.setChangedOnDate(new Date());
		overtimeFilingClient.setChangedInComputer(this.getComputerName());
		overtimeFilingClient.setChangedByUser(this.getUser() == null ? null : this.getUser().getUserName());

		return overtimeFilingClient;
	}

	public OvertimeFilingClient updateOvertimeFilingClient(Timestamp dateTimeFrom, Timestamp dateTimeTo,
			BigDecimal totalOtHours, OvertimeFilingClient overtimeFilingClient) {

		overtimeFilingClient.setDateTimeFrom(dateTimeFrom);
		overtimeFilingClient.setDateTimeTo(dateTimeTo);
		overtimeFilingClient.setTotalOtHours(totalOtHours);

		overtimeFilingClient.setUser(this.getUser());
		overtimeFilingClient.setChangedOnDate(new Date());
		overtimeFilingClient.setChangedInComputer(this.getComputerName());
		overtimeFilingClient.setChangedByUser(this.getUser() == null ? null : this.getUser().getUserName());

		return overtimeFilingClient;
	}

	@Override
	public void initializeObjects() {
		this.selectedBiometric = new EmployeeScheduleUploading();
		this.overtimeStart = new Timestamp(new Date().getTime());
		this.overtimeEnd = new Timestamp(new Date().getTime());
		this.timeInEntry = new Timestamp(new Date().getTime());
		this.timeOutEntry = new Timestamp(new Date().getTime());
		this.totalHrs = BigDecimal.ZERO;
		this.overtimeFilingClientList = new ArrayList<>();
		try {
			this.overtimeFilingMain = new OvertimeFilingMain();
			this.employeeScheduleUploadingMain = new EmployeeScheduleUploadingMain();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public OvertimeFilingClient showEditOvertimeFilingClient(OvertimeFilingClient overtimeFilingClient,
			ModificationType modificationType, EmployeeScheduleUploading employeeScheduleUploading, Stage stage,
			Timestamp timeInEntry, Timestamp timeOutEntry,
			List<OvertimeFilingClient> overtimeFilingClientListFromEdit) {
		this.initializeObjects();
		this.selectedBiometric = employeeScheduleUploading;
		this.timeInEntry = timeInEntry;
		this.timeOutEntry = timeOutEntry;
		this.overtimeFilingClientList.addAll(overtimeFilingClientListFromEdit);

		try {
			FxmlUtil<EditOvertimeFilingClientController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditOvertimeFilingClient.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditOvertimeFilingClientController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			if (controller.showEditDialogStage(modificationType + " - Overtime Filing Client", stage, editLayout,
					overtimeFilingClient)) {
				if (modificationType.equals(ModificationType.ADD)) {
					return this.createOvertimeFilingClient(this.overtimeStart, this.overtimeEnd, this.totalHrs);
				} else {
					return this.updateOvertimeFilingClient(this.overtimeStart, this.overtimeEnd, this.totalHrs,
							overtimeFilingClient);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Timestamp stringToTimestampConverter(DatePicker datePicker, ValidatedTextField textField) {
		String StringInput = datePicker.getValue().toString().concat(" ".concat(textField.getText()));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTimeInput = LocalDateTime.from(formatter.parse(StringInput));
		return Timestamp.valueOf(localDateTimeInput);
	}

	public void setTextFieldTimeFormat(ValidatedTextField textField) {
		textField.setFormat("##:##");
		textField.setPromptText("HH:mm");
	}

	public Integer stringToIntTimeConverter(ValidatedTextField textField) {
		String getTimeString = textField.getText();
		String[] splitTimeString = getTimeString.split(":");
		return Integer.valueOf(splitTimeString[0] + splitTimeString[1]);
	}

	public void timeConfig(ValidatedTextField textField) {
		String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
		String input = textField.getText();

		if (!input.isEmpty() && input.length() > 3) {
			Integer result = this.stringToIntTimeConverter(textField);
			Integer hour = result / 100;
			Integer min = result % 100;
			String hourString = String.valueOf(hour);
			String minString = String.valueOf(min);

			if (hour <= 23 && min <= 59) { // 24 hour validation
				String fix = "0".concat(hourString).concat(":".concat(minString));

				if (hour <= 9 && min <= 9) { // 3 input auto format
					String fix2 = "0".concat(hourString).concat(":0".concat(minString));
					textField.replaceSelection("");
					textField.setText(fix2);
					return;

				}
				if (!textField.getSelectedText().matches(regex)) { // 24 hour auto format
					textField.replaceSelection("");
					textField.setText(fix);
					return;
				}

			}
		}
		if (!textField.getSelectedText().matches(regex)) {
			textField.clear();
		}
	}

	public EmployeeScheduleUploading getSelectedBiometric() {
		return selectedBiometric;
	}

	public void setSelectedBiometric(EmployeeScheduleUploading selectedBiometric) {
		this.selectedBiometric = selectedBiometric;
	}

	public OvertimeFilingMain getOvertimeFilingMain() {
		return overtimeFilingMain;
	}

	public void setOvertimeFilingMain(OvertimeFilingMain overtimeFilingMain) {
		this.overtimeFilingMain = overtimeFilingMain;
	}

	public EmployeeScheduleUploadingMain getEmployeeScheduleUploadingMain() {
		return employeeScheduleUploadingMain;
	}

	public void setEmployeeScheduleUploadingMain(EmployeeScheduleUploadingMain employeeScheduleUploadingMain) {
		this.employeeScheduleUploadingMain = employeeScheduleUploadingMain;
	}

	public Timestamp getOvertimeStart() {
		return overtimeStart;
	}

	public void setOvertimeStart(Timestamp overtimeStart) {
		this.overtimeStart = overtimeStart;
	}

	public Timestamp getOvertimeEnd() {
		return overtimeEnd;
	}

	public void setOvertimeEnd(Timestamp overtimeEnd) {
		this.overtimeEnd = overtimeEnd;
	}

	public BigDecimal getTotalHrs() {
		return totalHrs;
	}

	public void setTotalHrs(BigDecimal totalHrs) {
		this.totalHrs = totalHrs;
	}

	public Timestamp getTimeInEntry() {
		return timeInEntry;
	}

	public void setTimeInEntry(Timestamp timeInEntry) {
		this.timeInEntry = timeInEntry;
	}

	public Timestamp getTimeOutEntry() {
		return timeOutEntry;
	}

	public void setTimeOutEntry(Timestamp timeOutEntry) {
		this.timeOutEntry = timeOutEntry;
	}

	public List<OvertimeFilingClient> getOvertimeFilingClientList() {
		return overtimeFilingClientList;
	}

	public void setOvertimeFilingClientList(List<OvertimeFilingClient> overtimeFilingClientList) {
		this.overtimeFilingClientList = overtimeFilingClientList;
	}

}

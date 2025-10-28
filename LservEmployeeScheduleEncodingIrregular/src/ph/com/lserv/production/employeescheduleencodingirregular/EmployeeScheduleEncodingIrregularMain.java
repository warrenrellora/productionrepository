package ph.com.lserv.production.employeescheduleencodingirregular;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.EmploymentHistoryMain;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lserv.production.employeescheduleencoding.EmployeeScheduleEncodingMain;
import ph.com.lserv.production.employeescheduleencodingirregular.data.EmployeeScheduleEncodingIrregularDao;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleencodingirregular.view.EditEmployeeScheduleEncodingIrregularController;
import ph.com.lserv.production.scheduleencoding.ScheduleEncodingMain;

public class EmployeeScheduleEncodingIrregularMain extends MasterMain<EmployeeScheduleEncodingIrregular> {
	ScheduleEncodingMain scheduleEncodingMain;
	ObservableList<Date> allDateOfTheMonthObservableList;
	ObservableList<String> allMonthsNameObservableList;
	ObservableList<String> allDateOfTheMonthFormattedObservableList;
	ObservableList<Map.Entry<String, Boolean>> observableListDates;
	List<String> selectedDatesList;
	List<EmployeeScheduleEncodingIrregular> createdObjectsToBeSavedList;
	Stage dialogStage;
	EmployeeScheduleEncodingIrregular selectedEmployeeScheduleEncodingIrregular;
	ObservableList<Map.Entry<Employee, Boolean>> selectedEmployeesObservableList;

	EmployeeScheduleEncodingMain employeeScheduleEncodingMain;
	EmploymentHistoryMain employmentHistoryMain;

	List<EmployeeScheduleEncodingIrregular> modifyDetailsErrorAddList;

	public EmployeeScheduleEncodingIrregularMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(EmployeeScheduleEncodingIrregular.class);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public boolean createMainObject(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory)
				.createData(employeeScheduleEncodingIrregular) > 0;
	}

	public boolean createMultipleData(List<EmployeeScheduleEncodingIrregular> createdObjectsToBeSavedList) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory)
				.createMultipleData(createdObjectsToBeSavedList) > 0;
	}

	@Override
	public boolean updateMainObject(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory)
				.updateData(employeeScheduleEncodingIrregular) > 0;
	}

	@Override
	public boolean deleteMainObject(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory)
				.deleteData(employeeScheduleEncodingIrregular) > 0;
	}

	public List<EmployeeScheduleEncodingIrregular> getAllEmployeeIrregularScheduleByEmployeeID(Integer employeeID) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory)
				.getAllEmployeeIrregularScheduleByEmployeeID(employeeID);
	}

	public EmployeeScheduleEncodingIrregular getEmployeeIrregularScheduleByPrikey(Integer prikey) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory).getEmployeeIrregularScheduleByPrikey(prikey);
	}

	public EmployeeScheduleEncodingIrregular getEmployeeIrregularScheduleByDateAndEmployeeID(String dateSchedule,
			Integer employeeID) {
		return new EmployeeScheduleEncodingIrregularDao(sqlSessionFactory)
				.getEmployeeIrregularScheduleByDateAndEmployeeID(dateSchedule, employeeID);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return null;
	}

	@Override
	public void initializeObjects() {
		this.observableListDates = FXCollections.observableArrayList();
		this.allDateOfTheMonthObservableList = FXCollections.observableArrayList();
		this.allMonthsNameObservableList = FXCollections.observableArrayList();
		this.allDateOfTheMonthFormattedObservableList = FXCollections.observableArrayList();
		this.selectedEmployeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();
		this.selectedDatesList = new ArrayList<>();
		this.createdObjectsToBeSavedList = new ArrayList<>();
		this.selectedEmployeesObservableList = FXCollections.observableArrayList();
		this.modifyDetailsErrorAddList = new ArrayList<>();
		try {
			this.scheduleEncodingMain = new ScheduleEncodingMain();
			this.employmentHistoryMain = new EmploymentHistoryMain();
			this.employeeScheduleEncodingMain = new EmployeeScheduleEncodingMain();
			this.employeeScheduleEncodingMain.initializeObjects();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public EmployeeScheduleEncodingIrregular createObjectToModify(String dateSelected, String offsetAllowed,
			String totalMinPerDay, String timeIn, String timeOut, String lunchIn, String lunchOut, Employee employee,
			Boolean isBreakExempted) {

		EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();

		DateFormat formatter = new SimpleDateFormat("HH:mm");

		employeeScheduleEncodingIrregular
				.setIsBreakExempted(isBreakExempted == null ? null : isBreakExempted == true ? 1 : 0);

		try {
			employeeScheduleEncodingIrregular.setDateSchedule(new SimpleDateFormat("MM-dd-yyyy").parse(dateSelected));
		} catch (Exception e) {
			e.printStackTrace();
		}

		EmploymentHistory employmentHistory = this.getEmploymentHistoryMain()
				.getEmploymentHistoryMaxByEmployeeCode(employee.getEmployeeCode());

		employeeScheduleEncodingIrregular.setEmploymentHistory(employmentHistory);
		employeeScheduleEncodingIrregular.setOffsetAllowed(Integer.valueOf(offsetAllowed));
		employeeScheduleEncodingIrregular.setTotalMinPerDay(Integer.valueOf(totalMinPerDay));

		employeeScheduleEncodingIrregular
				.setChangedByUser(this.getUser() == null ? null : this.getUser().getUserName());
		employeeScheduleEncodingIrregular.setChangedOnDate(new Date());
		employeeScheduleEncodingIrregular.setChangedInComputer(this.getComputerName());
		employeeScheduleEncodingIrregular.setUser(this.getUser());

		try {
			employeeScheduleEncodingIrregular.setTimeIn(new Time(formatter.parse(timeIn).getTime()));
			employeeScheduleEncodingIrregular.setTimeOut(new Time(formatter.parse(timeOut).getTime()));

			if (!isBreakExempted) {
				employeeScheduleEncodingIrregular.setLunchIn(new Time(formatter.parse(lunchIn).getTime()));
				employeeScheduleEncodingIrregular.setLunchOut(new Time(formatter.parse(lunchOut).getTime()));
				return employeeScheduleEncodingIrregular;
			}

			employeeScheduleEncodingIrregular.setLunchIn(null);
			employeeScheduleEncodingIrregular.setLunchOut(null);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return employeeScheduleEncodingIrregular;
	}

	public boolean showEditEmployeeScheduleEncodingIrregular(
			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular, ModificationType modificationType,
			ObservableList<Map.Entry<Employee, Boolean>> selectedEmployeesObservableList) {
		this.initializeObjects();
		try {
			this.getSelectedEmployeesObservableList().setAll(selectedEmployeesObservableList);
			FxmlUtil<EditEmployeeScheduleEncodingIrregularController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditEmployeeScheduleEncodingIrregular.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditEmployeeScheduleEncodingIrregularController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " - Employee Irregular Schedule Encoding",
					this.primaryStage, editLayout, employeeScheduleEncodingIrregular);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void populateObservableListDates() {
		this.observableListDates.clear();

		if (this.allDateOfTheMonthObservableList != null && !this.allDateOfTheMonthObservableList.isEmpty()) {
			HashMap<String, Boolean> hashMap = new HashMap<>();
			this.allDateOfTheMonthFormattedObservableList.forEach(C -> hashMap.put(C, false));

			this.observableListDates.addAll(hashMap.entrySet());
			ObservableListUtil.sort(this.observableListDates, p -> p.getKey());
		}
	}

	public Integer[] formatStringToIntTime(List<ValidatedTextField> validatedTextFieldList) {
		Integer results[] = new Integer[4];
		results[0] = 0;
		results[1] = 0;
		results[2] = 0;
		results[3] = 0;

		Integer TimeIn = this.stringToIntTimeConverter(validatedTextFieldList.get(0));
		Integer TimeOut = this.stringToIntTimeConverter(validatedTextFieldList.get(1));
		Integer LunchIn = this.stringToIntTimeConverter(validatedTextFieldList.get(2));
		Integer LunchOut = this.stringToIntTimeConverter(validatedTextFieldList.get(3));

		results[0] = TimeIn;
		results[1] = TimeOut;
		results[2] = LunchIn;
		results[3] = LunchOut;

		return results;
	}

	public boolean checkInvalidRangeField(Integer[] results, List<ValidatedTextField> validatedTextFieldList) {
		if ((results[0] != null && results[1] != null)
				&& (results[0] > results[1] || results[0].compareTo(results[1]) == 0)) {
			validatedTextFieldList.get(0).requestFocus();
			return true;
		} else if ((results[2] != null && results[3] != null)
				&& (results[2] > results[3] || results[2].compareTo(results[3]) == 0)) {
			validatedTextFieldList.get(2).requestFocus();
			return true;
		}
		return false;
	}

	public void setTimeConfigTextField(ValidatedTextField textField) {
		this.timeConfig(textField);

		textField.setOnMouseMoved(event -> {
			this.timeConfig(textField);
		});

		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
				this.timeConfig(textField);
			}
		});
	}

	public Integer stringToIntTimeConverter(ValidatedTextField textField) {
		if (textField.getText().isEmpty()) {
			return null;
		}
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
		} // else
		if (!textField.getSelectedText().matches(regex)) {
			textField.clear();
		}
	}

	public void getSelectedDates() {

	}

	public ObservableList<Date> getAllDateOfTheMonthObservableList() {
		return allDateOfTheMonthObservableList;
	}

	public void setAllDateOfTheMonthObservableList(ObservableList<Date> allDateOfTheMonthObservableList) {
		this.allDateOfTheMonthObservableList = allDateOfTheMonthObservableList;
	}

	public ObservableList<String> getAllMonthsNameObservableList() {
		return allMonthsNameObservableList;
	}

	public void setAllMonthsNameObservableList(ObservableList<String> allMonthsNameObservableList) {
		this.allMonthsNameObservableList = allMonthsNameObservableList;
	}

	public ObservableList<String> getAllDateOfTheMonthFormattedObservableList() {
		return allDateOfTheMonthFormattedObservableList;
	}

	public void setAllDateOfTheMonthFormattedObservableList(
			ObservableList<String> allDateOfTheMonthFormattedObservableList) {
		this.allDateOfTheMonthFormattedObservableList = allDateOfTheMonthFormattedObservableList;
	}

	public ObservableList<Map.Entry<String, Boolean>> getObservableListDates() {
		return observableListDates;
	}

	public void setObservableListDates(ObservableList<Map.Entry<String, Boolean>> observableListDates) {
		this.observableListDates = observableListDates;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public EmploymentHistoryMain getEmploymentHistoryMain() {
		return employmentHistoryMain;
	}

	public void setEmploymentHistoryMain(EmploymentHistoryMain employmentHistoryMain) {
		this.employmentHistoryMain = employmentHistoryMain;
	}

	public EmployeeScheduleEncodingMain getEmployeeScheduleEncodingMain() {
		return employeeScheduleEncodingMain;
	}

	public void setEmployeeScheduleEncodingMain(EmployeeScheduleEncodingMain employeeScheduleEncodingMain) {
		this.employeeScheduleEncodingMain = employeeScheduleEncodingMain;
	}

	public List<String> getSelectedDatesList() {
		return selectedDatesList;
	}

	public void setSelectedDatesList(List<String> selectedDatesList) {
		this.selectedDatesList = selectedDatesList;
	}

	public List<EmployeeScheduleEncodingIrregular> getCreatedObjectsToBeSavedList() {
		return createdObjectsToBeSavedList;
	}

	public void setCreatedObjectsToBeSavedList(List<EmployeeScheduleEncodingIrregular> createdObjectsToBeSavedList) {
		this.createdObjectsToBeSavedList = createdObjectsToBeSavedList;
	}

	public EmployeeScheduleEncodingIrregular getSelectedEmployeeScheduleEncodingIrregular() {
		return selectedEmployeeScheduleEncodingIrregular;
	}

	public void setSelectedEmployeeScheduleEncodingIrregular(
			EmployeeScheduleEncodingIrregular selectedEmployeeScheduleEncodingIrregular) {
		this.selectedEmployeeScheduleEncodingIrregular = selectedEmployeeScheduleEncodingIrregular;
	}

	public ObservableList<Map.Entry<Employee, Boolean>> getSelectedEmployeesObservableList() {
		return selectedEmployeesObservableList;
	}

	public void setSelectedEmployeesObservableList(
			ObservableList<Map.Entry<Employee, Boolean>> selectedEmployeesObservableList) {
		this.selectedEmployeesObservableList = selectedEmployeesObservableList;
	}

	public ScheduleEncodingMain getScheduleEncodingMain() {
		return scheduleEncodingMain;
	}

	public void setScheduleEncodingMain(ScheduleEncodingMain scheduleEncodingMain) {
		this.scheduleEncodingMain = scheduleEncodingMain;
	}

	public List<EmployeeScheduleEncodingIrregular> getModifyDetailsErrorAddList() {
		return modifyDetailsErrorAddList;
	}

	public void setModifyDetailsErrorAddList(List<EmployeeScheduleEncodingIrregular> modifyDetailsErrorAddList) {
		this.modifyDetailsErrorAddList = modifyDetailsErrorAddList;
	}

}

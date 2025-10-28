package ph.com.lserv.production.scheduleencoding;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.client.ClientMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.EmployeeMain;
import ph.com.lbpsc.production.employmenthistory.EmploymentHistoryMain;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.overtimetype.OvertimeTypeMain;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.employeescheduleencoding.EmployeeScheduleEncodingMain;
import ph.com.lserv.production.employeescheduleencodingirregular.EmployeeScheduleEncodingIrregularMain;
import ph.com.lserv.production.employeescheduleencodingovertime.EmployeeScheduleEncodingOvertimeMain;
import ph.com.lserv.production.scheduleencoding.data.ScheduleEncodingDao;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;
import ph.com.lserv.production.scheduleencoding.view.BrowseScheduleEncodingController;
import ph.com.lserv.production.scheduleencoding.view.EditScheduleEncodingController;
import ph.com.lserv.production.scheduleencodingreference.ScheduleEncodingReferenceMain;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class ScheduleEncodingMain extends MasterMain<ScheduleEncoding> {
	ObservableList<ScheduleEncoding> observableListScheduleEncoding;
	List<ScheduleEncoding> scheduleEncodingList;
	List<String> objectToModifySelectedList;
	List<ScheduleEncoding> objectToModifySelectedObjectList;
	ScheduleEncoding scheduleEncodingFromEdit;
	List<ValidatedTextField> enabledValidatedTextFieldList;
	List<ScheduleEncoding> scheduleEncodingToDeleteList;

	public ObservableList<Client> observableListClient;
	public Client client;
	public ClientMain clientMain;
	String clientName = "";
	String schedName = "";

	List<ScheduleEncoding> scheduleEncodingToSave;

	EmployeeScheduleEncodingIrregularMain employeeScheduleEncodingIrregularMain;
	EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain;
	ScheduleEncodingReferenceMain scheduleEncodingReferenceMain;
	EmployeeScheduleEncodingMain employeeScheduleEncodingMain;
	OvertimeTypeMain overtimeTypeMain;
	EmployeeMain employeeMain;
	EmploymentHistoryMain employmentHistoryMain;

	Boolean isBreakExempted = false;

	public ScheduleEncodingMain() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(ScheduleEncoding.class);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public boolean createMainObject(ScheduleEncoding scheduleEncoding) {
		return new ScheduleEncodingDao(sqlSessionFactory).createData(scheduleEncoding) > 0;
	}

	public boolean createMultipleMainObject(List<ScheduleEncoding> scheduleEncodingToSave) {
		return new ScheduleEncodingDao(sqlSessionFactory).createMultipleData(scheduleEncodingToSave) > 0;
	}

	@Override
	public boolean updateMainObject(ScheduleEncoding scheduleEncoding) {
		return new ScheduleEncodingDao(sqlSessionFactory).updateData(scheduleEncoding) > 0;
	}

	public List<ScheduleEncoding> getAllScheduleByClientCodeDistinct(Client client) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleByClientCodeDistinct(client);
	}

	public List<ScheduleEncoding> getAllScheduleByClientCode(Client client) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleByClientCode(client);
	}

	public List<ScheduleEncoding> getAllEmployeeScheduleByEmpId(Integer employeeID) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllEmployeeScheduleByEmpId(employeeID);
	}

	public ScheduleEncoding getAllScheduleByPrikeyReferenceClientAndScheduleDay(Integer prikeyReferenceClient,
			String schedDay) {
		return new ScheduleEncodingDao(sqlSessionFactory)
				.getAllScheduleByPrikeyReferenceClientAndScheduleDay(prikeyReferenceClient, schedDay);
	}

	public boolean updateMultipleMainObject(List<ScheduleEncoding> scheduleEncodingList) {
		return new ScheduleEncodingDao(sqlSessionFactory).updateMultipleData(scheduleEncodingList) > 0;
	}

	public boolean deleteMultipleMainObject(List<ScheduleEncoding> scheduleEncodingList) {
		return new ScheduleEncodingDao(sqlSessionFactory).deleteMultipleData(scheduleEncodingList) > 0;
	}

	@Override
	public boolean deleteMainObject(ScheduleEncoding scheduleEncoding) {
		return new ScheduleEncodingDao(sqlSessionFactory).deleteData(scheduleEncoding) > 0;
	}

	public boolean deleteDataByClientNameAndSchedNameAndDay(String schedName, String schedDay, String clientName) {
		return new ScheduleEncodingDao(sqlSessionFactory).deleteDataByClientNameAndSchedNameAndDay(schedName, schedDay,
				clientName) > 0;
	}

	public boolean deleteDataByClientNameAndSchedName(String clientName, String schedName) {
		return new ScheduleEncodingDao(sqlSessionFactory).deleteDataByClientNameAndSchedName(clientName, schedName) > 0;
	}

	public boolean deleteDataByPrikeyReferenceClient(Integer prikeyReferenceClient) {
		return new ScheduleEncodingDao(sqlSessionFactory).deleteDataByPrikeyReferenceClient(prikeyReferenceClient) > 0;
	}

	public boolean deleteDataByPrikeyReferenceClientAndSchedDay(Integer prikeyReferenceClient, String schedDay) {
		return new ScheduleEncodingDao(sqlSessionFactory)
				.deleteDataByPrikeyReferenceClientAndSchedDay(prikeyReferenceClient, schedDay) > 0;
	}

	public List<ScheduleEncoding> getAllData() {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllData();
	}

	public ScheduleEncoding getDataById(int id) {
		return new ScheduleEncodingDao(sqlSessionFactory).getDataById(id);
	}

	public ScheduleEncoding getSchedBySchedNameAndDay(String schedName, String day) {
		return new ScheduleEncodingDao(sqlSessionFactory).getSchedBySchedNameAndDay(schedName, day);
	}

	public ScheduleEncoding getScheduleImportedByEmpIdAndDay(Integer employeeID, String dayOfDate) {
		return new ScheduleEncodingDao(sqlSessionFactory).getScheduleImportedByEmpIdAndDay(employeeID, dayOfDate);
	}

	public List<ScheduleEncoding> getAllDistinctScheduleEncodingByClientName(String clientName) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllDistinctScheduleEncodingByClientName(clientName);
	}

	public List<ScheduleEncoding> getAllScheduleEncodingByEncodingName(String scheduleName) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleEncodingByEncodingName(scheduleName);
	}

	public List<ScheduleEncoding> getAllScheduleEncodingByClientName(String clientName) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleEncodingByClientName(clientName);
	}

	public List<ScheduleEncoding> getAllScheduleEncodingByClientNameAndScheduleName(String clientName,
			String scheduleName) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleEncodingByClientNameAndScheduleName(clientName,
				scheduleName);
	}

	public List<ScheduleEncoding> getAllScheduleByPrikeyReferenceClient(Integer prikeyReferenceClient) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleByPrikeyReferenceClient(prikeyReferenceClient);
	}

	public ScheduleEncoding getScheduleEncodingByClientNameAndScheduleNameAndScheduleDay(String clientName,
			String scheduleName, String scheduleDay) {
		return new ScheduleEncodingDao(sqlSessionFactory)
				.getScheduleEncodingByClientNameAndScheduleNameAndScheduleDay(clientName, scheduleName, scheduleDay);
	}

	public List<ScheduleEncoding> getAllScheduleUploadedByEmployeeId(Integer employeeID) {
		return new ScheduleEncodingDao(sqlSessionFactory).getAllScheduleUploadedByEmployeeId(employeeID);
	}

	public AnchorPane showBrowseScheduleEncoding() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseScheduleEncodingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseScheduleEncoding.fxml"));
		AnchorPane browseLayout = fxmlUtil.getFxmlPane();
		BrowseScheduleEncodingController controller = fxmlUtil.getController();
		controller.setMainApplication(this);

		this.primaryStage.setTitle("Schedule Encoding");
		// this.primaryStage.setResizable(false);
		this.primaryStage.setMaximized(true);
		return browseLayout;
	}

	public boolean showEditScheduleEncoding(ScheduleEncoding scheduleEncoding, ModificationType modificationType) {
		try {
			FxmlUtil<EditScheduleEncodingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditScheduleEncoding.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditScheduleEncodingController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " - Schedule Encoding", this.primaryStage,
					editLayout, scheduleEncoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ScheduleEncoding createScheduleEncodingObject(String schedDay, Time timeIn, Time timeOut, Time lunchIn,
			Time lunchOut, Integer offsetAllowed, ScheduleEncodingReference scheduleEncodingReference,
			Integer totalMinPerDay, Integer isBreakExempted) {
		ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
		// scheduleEncoding.setScheduleName(schedName);
		scheduleEncoding.setScheduleDay(schedDay);
		scheduleEncoding.setTimeIn(timeIn);
		scheduleEncoding.setTimeOut(timeOut);
		scheduleEncoding.setLunchIn(lunchIn);
		scheduleEncoding.setLunchOut(lunchOut);
		scheduleEncoding.setOffsetAllowed(offsetAllowed);
		// scheduleEncoding.setClientCode(clientCode);
		scheduleEncoding.setUser(this.getUser());
		scheduleEncoding.setChangedOnDate(this.getDateNow());
		scheduleEncoding.setChangedInComputer(this.getComputerName());
		scheduleEncoding.setScheduleEncodingReference(scheduleEncodingReference);
		scheduleEncoding.setTotalMinPerDay(totalMinPerDay);
		scheduleEncoding.setIsBreakExempted(isBreakExempted);

		return scheduleEncoding;
	}

	public void populateScheduleEncoding() {
		// List<ScheduleEncodingReference> scheduleEncodingReferenceList = new
		// ArrayList<>();
		if (!this.observableListScheduleEncoding.isEmpty()) {
			this.observableListScheduleEncoding.clear();
			// scheduleEncodingReferenceList.clear();
		}
		// this.observableListScheduleEncoding
		// .setAll(this.getAllDistinctScheduleEncodingByClientName(this.getClientName()));

		// scheduleEncodingReferenceList
		// .addAll(this.getScheduleEncodingReferenceMain().getAllScheduleByClientCode(this.getClient()));

		// for (ScheduleEncodingReference scheduleEncodingReference :
		// scheduleEncodingReferenceList) {
		//// this.observableListScheduleEncoding.setAll(
		//// this.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference()));
		//
		// this.observableListScheduleEncoding.setAll(scheduleEncodingReference);
		// }
		this.observableListScheduleEncoding.setAll(this.getAllScheduleByClientCodeDistinct(this.getClient()));

	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.showBrowseScheduleEncoding();
	}

	@Override
	public void initializeObjects() {
		// TODO Auto-generated method stub
		this.observableListScheduleEncoding = FXCollections.observableArrayList();
		this.observableListClient = FXCollections.observableArrayList();
		this.scheduleEncodingList = new ArrayList<>();
		this.objectToModifySelectedList = new ArrayList<>();
		this.objectToModifySelectedObjectList = new ArrayList<>();
		this.scheduleEncodingFromEdit = new ScheduleEncoding();
		this.enabledValidatedTextFieldList = new ArrayList<>();
		this.scheduleEncodingToDeleteList = new ArrayList<>();
		this.scheduleEncodingToSave = new ArrayList<>();
		this.client = new Client();
		this.isBreakExempted = false;
		try {
			this.clientMain = new ClientMain();
			this.scheduleEncodingReferenceMain = new ScheduleEncodingReferenceMain();
			this.employeeScheduleEncodingMain = new EmployeeScheduleEncodingMain();
			this.overtimeTypeMain = new OvertimeTypeMain();
			this.employeeMain = new EmployeeMain();
			this.employeeScheduleEncodingIrregularMain = new EmployeeScheduleEncodingIrregularMain();
			this.employmentHistoryMain = new EmploymentHistoryMain();
			this.employeeScheduleEncodingOvertimeMain = new EmployeeScheduleEncodingOvertimeMain();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public Integer stringToIntTimeConverter(ValidatedTextField textField) {
		Integer result = 0;

		if (textField.getText().isEmpty()) {
			return null;
		}

		if (textField.getText().length() <= 3) {
			return null;
		}

		String getTimeString = textField.getText();
		String[] splitTimeString = getTimeString.split(":");

		result = Integer.valueOf(splitTimeString[0] + splitTimeString[1]);
		return result;
	}

	public void timeConfig(ValidatedTextField textField) {
		String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
		String input = textField.getText();

		if (!input.isEmpty() && input.length() > 3) {
			Integer result = this.stringToIntTimeConverter(textField);

			if (result == null) {
				if (!textField.getSelectedText().matches(regex)) {
					textField.requestFocus();
					// textField.clear();
				}
				return;
			}

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

	public void setTextFieldFormat(ValidatedTextField textField) {
		textField.setFormat("##:##");
		textField.setPromptText("HH:mm");
	}

	public Integer[] formatStringToIntTime(List<ValidatedTextField> validatedTextFieldList, Boolean isBreakExempted) {
		Integer results[] = new Integer[4];
		results[0] = 0;
		results[1] = 0;
		results[2] = 0;
		results[3] = 0;
		Integer timeIn = 0;
		Integer timeOut = 0;
		Integer lunchIn = 0;
		Integer lunchOut = 0;

		timeIn = this.stringToIntTimeConverter(validatedTextFieldList.get(0));
		timeOut = this.stringToIntTimeConverter(validatedTextFieldList.get(1));
		if (!isBreakExempted) {
			lunchOut = this.stringToIntTimeConverter(validatedTextFieldList.get(2));
			lunchIn = this.stringToIntTimeConverter(validatedTextFieldList.get(3));
		}
		results[0] = timeIn;
		results[1] = timeOut;
		results[2] = lunchOut;
		results[3] = lunchIn;

		return results;
	}

	public boolean checkInvalidRangeField(Integer[] results, List<ValidatedTextField> validatedTextFieldList,
			Boolean isBreakExempted) {

		Integer timeIn = results[0];
		Integer timeOut = results[1];
		Integer lunchOut = results[2];
		Integer lunchIn = results[3];
		ValidatedTextField textFieldTimeIn = validatedTextFieldList.get(0);

		boolean isNightShift = false;

		if (timeIn != null && timeOut != null && lunchIn != null && lunchOut != null) {

			if (timeIn.equals(timeOut)) {
				AlertUtil.showErrorAlert("Time in cannot be the same with Time out.", this.getPrimaryStage());
				textFieldTimeIn.requestFocus();
				return true;
			}

			if (timeIn > timeOut) {
				// AlertUtil.showErrorAlert("Time in cannot be greater than Time out.",
				// this.getPrimaryStage());
				// textFieldTimeIn.requestFocus();
				// return true;
				isNightShift = true;
			}

			if (!isBreakExempted) {
				ValidatedTextField textFieldLunchOut = validatedTextFieldList.get(2);
				ValidatedTextField textFieldLunchIn = validatedTextFieldList.get(3);

				if (lunchIn.equals(lunchOut)) {
					AlertUtil.showErrorAlert("Lunch in cannot be the same with Lunch out", this.getPrimaryStage());
					textFieldLunchOut.requestFocus();
					return true;
				}

				if (!isNightShift) {
					if (lunchOut > lunchIn) {
						AlertUtil.showErrorAlert("Lunch out cannot be after Lunch in.", this.getPrimaryStage());
						textFieldLunchOut.requestFocus();
						return true;
					} else if (lunchOut > timeOut || lunchOut.compareTo(timeOut) == 0) {
						AlertUtil.showErrorAlert("Lunch out must be within Time in/out range", this.getPrimaryStage());
						textFieldLunchOut.requestFocus();
						return true;
					} else if (lunchIn > timeOut || lunchIn.compareTo(timeOut) == 0) {
						AlertUtil.showErrorAlert("Lunch in must be within Time in/out range", this.getPrimaryStage());
						textFieldLunchIn.requestFocus();
						return true;
					} else if (lunchOut < timeIn || lunchOut.compareTo(timeIn) == 0) {
						AlertUtil.showErrorAlert("Lunch out must be within Time in/out range", this.getPrimaryStage());
						textFieldLunchOut.requestFocus();
						return true;
					} else if (lunchIn < timeIn || lunchIn.compareTo(timeIn) == 0) {
						AlertUtil.showErrorAlert("Lunch in must be within Time in/out range", this.getPrimaryStage());
						textFieldLunchIn.requestFocus();
						return true;
					}
				} else {
					if ((lunchOut > timeOut && lunchOut < timeIn) || lunchOut.compareTo(timeIn) == 0) {
						AlertUtil.showErrorAlert("Lunch out must be within Time in/out range", this.getPrimaryStage());
						textFieldLunchOut.requestFocus();
						return true;
					} else if ((lunchIn > timeOut && lunchIn < timeIn) || lunchIn.compareTo(timeOut) == 0) {
						AlertUtil.showErrorAlert("Lunch in must be within Time in/out range", this.getPrimaryStage());
						textFieldLunchIn.requestFocus();
						return true;
					}
				}

			}
		}
		return false;
	}

	public void setTimeConfigTextField(ValidatedTextField textField) {
		this.setTextFieldFormat(textField);

		textField.setOnMouseMoved(event -> {
			this.timeConfig(textField);
		});

		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
				this.timeConfig(textField);
			}
		});
	}

	public boolean isValidateTextFieldExemptedClientValid(Boolean isBreakExempted,
			List<ValidatedTextField> textFieldList) {
		boolean isValid = true;

		Integer[] results = this.formatStringToIntTime(textFieldList, isBreakExempted);

		if (isBreakExempted) {
			if (textFieldList.get(0).getText().compareTo("") == 0
					|| textFieldList.get(1).getText().compareTo("") == 0) {
				return !isValid;
			}

			if (results[0] == null || results[1] == null) {
				return !isValid;
			}

			Integer hrTimeIn = results[0] / 100;
			Integer minTimeIn = results[0] % 100;
			Integer hrTimeOut = results[1] / 100;
			Integer minTimeOut = results[1] % 100;

			if (hrTimeIn > 23 || hrTimeOut > 23) {
				return !isValid;
			}

			if (minTimeIn > 59 || minTimeOut > 59) {
				return !isValid;
			}

			if (results[0] > 2359 || results[1] > 2359) {
				return !isValid;
			}

			if (textFieldList.get(0).getText().length() < 5 || textFieldList.get(1).getText().length() < 5) {
				return !isValid;
			}

			if ((textFieldList.get(0).getText().compareTo(textFieldList.get(1).getText()) == 0)) {
				return !isValid;
			}
		} else {
			if (textFieldList.get(0).getText().compareTo("") == 0
					|| textFieldList.get(1).getText().compareTo("") == 0) {
				return !isValid;
			}

			if (results[0] == null || results[1] == null || results[2] == null || results[3] == null) {
				return !isValid;
			}

			Integer hrTimeIn = results[0] / 100;
			Integer minTimeIn = results[0] % 100;
			Integer hrTimeOut = results[1] / 100;
			Integer minTimeOut = results[1] % 100;
			Integer hrLunchOut = results[2] / 100;
			Integer minLunchOut = results[2] % 100;
			Integer hrLunchIn = results[3] / 100;
			Integer minLunchIn = results[3] % 100;

			if (hrTimeIn > 23 || hrTimeOut > 23 || hrLunchIn > 23 || hrLunchOut > 23) {
				return !isValid;
			}

			if (minTimeIn > 59 || minTimeOut > 59 || minLunchIn > 59 || minLunchOut > 59) {
				return !isValid;
			}

			if (results[0] > 2359 || results[1] > 2359 || results[2] > 2359 || results[3] > 2359) {
				return !isValid;
			}

			if (textFieldList.get(0).getText().length() < 5 || textFieldList.get(1).getText().length() < 5
					|| textFieldList.get(2).getText().length() < 5 || textFieldList.get(3).getText().length() < 5) {
				return !isValid;
			}

			if ((textFieldList.get(0).getText().compareTo(textFieldList.get(1).getText()) == 0)
					|| (textFieldList.get(2).getText()).compareTo(textFieldList.get(3).getText()) == 0) {
				return !isValid;
			}
		}
		return isValid;
	}

	public Long computeTotalMinPerDay(List<ValidatedTextField> textFieldList, Boolean isBreakExempted) {
		long totalMinPerDay = 0;
		long timeInOutInterval = 0;
		long lunchInOutInterval = 0;
		long total = 0;
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");

		if (this.isValidateTextFieldExemptedClientValid(isBreakExempted, textFieldList)) {
			LocalTime timeIn = LocalTime.parse(textFieldList.get(0).getText(), format);
			LocalTime timeOut = LocalTime.parse(textFieldList.get(1).getText(), format);

			if (timeIn.isAfter(timeOut)) {
				Timestamp timeFrom = this.entryToTimestampConverter(this.getDateNow(), Time.valueOf(timeIn));
				Timestamp timeTo = this.entryToTimestampConverter(this.getDateNow(), Time.valueOf(timeOut));

				timeTo = this.entryToTimestampConverter(DateUtil.addDayOrMonthToDate(new Date(), 0, 1),
						Time.valueOf(timeOut));

				Integer timeInOutIntervalInt = this.computeTotalMinsEntry(timeFrom, timeTo);
				timeInOutInterval = new Long(timeInOutIntervalInt);

			} else {
				timeInOutInterval = ChronoUnit.MINUTES.between(timeIn, timeOut);
			}

			if (!isBreakExempted) {
				LocalTime lunchOut = LocalTime.parse(textFieldList.get(2).getText(), format);
				LocalTime lunchIn = LocalTime.parse(textFieldList.get(3).getText(), format);
				lunchInOutInterval = ChronoUnit.MINUTES.between(lunchOut, lunchIn);

				total = timeInOutInterval - lunchInOutInterval;

				if (total <= 0) {
					total = 0;
				}
				totalMinPerDay = total;
				return totalMinPerDay;
			}
			totalMinPerDay = timeInOutInterval;
			return totalMinPerDay;
		}

		return null;
	}

	public Timestamp entryToTimestampConverter(Date dateEntry, Time timeEntry) {
		Timestamp result = null;

		if (dateEntry != null && timeEntry != null) {
			String StringInput = new SimpleDateFormat("yyyy-MM-dd").format(dateEntry)
					.concat(" ".concat(new SimpleDateFormat("HH:mm:ss").format(timeEntry)));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTimeInput = LocalDateTime.from(formatter.parse(StringInput));
			result = Timestamp.valueOf(localDateTimeInput);
		}

		return result;
	}

	public Integer computeTotalMinsEntry(Timestamp timeFrom, Timestamp timeTo) {
		Integer totalMins = 0;

		long millisResult = timeTo.getTime() - timeFrom.getTime();
		BigDecimal seconds = BigDecimal.valueOf(millisResult).divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal minutes = seconds.divide(new BigDecimal(60), 4, RoundingMode.HALF_UP);

		totalMins = minutes.intValue();

		return totalMins;
	}

	public void showDetailsScheduleConfig(List<ValidatedTextField> validatedTextFieldList,
			ScheduleEncoding scheduleEncoding) {
		ValidatedTextField textFieldTimeIn = validatedTextFieldList.get(0);
		ValidatedTextField textFieldTimeOut = validatedTextFieldList.get(1);

		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Boolean isBreakExempted = scheduleEncoding.getIsBreakExempted() == null ? false
				: scheduleEncoding.getIsBreakExempted() == 1 ? true : false;

		if (!isBreakExempted) {
			ValidatedTextField textFieldLunchOut = validatedTextFieldList.get(2);
			ValidatedTextField textFieldLunchIn = validatedTextFieldList.get(3);
			ValidatedTextField textFieldTotalMinPerDay = validatedTextFieldList.get(4);

			textFieldTimeIn.setText(timeFormat.format(scheduleEncoding.getTimeIn()));
			textFieldTimeOut.setText(timeFormat.format(scheduleEncoding.getTimeOut()));
			textFieldLunchOut.setText(timeFormat.format(scheduleEncoding.getLunchOut()));
			textFieldLunchIn.setText(timeFormat.format(scheduleEncoding.getLunchIn()));
			textFieldTotalMinPerDay.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));
		} else {
			ValidatedTextField textFieldTotalMinPerDay = validatedTextFieldList.get(2);

			textFieldTimeIn.setText(timeFormat.format(scheduleEncoding.getTimeIn()));
			textFieldTimeOut.setText(timeFormat.format(scheduleEncoding.getTimeOut()));
			textFieldTotalMinPerDay.setText(String.valueOf(scheduleEncoding.getTotalMinPerDay()));

		}

	}

	public boolean isInvalidRangeForEachTextField(List<ValidatedTextField> validatedTextFieldMonList,
			List<ValidatedTextField> validatedTextFieldTuesList, List<ValidatedTextField> validatedTextFieldWedList,
			List<ValidatedTextField> validatedTextFieldThursList, List<ValidatedTextField> validatedTextFieldFriList,
			List<ValidatedTextField> validatedTextFieldSatList, List<ValidatedTextField> validatedTextFieldSunList,
			Boolean isBreakExempted) {
		boolean isInvalidRange = false;

		Integer[] resultsMonday = this.formatStringToIntTime(validatedTextFieldMonList, isBreakExempted);
		boolean isInvalidRangeMon = this.checkInvalidRangeField(resultsMonday, validatedTextFieldMonList,
				isBreakExempted);

		Integer[] resultsTuesday = this.formatStringToIntTime(validatedTextFieldTuesList, isBreakExempted);
		boolean isInvalidRangeTues = this.checkInvalidRangeField(resultsTuesday, validatedTextFieldTuesList,
				isBreakExempted);

		Integer[] resultsWednesday = this.formatStringToIntTime(validatedTextFieldWedList, isBreakExempted);
		boolean isInvalidRangeWed = this.checkInvalidRangeField(resultsWednesday, validatedTextFieldWedList,
				isBreakExempted);

		Integer[] resultsThursday = this.formatStringToIntTime(validatedTextFieldThursList, isBreakExempted);
		boolean isInvalidRangeThurs = this.checkInvalidRangeField(resultsThursday, validatedTextFieldThursList,
				isBreakExempted);

		Integer[] resultsFriday = this.formatStringToIntTime(validatedTextFieldFriList, isBreakExempted);
		boolean isInvalidRangeFri = this.checkInvalidRangeField(resultsFriday, validatedTextFieldFriList,
				isBreakExempted);

		Integer[] resultsSaturday = this.formatStringToIntTime(validatedTextFieldSatList, isBreakExempted);
		boolean isInvalidRangeSat = this.checkInvalidRangeField(resultsSaturday, validatedTextFieldSatList,
				isBreakExempted);

		Integer[] resultsSunday = this.formatStringToIntTime(validatedTextFieldSunList, isBreakExempted);
		boolean isInvalidRangeSun = this.checkInvalidRangeField(resultsSunday, validatedTextFieldSunList,
				isBreakExempted);

		if (isInvalidRangeMon) {
			isInvalidRange = isInvalidRangeMon;
		}

		if (isInvalidRangeTues) {
			isInvalidRange = isInvalidRangeTues;
		}

		if (isInvalidRangeWed) {
			isInvalidRange = isInvalidRangeWed;
		}

		if (isInvalidRangeFri) {
			isInvalidRange = isInvalidRangeThurs;
		}

		if (isInvalidRangeFri) {
			isInvalidRange = isInvalidRangeFri;
		}

		if (isInvalidRangeSat) {
			isInvalidRange = isInvalidRangeSat;
		}

		if (isInvalidRangeSun) {
			isInvalidRange = isInvalidRangeSun;
		}

		return isInvalidRange;
	}

	public void populateComboBoxClient() {
		this.observableListClient.setAll(this.clientMain.getClientByUser(this.getUser()));
	}

	public ObservableList<ScheduleEncoding> getObservableListScheduleEncoding() {
		return observableListScheduleEncoding;
	}

	public void setObservableListScheduleEncoding(ObservableList<ScheduleEncoding> observableListScheduleEncoding) {
		this.observableListScheduleEncoding = observableListScheduleEncoding;
	}

	public List<ScheduleEncoding> getScheduleEncodingList() {
		return scheduleEncodingList;
	}

	public void setScheduleEncodingList(List<ScheduleEncoding> scheduleEncodingList) {
		this.scheduleEncodingList = scheduleEncodingList;
	}

	public List<String> getObjectToModifySelectedList() {
		return objectToModifySelectedList;
	}

	public void setObjectToModifySelectedList(List<String> objectToModifySelectedList) {
		this.objectToModifySelectedList = objectToModifySelectedList;
	}

	public List<ScheduleEncoding> getObjectToModifySelectedObjectList() {
		return objectToModifySelectedObjectList;
	}

	public void setObjectToModifySelectedObjectList(List<ScheduleEncoding> objectToModifySelectedObjectList) {
		this.objectToModifySelectedObjectList = objectToModifySelectedObjectList;
	}

	public ScheduleEncoding getScheduleEncodingFromEdit() {
		return scheduleEncodingFromEdit;
	}

	public void setScheduleEncodingFromEdit(ScheduleEncoding scheduleEncodingFromEdit) {
		this.scheduleEncodingFromEdit = scheduleEncodingFromEdit;
	}

	public List<ScheduleEncoding> getScheduleEncodingToDeleteList() {
		return scheduleEncodingToDeleteList;
	}

	public void setScheduleEncodingToDeleteList(List<ScheduleEncoding> scheduleEncodingToDeleteList) {
		this.scheduleEncodingToDeleteList = scheduleEncodingToDeleteList;
	}

	public List<ValidatedTextField> getEnabledValidatedTextFieldList() {
		return enabledValidatedTextFieldList;
	}

	public void setEnabledValidatedTextFieldList(List<ValidatedTextField> enabledValidatedTextFieldList) {
		this.enabledValidatedTextFieldList = enabledValidatedTextFieldList;
	}

	public ObservableList<Client> getObservableListClient() {
		return observableListClient;
	}

	public void setObservableListClient(ObservableList<Client> observableListClient) {
		this.observableListClient = observableListClient;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public ClientMain getClientMain() {
		return clientMain;
	}

	public void setClientMain(ClientMain clientMain) {
		this.clientMain = clientMain;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public ScheduleEncodingReferenceMain getScheduleEncodingReferenceMain() {
		return scheduleEncodingReferenceMain;
	}

	public void setScheduleEncodingReferenceMain(ScheduleEncodingReferenceMain scheduleEncodingReferenceMain) {
		this.scheduleEncodingReferenceMain = scheduleEncodingReferenceMain;
	}

	public String getSchedName() {
		return schedName;
	}

	public void setSchedName(String schedName) {
		this.schedName = schedName;
	}

	public List<ScheduleEncoding> getScheduleEncodingToSave() {
		return scheduleEncodingToSave;
	}

	public void setScheduleEncodingToSave(List<ScheduleEncoding> scheduleEncodingToSave) {
		this.scheduleEncodingToSave = scheduleEncodingToSave;
	}

	public EmployeeScheduleEncodingMain getEmployeeScheduleEncodingMain() {
		return employeeScheduleEncodingMain;
	}

	public void setEmployeeScheduleEncodingMain(EmployeeScheduleEncodingMain employeeScheduleEncodingMain) {
		this.employeeScheduleEncodingMain = employeeScheduleEncodingMain;
	}

	public Boolean getIsBreakExempted() {
		return isBreakExempted;
	}

	public void setIsBreakExempted(Boolean isBreakExempted) {
		this.isBreakExempted = isBreakExempted;
	}

	public OvertimeTypeMain getOvertimeTypeMain() {
		return overtimeTypeMain;
	}

	public void setOvertimeTypeMain(OvertimeTypeMain overtimeTypeMain) {
		this.overtimeTypeMain = overtimeTypeMain;
	}

	public EmployeeMain getEmployeeMain() {
		return employeeMain;
	}

	public void setEmployeeMain(EmployeeMain employeeMain) {
		this.employeeMain = employeeMain;
	}

	public EmployeeScheduleEncodingIrregularMain getEmployeeScheduleEncodingIrregularMain() {
		return employeeScheduleEncodingIrregularMain;
	}

	public void setEmployeeScheduleEncodingIrregularMain(
			EmployeeScheduleEncodingIrregularMain employeeScheduleEncodingIrregularMain) {
		this.employeeScheduleEncodingIrregularMain = employeeScheduleEncodingIrregularMain;
	}

	public EmploymentHistoryMain getEmploymentHistoryMain() {
		return employmentHistoryMain;
	}

	public void setEmploymentHistoryMain(EmploymentHistoryMain employmentHistoryMain) {
		this.employmentHistoryMain = employmentHistoryMain;
	}

	public EmployeeScheduleEncodingOvertimeMain getEmployeeScheduleEncodingOvertimeMain() {
		return employeeScheduleEncodingOvertimeMain;
	}

	public void setEmployeeScheduleEncodingOvertimeMain(
			EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain) {
		this.employeeScheduleEncodingOvertimeMain = employeeScheduleEncodingOvertimeMain;
	}

}

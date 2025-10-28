package ph.com.lserv.production.employeescheduleuploadingovertimebreakdown;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.overtimetype.OvertimeTypeMain;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.user.UserMain;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lserv.production.employeescheduleencodingovertime.EmployeeScheduleEncodingOvertimeMain;
import ph.com.lserv.production.employeescheduleencodingovertime.model.EmployeeScheduleEncodingOvertime;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.data.EmployeeScheduleUploadingOvertimeBreakdownDao;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;
import ph.com.lserv.production.holidayencoding.HolidayEncodingMain;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;
import ph.com.lserv.production.overtimefiling.OvertimeFilingMain;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefilingclient.OvertimeFilingClientMain;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;
import ph.com.lserv.production.remarksreference.RemarksReferenceMain;
import ph.com.lserv.production.remarksreference.model.RemarksReference;

public class EmployeeScheduleUploadingOvertimeBreakdownMain
		extends MasterMain<EmployeeScheduleUploadingOvertimeBreakdown> {
	Stage stage;
	OvertimeTypeMain overtimeTypeMain;
	EmployeeScheduleUploadingMain employeeScheduleUploadingMain;
	EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain;
	OvertimeFilingMain overtimeFilingMain;
	OvertimeFilingClientMain overtimeFilingClientMain;
	HolidayEncodingMain holidayEncodingMain;
	RemarksReferenceMain remarksReferenceMain;
	UserMain userMain;

	Integer totalMinutesLeftBio;

	List<EmployeeScheduleUploadingOvertimeBreakdown> employeeScheduleUploadingOvertimeBreakdownList = new ArrayList<>();
	Timestamp timeInSched = new Timestamp(new Date().getTime());

	HashMap<Integer, HashSet<String>> employeesWithoutOvertimeConfigHashMap;
	HashSet<String> overtimeTypeConfigNotEncodedHashSet;
	List<EmployeeScheduleEncodingOvertime> encodedOvertimeConfigByEmployeeList;
	HashSet<String> overtimeTypeNotEncodedForManualEditList = new HashSet<>();

	boolean isNotEarlyOvertime = true;
	boolean isHalfDay = true;
	Timestamp earlyOvertimeTimeIn = new Timestamp(new Date().getTime());
	Timestamp earlyOvertimeTimeOut = new Timestamp(new Date().getTime());
	int totalMinsEarlyOvertime = 0;
	int otFilingClientListSize = 0;
	int otFilingListSize = 0;
	int totalMinsOTFilingGap = 0;
	OvertimeFilingClient earlyOvertimeFilingClient;
	OvertimeFiling earlyOvertimeFiling;

	boolean isWithError = false;

	List<Integer> clientPrikeyBreakdownOT30OT169List;
	List<Integer> clientPrikeyRulesBreakdownNotApplicableList;

	User userEditing;

	public EmployeeScheduleUploadingOvertimeBreakdownMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(EmployeeScheduleUploadingOvertimeBreakdown.class);
	}

	@Override
	public boolean createMainObject(
			EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.createData(employeeScheduleUploadingOvertimeBreakdown) > 0;
	}

	@Override
	public boolean updateMainObject(
			EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.updateData(employeeScheduleUploadingOvertimeBreakdown) > 0;
	}

	@Override
	public boolean deleteMainObject(
			EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.deleteData(employeeScheduleUploadingOvertimeBreakdown) > 0;
	}

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getDataByPrikeyEmployeeScheduleUploading(
			Integer employeeScheduleUploadingPrikey) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.getDataByPrikeyEmployeeScheduleUploading(employeeScheduleUploadingPrikey);
	}

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getAllDataByEmployeeCodePayFromPayTo(Integer employeeCode,
			Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.getAllDataByEmployeeCodePayFromPayTo(employeeCode, payFrom, payTo);
	}

	public List<Integer> getAllOvertimeTypeDistinctByPayFromPayToClientCd(Date payFrom, Date payTo, String clientCode) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.getAllOvertimeTypeDistinctByPayFromPayToClientCd(payFrom, payTo, clientCode);
	}

	public List<Integer> getAllPrikeyUploadingDistinctByPayFromPayTo(Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingOvertimeBreakdownDao(sqlSessionFactory)
				.getAllPrikeyUploadingDistinctByPayFromPayTo(payFrom, payTo);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return null;
	}

	@Override
	public void initializeObjects() {
		this.initializeCustomRules();

		this.earlyOvertimeFiling = new OvertimeFiling();
		this.earlyOvertimeFilingClient = new OvertimeFilingClient();

		try {
			this.overtimeTypeMain = new OvertimeTypeMain();
			this.employeeScheduleUploadingMain = new EmployeeScheduleUploadingMain();
			this.userMain = new UserMain();
			this.employeeScheduleEncodingOvertimeMain = new EmployeeScheduleEncodingOvertimeMain();
			this.overtimeFilingMain = new OvertimeFilingMain();
			this.holidayEncodingMain = new HolidayEncodingMain();
			this.remarksReferenceMain = new RemarksReferenceMain();
			this.employeesWithoutOvertimeConfigHashMap = new HashMap<>();
			this.overtimeTypeConfigNotEncodedHashSet = new HashSet<>();
			this.encodedOvertimeConfigByEmployeeList = new ArrayList<>();
			this.overtimeFilingClientMain = new OvertimeFilingClientMain();
			this.userEditing = new User();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void initializeCustomRules() {
		this.initializeRulesClientList();
		this.initializeOT30OT169ClientCustomList();
	}

	public void initializeRulesClientList() {
		// all client-company prikey not applicable with the ff rules:
		// * if OT > 3hrs = -30mins
		// * OT additional -15 mins
		this.clientPrikeyRulesBreakdownNotApplicableList = new ArrayList<>();
		this.clientPrikeyRulesBreakdownNotApplicableList.add(323);
	}

	public void initializeOT30OT169ClientCustomList() {
		// clients with custom ot30-ot169 ot breakdown:
		this.clientPrikeyBreakdownOT30OT169List = new ArrayList<>();
		this.clientPrikeyBreakdownOT30OT169List.add(323);
	}

	public EmployeeScheduleUploadingOvertimeBreakdown createObject(EmployeeScheduleUploading employeeScheduleUploading,
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime, OvertimeFiling overtimeFiling,
			OvertimeFilingClient overtimeFilingClient, Integer totalMin, OvertimeType overtimeType,
			boolean isFromManualEditing) {
		EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown = new EmployeeScheduleUploadingOvertimeBreakdown();

		employeeScheduleUploadingOvertimeBreakdown.setEmployeeScheduleUploading(employeeScheduleUploading);
		employeeScheduleUploadingOvertimeBreakdown
				.setEmploymentHistory(employeeScheduleUploading.getEmploymentHistory());
		employeeScheduleUploadingOvertimeBreakdown.setPayFrom(employeeScheduleUploading.getPayFrom());
		employeeScheduleUploadingOvertimeBreakdown.setPayTo(employeeScheduleUploading.getPayTo());

		employeeScheduleUploadingOvertimeBreakdown
				.setEmployeeScheduleEncodingOvertime(employeeScheduleEncodingOvertime);
		employeeScheduleUploadingOvertimeBreakdown.setOvertimeType(overtimeType);

		employeeScheduleUploadingOvertimeBreakdown.setClient(employeeScheduleUploading.getClient());
		employeeScheduleUploadingOvertimeBreakdown.setDepartment(employeeScheduleUploading.getDepartment());

		employeeScheduleUploadingOvertimeBreakdown.setTotalMin(totalMin);

		if (overtimeFiling != null) {
			employeeScheduleUploadingOvertimeBreakdown.setOvertimeFiling(overtimeFiling);

			employeeScheduleUploadingOvertimeBreakdown
					.setAllowedOvertime(this.timestampToTime(overtimeFiling.getDateTimeTo()));
			if (this.otFilingListSize > 1) {
				employeeScheduleUploadingOvertimeBreakdown.setEarlyOvertimeFiling(this.earlyOvertimeFiling);
			}
		} else {
			if (overtimeFilingClient != null) {
				employeeScheduleUploadingOvertimeBreakdown.setOvertimeFilingClient(overtimeFilingClient);
				employeeScheduleUploadingOvertimeBreakdown
						.setAllowedOvertime(this.timestampToTime(overtimeFilingClient.getDateTimeTo()));

				if (this.otFilingClientListSize > 1) {
					employeeScheduleUploadingOvertimeBreakdown
							.setEarlyOvertimeFilingClient(this.earlyOvertimeFilingClient);
				}
			}
		}

		employeeScheduleUploadingOvertimeBreakdown.setUser(this.getUserEditing());
		employeeScheduleUploadingOvertimeBreakdown.setChangedOnDate(new Date());
		employeeScheduleUploadingOvertimeBreakdown.setChangedInComputer(this.getComputerName());
		employeeScheduleUploadingOvertimeBreakdown
				.setChangedByUser(this.getUser() == null ? null : this.getUser().getUserName());

		// reset global var for next otfiling
		// as it is already set to OT_breakdown object
		this.earlyOvertimeTimeIn = null;
		this.earlyOvertimeTimeOut = null;
		this.earlyOvertimeFiling = null;
		this.earlyOvertimeFilingClient = null;

		// if galing sa process, save agad, else ineedit manually so add muna sa list
		if (!isFromManualEditing) {
			this.createMainObject(employeeScheduleUploadingOvertimeBreakdown);
		}

		return employeeScheduleUploadingOvertimeBreakdown;
	}

	public EmployeeScheduleUploadingOvertimeBreakdown updateObject(
			EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown,
			EmployeeScheduleUploading employeeScheduleUploading,
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime, OvertimeFiling overtimeFiling,
			OvertimeFilingClient overtimeFilingClient, Integer totalMin, boolean isFromManualEditing,
			OvertimeType overtimeType) {

		employeeScheduleUploadingOvertimeBreakdown.setEmployeeScheduleUploading(employeeScheduleUploading);
		employeeScheduleUploadingOvertimeBreakdown
				.setEmploymentHistory(employeeScheduleUploading.getEmploymentHistory());
		employeeScheduleUploadingOvertimeBreakdown.setPayFrom(employeeScheduleUploading.getPayFrom());
		employeeScheduleUploadingOvertimeBreakdown.setPayTo(employeeScheduleUploading.getPayTo());
		employeeScheduleUploadingOvertimeBreakdown
				.setEmployeeScheduleEncodingOvertime(employeeScheduleEncodingOvertime);
		employeeScheduleUploadingOvertimeBreakdown.setAllowedOvertime(employeeScheduleUploading.getAllowedOvertime());

		if (overtimeFiling != null) {
			employeeScheduleUploadingOvertimeBreakdown.setOvertimeFiling(overtimeFiling);

			if (this.otFilingListSize > 1) {
				employeeScheduleUploadingOvertimeBreakdown.setEarlyOvertimeFiling(this.earlyOvertimeFiling);
			}

		} else {
			if (overtimeFilingClient != null) {
				employeeScheduleUploadingOvertimeBreakdown.setOvertimeFilingClient(overtimeFilingClient);

				if (this.otFilingClientListSize > 1) {
					employeeScheduleUploadingOvertimeBreakdown
							.setEarlyOvertimeFilingClient(this.earlyOvertimeFilingClient);
				}
			}
		}

		employeeScheduleUploadingOvertimeBreakdown.setOvertimeType(overtimeType);
		employeeScheduleUploadingOvertimeBreakdown.setClient(employeeScheduleUploading.getClient());
		employeeScheduleUploadingOvertimeBreakdown.setDepartment(employeeScheduleUploading.getDepartment());

		employeeScheduleUploadingOvertimeBreakdown.setTotalMin(totalMin);

		employeeScheduleUploadingOvertimeBreakdown.setUser(this.getUserEditing());
		employeeScheduleUploadingOvertimeBreakdown.setChangedOnDate(new Date());
		employeeScheduleUploadingOvertimeBreakdown.setChangedInComputer(this.getComputerName());
		employeeScheduleUploadingOvertimeBreakdown
				.setChangedByUser(this.getUser() == null ? null : this.getUser().getUserName());

		this.earlyOvertimeTimeIn = null;
		this.earlyOvertimeTimeOut = null;
		this.earlyOvertimeFiling = null;
		this.earlyOvertimeFilingClient = null;

		if (!isFromManualEditing) {
			this.updateMainObject(employeeScheduleUploadingOvertimeBreakdown);
		}

		return employeeScheduleUploadingOvertimeBreakdown;
	}

	public boolean isLunchExempted(EmployeeScheduleUploading employeeScheduleUploading) {
		if (employeeScheduleUploading.getScheduleEncoding() != null) {
			if (employeeScheduleUploading.getScheduleEncoding().getIsBreakExempted() == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
				if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getIsBreakExempted() == 0) {
					return false;
				} else {
					return true;
				}
			}
		}
		return true;
	}

	public Timestamp entryToTimestamp(Date dateEntry, Time timeEntry) {
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

	public String[] getDateAndTimeEntryFromTimestamp(Timestamp entry) {
		if (entry != null) {
			String[] result = String.format("%1$TF %1$TT", entry).split("\\s");
			return result;
		}
		return null;
	}

	public Time getTimeFromTimestamp(Timestamp entry) {
		String[] result = this.getDateAndTimeEntryFromTimestamp(entry);
		String timeEntryToBeSave = result[1].substring(0, 6).concat("00");
		Time timeResult = Time.valueOf(timeEntryToBeSave);
		return timeResult;
	}

	public Integer computeTotalMinsEntry(Timestamp timeFrom, Timestamp timeTo) {

		Integer totalMins = 0;

		long millisResult = timeTo.getTime() - timeFrom.getTime();

		Integer millisInt = Math.toIntExact(millisResult);
		Integer seconds = millisInt / 1000;
		Integer minutes = seconds / 60;

		totalMins = minutes;

		return totalMins;
	}

	public Integer checkIfOvertimeFilingHasGap(List<OvertimeFiling> overtimeFilingList,
			List<OvertimeFilingClient> overtimeFilingClientList, Timestamp timeInEntry, Timestamp timeOutEntry,
			EmployeeScheduleUploading employeeScheduleUploading, boolean isRestDay) {
		this.isWithError = false;
		Integer totalMinsOTFilingGap = 0;
		Integer allTotalMinsOTFiling = 0;
		Integer eachTotalMinsOTFiling = 0;
		this.totalMinsOTFilingGap = 0;

		Timestamp timeStartOT = new Timestamp(new Date().getTime());
		Timestamp timeEndOT = new Timestamp(new Date().getTime());

		Timestamp timeStart = new Timestamp(new Date().getTime());
		Timestamp timeEnd = new Timestamp(new Date().getTime());

		if (overtimeFilingList != null && !overtimeFilingList.isEmpty()) {
			if (overtimeFilingList.size() > 1) {
				// possible 2 or more overtime filing same day if pinabalik -- may gap sa bio
				// OT filing MUST be seperate if same day to exclude the gap in totalMins

				timeStartOT = overtimeFilingList.get(0).getDateTimeFrom();
				timeEndOT = overtimeFilingList.get(overtimeFilingList.size() - 1).getBypassDateTimeTo() != null
						? overtimeFilingList.get(overtimeFilingList.size() - 1).getBypassDateTimeTo()
						: overtimeFilingList.get(overtimeFilingList.size() - 1).getDateTimeTo();

				if (timeInEntry.after(timeStartOT)) {
					timeStart = timeInEntry;

					if (timeOutEntry.before(timeEndOT)) {
						timeEnd = timeOutEntry;
					} else {
						timeEnd = timeEndOT;
					}
				} else {
					timeStart = timeStartOT;

					if (timeOutEntry.before(timeEndOT)) {
						timeEnd = timeOutEntry;
					} else {
						timeEnd = timeEndOT;
					}
				}

				allTotalMinsOTFiling = this.computeTotalMinsEntry(timeStart, timeEnd);

				for (OvertimeFiling overtimeFiling : overtimeFilingList) {
					timeStartOT = overtimeFiling.getDateTimeFrom();
					timeEndOT = overtimeFiling.getBypassDateTimeTo() != null ? overtimeFiling.getBypassDateTimeTo()
							: overtimeFiling.getDateTimeTo();

					if (timeStartOT.before(timeInEntry)) {
						this.isWithError = true;
					}

					if (timeStartOT.after(timeOutEntry)) {
						this.isWithError = true;
					}

					if (timeOutEntry.after(timeEndOT) || timeOutEntry.equals(timeEndOT)) {
						eachTotalMinsOTFiling = eachTotalMinsOTFiling + this.computeTotalMinsEntry(
								overtimeFiling.getDateTimeFrom(), overtimeFiling.getDateTimeTo());
					}
				}

				// compute total mins of gap then subtract to totalMinsEntry
				totalMinsOTFilingGap = allTotalMinsOTFiling - eachTotalMinsOTFiling;

				if (totalMinsOTFilingGap >= 540) {
					// exclude work mins and lunch
					totalMinsOTFilingGap = totalMinsOTFilingGap - 540;
				}
			} else {
				if (!isRestDay) {
					timeStartOT = overtimeFilingList.get(0).getDateTimeFrom();
					timeEndOT = overtimeFilingList.get(0).getDateTimeTo();

					if (timeStartOT.after(timeOutEntry) && timeEndOT.after(timeOutEntry)) {
						this.isWithError = true;
					}
				}
			}
		}

		if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {
			if (overtimeFilingClientList.size() > 1) {
				timeStartOT = overtimeFilingClientList.get(0).getDateTimeFrom();
				timeEndOT = overtimeFilingClientList.get(overtimeFilingClientList.size() - 1).getDateTimeTo();

				if (timeInEntry.after(timeStartOT) || timeInEntry.equals(timeStartOT)) {
					timeStart = timeInEntry;
				} else {
					timeStart = timeStartOT;
				}

				if (timeOutEntry.before(timeEndOT)) {
					timeEnd = timeOutEntry;
				} else {
					timeEnd = timeEndOT;
				}

				allTotalMinsOTFiling = this.computeTotalMinsEntry(timeStart, timeEnd);

				eachTotalMinsOTFiling = 0;
				for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {

					timeStartOT = overtimeFilingClient.getDateTimeFrom();
					timeEndOT = overtimeFilingClient.getDateTimeTo();

					if (timeStartOT.before(timeInEntry)) {
						this.isWithError = true;
					}

					if (timeStartOT.after(timeOutEntry)) {
						this.isWithError = true;
					}

					if (timeOutEntry.after(timeEndOT) || timeOutEntry.equals(timeEndOT)) {
						eachTotalMinsOTFiling = eachTotalMinsOTFiling + this.computeTotalMinsEntry(
								overtimeFilingClient.getDateTimeFrom(), overtimeFilingClient.getDateTimeTo());
					}

					totalMinsOTFilingGap = allTotalMinsOTFiling - eachTotalMinsOTFiling;

					if (totalMinsOTFilingGap >= 540) {
						totalMinsOTFilingGap = totalMinsOTFilingGap - 540;
					}
				}
			} else {
				if (!isRestDay) {
					timeStartOT = overtimeFilingClientList.get(0).getDateTimeFrom();
					timeEndOT = overtimeFilingClientList.get(0).getDateTimeTo();

					if (timeStartOT.after(timeOutEntry) && timeEndOT.after(timeOutEntry)) {
						this.isWithError = true;
					}
				} else {

				}
			}
		}

		// check if there's a gap between time out schedule and first ot time start
		Timestamp timeOutSchedule = new Timestamp(new Date().getTime());
		if (employeeScheduleUploading.getIsRegularSchedule() == 1) {
			if (employeeScheduleUploading.getScheduleEncoding() != null) {
				timeOutSchedule = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						Time.valueOf(new SimpleDateFormat("HH:mm:ss")
								.format(employeeScheduleUploading.getScheduleEncoding().getTimeOut())));
			}
		} else {
			if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
				timeOutSchedule = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeOut());
			}
		}

		if (overtimeFilingList != null && !overtimeFilingList.isEmpty()) {
			if (timeOutSchedule != null) {
				if (overtimeFilingList.get(0).getDateTimeFrom().after(timeOutSchedule)) {

					int gapMinutes = this.computeTotalMinsEntry(timeOutSchedule,
							overtimeFilingList.get(0).getDateTimeFrom());
					totalMinsOTFilingGap = totalMinsOTFilingGap + gapMinutes;
				}
			}
		} else if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {
			if (overtimeFilingClientList.get(0).getDateTimeFrom().after(timeOutSchedule)) {
				int gapMinutes = this.computeTotalMinsEntry(timeOutSchedule,
						overtimeFilingClientList.get(0).getDateTimeFrom());
				totalMinsOTFilingGap = totalMinsOTFilingGap + gapMinutes;
			}
		}

		this.totalMinsOTFilingGap = totalMinsOTFilingGap;
		return totalMinsOTFilingGap;
	}

	public HashMap<Integer, HashSet<String>> breakdownOvertime(Integer employeeId, Date payFrom, Date payTo,
			User userUploading) {
		this.initializeObjects();
		this.isWithError = false;
		this.userEditing = userUploading;
		this.overtimeTypeConfigNotEncodedHashSet = new HashSet<>();

		List<EmployeeScheduleUploading> bioByEmployeeByPayPeriodList = new ArrayList<>();
		bioByEmployeeByPayPeriodList
				.addAll(this.getEmployeeScheduleUploadingMain().getDataByEmployeeIdPayFrom(employeeId, payFrom));

		User user = new User();
		user = this.getUserMain().getUserByEmployeeId(employeeId);

		for (EmployeeScheduleUploading employeeScheduleUploading : bioByEmployeeByPayPeriodList) {
			this.encodedOvertimeConfigByEmployeeList = new ArrayList<>();

			this.employeeScheduleUploadingOvertimeBreakdownList = this
					.getDataByPrikeyEmployeeScheduleUploading(employeeScheduleUploading.getPrimaryKey());

			Boolean isRestDay = employeeScheduleUploading.getIsRestDay() != null
					? employeeScheduleUploading.getIsRestDay() == 1 ? true : false
					: false;
			Boolean isRegularHoliday = employeeScheduleUploading.getIsHoliday() != null
					? employeeScheduleUploading.getIsHoliday() == 1 ? true : false
					: false;
			Boolean isSpecialHoliday = employeeScheduleUploading.getIsHoliday() != null
					? employeeScheduleUploading.getIsHoliday() == 2 ? true : false
					: false;
			Boolean isRegularSchedule = employeeScheduleUploading.getIsRegularSchedule() == null ? false
					: employeeScheduleUploading.getIsRegularSchedule() == 1 ? true : false;
			Boolean isDoubleHoliday = this.isDateEntryDoubleHoliday(employeeScheduleUploading);

			String dateEntry = new SimpleDateFormat("yyyy-MM-dd").format(employeeScheduleUploading.getDateEntry())
					.concat("%");

			if (employeeScheduleUploading.getTimeInEntry() != null
					&& employeeScheduleUploading.getTimeOutEntry() != null) {

				this.fetchOvertimeConfig(employeeScheduleUploading, isRegularSchedule);

				if (user != null) {
					// LSERV
					List<OvertimeFiling> overtimeFilingList = new ArrayList<>();
					overtimeFilingList = this.getOvertimeFilingMain()
							.getAllOvertimeByOvertimeFromAndPrikeyUser(dateEntry, user.getPrimaryKey());

					if (overtimeFilingList.size() != 0 && this.encodedOvertimeConfigByEmployeeList.size() != 0) {

						this.checkIfOvertimeStillExists(employeeScheduleUploading);

						Timestamp timeTo = this.computeEntryTotalMins(overtimeFilingList, null,
								employeeScheduleUploading, isRegularSchedule, isRegularHoliday, isSpecialHoliday,
								isRestDay);

						this.startBreakdownOvertime(isRestDay, isRegularHoliday, isSpecialHoliday, isDoubleHoliday,
								overtimeFilingList, null, employeeScheduleUploading, timeTo, false);

					}
				} else {
					// other client
					List<OvertimeFilingClient> overtimeFilingClientList = new ArrayList<>();
					overtimeFilingClientList = this.getOvertimeFilingClientMain().getDataByEmpIdAndOvertimeDateFrom(
							employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode(),
							employeeScheduleUploading.getDateEntry());

					if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {

						this.checkIfOvertimeStillExists(employeeScheduleUploading);

						Timestamp timeTo = this.computeEntryTotalMins(null, overtimeFilingClientList,
								employeeScheduleUploading, isRegularSchedule, isRegularHoliday, isSpecialHoliday,
								isRestDay);

						this.startBreakdownOvertime(isRestDay, isRegularHoliday, isSpecialHoliday, isDoubleHoliday,
								null, overtimeFilingClientList, employeeScheduleUploading, timeTo, false);
					}

				}
			}

			this.employeeScheduleUploadingOvertimeBreakdownList.forEach(employeeScheduleUploadingOvertimeBreakdown -> {
				this.deleteMainObject(employeeScheduleUploadingOvertimeBreakdown);
			});
		}

		if (!this.overtimeTypeConfigNotEncodedHashSet.isEmpty()
				|| this.overtimeTypeConfigNotEncodedHashSet.size() > 0) {
			this.employeesWithoutOvertimeConfigHashMap.put(employeeId, this.overtimeTypeConfigNotEncodedHashSet);
		}

		return this.employeesWithoutOvertimeConfigHashMap;
	}

	public void checkIfOvertimeStillExists(EmployeeScheduleUploading employeeScheduleUploading) {
		// if there is overtime breakdown but the ot filing is deleted or missing,
		// delete the ot breakdown. this is for reupload.
		boolean isIntegralEmployee = employeeScheduleUploading.getEmploymentHistory().getClient().getClientCode()
				.equals("LBPSC");

		if (employeeScheduleUploading.getOvertimeBreakdownList() != null
				&& !employeeScheduleUploading.getOvertimeBreakdownList().isEmpty()) {
			employeeScheduleUploading.getOvertimeBreakdownList().forEach(overtimeBreakdown -> {
				if (isIntegralEmployee) {

					Boolean isEarlyOTNull = overtimeBreakdown.getEarlyOvertimeFiling() == null;
					Boolean isOTNull = overtimeBreakdown.getOvertimeFiling() == null;

					if (isEarlyOTNull) {
						if (isOTNull) {
							this.deleteMainObject(overtimeBreakdown);
						}
					}
				} else {
					Boolean isEarlyOTClientNull = overtimeBreakdown.getEarlyOvertimeFilingClient() == null;
					Boolean isOTClientNull = overtimeBreakdown.getOvertimeFilingClient() == null;

					if (isEarlyOTClientNull) {
						if (isOTClientNull) {
							this.deleteMainObject(overtimeBreakdown);
						}
					}
				}
			});
		}
	}

	public Timestamp computeEntryTotalMins(List<OvertimeFiling> overtimeFilingList,
			List<OvertimeFilingClient> overtimeFilingClientList, EmployeeScheduleUploading employeeScheduleUploading,
			boolean isRegularSchedule, boolean isRegularHoliday, boolean isSpecialHoliday, boolean isRestDay) {
		Time overtimeFilingAllowedOvertime = new Time(System.currentTimeMillis());

		if (overtimeFilingList != null && !overtimeFilingList.isEmpty()) {
			overtimeFilingAllowedOvertime = this.getTimeFromTimestamp(overtimeFilingList.get(0).getDateTimeTo());
			if (overtimeFilingList.size() > 1) {
				overtimeFilingAllowedOvertime = this
						.getTimeFromTimestamp(overtimeFilingList.get(overtimeFilingList.size() - 1).getDateTimeTo());
			}
		} else {
			if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {
				overtimeFilingAllowedOvertime = this
						.getTimeFromTimestamp(overtimeFilingClientList.get(0).getDateTimeTo());
				if (overtimeFilingClientList.size() > 1) {
					overtimeFilingAllowedOvertime = this.getTimeFromTimestamp(
							overtimeFilingClientList.get(overtimeFilingClientList.size() - 1).getDateTimeTo());
				}
			}
		}

		String timeInEntryTime = new SimpleDateFormat("HH:mm:00")
				.format(this.getTimeFromTimestamp(employeeScheduleUploading.getTimeInEntry()));

		String timeOutEntryTime = new SimpleDateFormat("HH:mm:00")
				.format(this.getTimeFromTimestamp(employeeScheduleUploading.getTimeOutEntry()));

		Timestamp timeFrom = this.entryToTimestamp(
				DateFormatter.toDate(employeeScheduleUploading.getTimeInEntry().toLocalDateTime().toLocalDate()),
				Time.valueOf(timeInEntryTime));

		Timestamp timeTo = this.entryToTimestamp(
				DateFormatter.toDate(employeeScheduleUploading.getTimeOutEntry().toLocalDateTime().toLocalDate()),
				overtimeFilingAllowedOvertime);

		Timestamp timeToEntry = this.entryToTimestamp(
				DateFormatter.toDate(employeeScheduleUploading.getTimeOutEntry().toLocalDateTime().toLocalDate()),
				Time.valueOf(timeOutEntryTime));

		Integer totalMinsOTFilingGap = this.checkIfOvertimeFilingHasGap(overtimeFilingList, overtimeFilingClientList,
				timeFrom, timeToEntry, employeeScheduleUploading, isRestDay);

		Timestamp result[] = this.computeInitialTotalMinsLeft(isRegularSchedule, isRegularHoliday, isSpecialHoliday,
				isRestDay, employeeScheduleUploading, timeFrom, timeTo, timeToEntry, overtimeFilingList,
				overtimeFilingClientList, totalMinsOTFilingGap);
		timeFrom = result[0];
		timeTo = result[1];

		this.computeLunch(isRegularSchedule, employeeScheduleUploading, timeFrom, timeTo, isRestDay);

		return timeTo;
	}

	// public Timestamp getNightDiffStart(EmployeeScheduleUploading
	// employeeScheduleUploading) {
	// EmployeeScheduleEncodingOvertime OT10config = new
	// EmployeeScheduleEncodingOvertime();
	//
	// Time nightDiffStartTime = Time.valueOf("22:00:00");
	// Timestamp nightDiffStart =
	// this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
	// nightDiffStartTime);
	//
	// if (this.isOvertimeConfigEncoded("OT10%")) {
	// OT10config = this.encodedOvertimeConfigByEmployeeList.stream()
	// .filter(p ->
	// p.getOvertimeType().getOvertimeName().equals("OT10%")).findFirst().get();
	// nightDiffStart =
	// this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
	// OT10config.getTimeStart());
	// }
	// return nightDiffStart;
	// }

	public List<EmployeeScheduleUploadingOvertimeBreakdown> startBreakdownOvertime(boolean isRestDay,
			boolean isRegularHoliday, boolean isSpecialHoliday, boolean isDoubleHoliday,
			List<OvertimeFiling> overtimeFilingList, List<OvertimeFilingClient> overtimeFilingClientList,
			EmployeeScheduleUploading employeeScheduleUploading, Timestamp timeTo, boolean isFromManualEditing) {

		List<EmployeeScheduleUploadingOvertimeBreakdown> resultList = new ArrayList<>();
		this.overtimeTypeNotEncodedForManualEditList.clear();

		if (overtimeFilingList != null) {
			this.otFilingListSize = overtimeFilingList.size();
		} else if (overtimeFilingClientList != null) {
			this.otFilingClientListSize = overtimeFilingClientList.size();
		}

		Time nightDiffStartTime = Time.valueOf("22:00:00");
		Timestamp nightDiffStart = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(), nightDiffStartTime);

		this.excludeEarlyTimeIn(employeeScheduleUploading);

		if (isRestDay) {
			// rest day, double holiday (1 reg holiday, 1 special holiday)
			if (isDoubleHoliday) {
				if (overtimeFilingList != null) {
					for (OvertimeFiling overtimeFiling : overtimeFilingList) {
						this.isNotEarlyOvertime = true;
						Timestamp otStart = overtimeFiling.getDateTimeFrom();
						Timestamp otEnd = overtimeFiling.getDateTimeTo();
						// ordinary day OT
						this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
								"OT390%");

						resultList.add(this.computeFirstOvertime("OT390%", otStart, otEnd, overtimeFiling, null,
								employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
								overtimeFilingList, null));

						resultList.add(this.computeOvertime("OT507%", otStart, nightDiffStart, overtimeFiling, null,
								employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

						resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null, otStart,
								otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true, isRestDay,
								isFromManualEditing));
					}
				} else {
					for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
						this.isNotEarlyOvertime = true;
						Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
						Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
						// ordinary day OT
						this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null, overtimeFilingClient,
								"OT390%");

						resultList.add(this.computeFirstOvertime("OT390%", otStart, otEnd, null, overtimeFilingClient,
								employeeScheduleUploading, true, false, isRestDay, isFromManualEditing, null,
								overtimeFilingClientList));

						resultList
								.add(this.computeOvertime("OT507%", otStart, nightDiffStart, null, overtimeFilingClient,
										employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

						resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null, overtimeFilingClient,
								otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
								isRestDay, isFromManualEditing));
					}
				}
			} else {
				if (isRegularHoliday) {
					// rest day and regular holiday

					if (overtimeFilingList != null) {
						for (OvertimeFiling overtimeFiling : overtimeFilingList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFiling.getDateTimeFrom();
							Timestamp otEnd = overtimeFiling.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
									"OT160%");

							resultList.add(this.computeFirstOvertime("OT160%", otStart, otEnd, overtimeFiling, null,
									employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
									overtimeFilingList, null));

							resultList.add(this.computeOvertime("OT338%", otStart, nightDiffStart, overtimeFiling, null,
									employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
									otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
									isRestDay, isFromManualEditing));
						}
					} else {
						for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
							Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
									overtimeFilingClient, "OT160%");

							resultList.add(this.computeFirstOvertime("OT160%", otStart, otEnd, null,
									overtimeFilingClient, employeeScheduleUploading, true, false, isRestDay,
									isFromManualEditing, null, overtimeFilingClientList));

							resultList.add(
									this.computeOvertime("OT338%", otStart, nightDiffStart, null, overtimeFilingClient,
											employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
									overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
									employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
						}
					}

				} else if (isSpecialHoliday) {
					// rest day and special holiday
					if (overtimeFilingList != null) {
						for (OvertimeFiling overtimeFiling : overtimeFilingList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFiling.getDateTimeFrom();
							Timestamp otEnd = overtimeFiling.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
									"OT30%");

							resultList.add(this.computeFirstOvertime("OT30%", otStart, otEnd, overtimeFiling, null,
									employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
									overtimeFilingList, null));

							resultList.add(this.computeOvertime("OT195%", otStart, nightDiffStart, overtimeFiling, null,
									employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
									otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
									isRestDay, isFromManualEditing));
						}
					} else {
						for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
							Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
									overtimeFilingClient, "OT30%");

							resultList.add(this.computeFirstOvertime("OT30%", otStart, otEnd, null,
									overtimeFilingClient, employeeScheduleUploading, true, false, isRestDay,
									isFromManualEditing, null, overtimeFilingClientList));

							resultList.add(
									this.computeOvertime("OT195%", otStart, nightDiffStart, null, overtimeFilingClient,
											employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
									overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
									employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
						}
					}

				} else {
					// rest day not holiday
					Boolean isDaily = employeeScheduleUploading.getEmploymentHistory().getEmploymentConfiguration()
							.getIsDaily();

					if (isDaily) {
						if (overtimeFilingList != null) {
							for (OvertimeFiling overtimeFiling : overtimeFilingList) {
								this.isNotEarlyOvertime = true;
								Timestamp otStart = overtimeFiling.getDateTimeFrom();
								Timestamp otEnd = overtimeFiling.getDateTimeTo();
								// ordinary day OT
								this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling,
										null, "OT130%");

								resultList.add(this.computeFirstOvertime("OT130%", otStart, otEnd, overtimeFiling, null,
										employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
										overtimeFilingList, null));

								resultList.add(this.computeOvertime("OT169%", otStart, nightDiffStart, overtimeFiling,
										null, employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

								resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
										otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
										isRestDay, isFromManualEditing));
							}
						} else {
							for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
								if (this.clientPrikeyBreakdownOT30OT169List
										.contains(overtimeFilingClient.getClient().getPrimaryKey())) {
									for (Integer prikeyClient : clientPrikeyBreakdownOT30OT169List) {
										switch (prikeyClient) {
										case 323:
											this.breakdownPHLSHSSOvertime150(overtimeFilingClient,
													employeeScheduleUploading, resultList, nightDiffStart, isRestDay,
													isFromManualEditing, timeTo, overtimeFilingClientList);
											break;
										default:
											break;
										}
									}
								} else {
									this.isNotEarlyOvertime = true;
									Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
									Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
									// ordinary day OT
									this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
											overtimeFilingClient, "OT130%");

									resultList.add(this.computeFirstOvertime("OT130%", otStart, otEnd, null,
											overtimeFilingClient, employeeScheduleUploading, true, false, isRestDay,
											isFromManualEditing, null, overtimeFilingClientList));

									resultList.add(this.computeOvertime("OT169%", otStart, nightDiffStart, null,
											overtimeFilingClient, employeeScheduleUploading, false, false, isRestDay,
											isFromManualEditing));

									resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
											overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
											employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
								}
							}
						}

					} else {
						if (overtimeFilingList != null) {
							for (OvertimeFiling overtimeFiling : overtimeFilingList) {
								this.isNotEarlyOvertime = true;
								Timestamp otStart = overtimeFiling.getDateTimeFrom();
								Timestamp otEnd = overtimeFiling.getDateTimeTo();
								// ordinary day OT
								this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling,
										null, "OT30%");

								resultList.add(this.computeFirstOvertime("OT30%", otStart, otEnd, overtimeFiling, null,
										employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
										overtimeFilingList, null));

								resultList.add(this.computeOvertime("OT169%", otStart, nightDiffStart, overtimeFiling,
										null, employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

								resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
										otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
										isRestDay, isFromManualEditing));
							}
						} else {

							for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
								if (this.clientPrikeyBreakdownOT30OT169List
										.contains(overtimeFilingClient.getClient().getCompany().getCompanyCode())) {
									for (Integer prikeyClient : clientPrikeyBreakdownOT30OT169List) {
										switch (prikeyClient) {
										case 323:
											this.breakdownPHLSHSSOvertime150(overtimeFilingClient,
													employeeScheduleUploading, resultList, nightDiffStart, isRestDay,
													isFromManualEditing, timeTo, overtimeFilingClientList);
											break;
										default:
											break;
										}
									}
								} else {
									this.isNotEarlyOvertime = true;
									Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
									Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
									// ordinary day OT
									this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
											overtimeFilingClient, "OT30%");

									resultList.add(this.computeFirstOvertime("OT30%", otStart, otEnd, null,
											overtimeFilingClient, employeeScheduleUploading, true, false, isRestDay,
											isFromManualEditing, null, overtimeFilingClientList));

									resultList.add(this.computeOvertime("OT169%", otStart, nightDiffStart, null,
											overtimeFilingClient, employeeScheduleUploading, false, false, isRestDay,
											isFromManualEditing));

									resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
											overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
											employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
								}
							}
						}
					}
				}
			}
		} else {

			// regular day, double holiday
			if (isDoubleHoliday) {
				if (overtimeFilingList != null) {
					for (OvertimeFiling overtimeFiling : overtimeFilingList) {
						this.isNotEarlyOvertime = true;
						Timestamp otStart = overtimeFiling.getDateTimeFrom();
						Timestamp otEnd = overtimeFiling.getDateTimeTo();
						// ordinary day OT
						this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
								"OT300%");

						resultList.add(this.computeFirstOvertime("OT300%", otStart, otEnd, overtimeFiling, null,
								employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
								overtimeFilingList, null));

						resultList.add(this.computeOvertime("OT390%", otStart, nightDiffStart, overtimeFiling, null,
								employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

						resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null, otStart,
								otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true, isRestDay,
								isFromManualEditing));
					}
				} else {
					for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
						this.isNotEarlyOvertime = true;
						Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
						Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
						// ordinary day OT
						this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null, overtimeFilingClient,
								"OT300%");

						resultList.add(this.computeFirstOvertime("OT300%", otStart, otEnd, null, overtimeFilingClient,
								employeeScheduleUploading, true, false, isRestDay, isFromManualEditing, null,
								overtimeFilingClientList));

						resultList
								.add(this.computeOvertime("OT390%", otStart, nightDiffStart, null, overtimeFilingClient,
										employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

						resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null, overtimeFilingClient,
								otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
								isRestDay, isFromManualEditing));
					}
				}
			} else {
				if (isRegularHoliday) {
					// regular holiday not rest day
					// if may early time in - yun yung early ot time in
					// same sa early time out
					if (overtimeFilingList != null) {
						for (OvertimeFiling overtimeFiling : overtimeFilingList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFiling.getDateTimeFrom();
							Timestamp otEnd = overtimeFiling.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
									"OT100%");

							resultList.add(this.computeFirstOvertime("OT100%", otStart, otEnd, overtimeFiling, null,
									employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
									overtimeFilingList, null));

							resultList.add(this.computeOvertime("OT200%", otStart, nightDiffStart, overtimeFiling, null,
									employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
									otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
									isRestDay, isFromManualEditing));
						}
					} else {
						for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
							Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
									overtimeFilingClient, "OT100%");

							resultList.add(this.computeFirstOvertime("OT100%", otStart, otEnd, null,
									overtimeFilingClient, employeeScheduleUploading, true, false, isRestDay,
									isFromManualEditing, null, overtimeFilingClientList));

							resultList.add(
									this.computeOvertime("OT200%", otStart, nightDiffStart, null, overtimeFilingClient,
											employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
									overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
									employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
						}
					}

				} else if (isSpecialHoliday) {
					// special holiday not rest day
					// if may early time in - yun yung early ot time in?
					// same sa early time out?
					if (overtimeFilingList != null) {
						for (OvertimeFiling overtimeFiling : overtimeFilingList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFiling.getDateTimeFrom();
							Timestamp otEnd = overtimeFiling.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
									"OT30%");

							resultList.add(this.computeFirstOvertime("OT30%", otStart, otEnd, overtimeFiling, null,
									employeeScheduleUploading, true, false, isRestDay, isFromManualEditing,
									overtimeFilingList, null));

							resultList.add(this.computeOvertime("OT169%", otStart, nightDiffStart, overtimeFiling, null,
									employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
									otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
									isRestDay, isFromManualEditing));
						}
					} else {
						for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
							Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
							// ordinary day OT
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
									overtimeFilingClient, "OT30%");

							resultList.add(this.computeFirstOvertime("OT30%", otStart, otEnd, null,
									overtimeFilingClient, employeeScheduleUploading, true, false, isRestDay,
									isFromManualEditing, null, overtimeFilingClientList));

							resultList.add(
									this.computeOvertime("OT169%", otStart, nightDiffStart, null, overtimeFilingClient,
											employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

							resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
									overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
									employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
						}
					}

				} else {

					if (overtimeFilingList != null) {
						for (OvertimeFiling overtimeFiling : overtimeFilingList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFiling.getDateTimeFrom();
							Timestamp otEnd = overtimeFiling.getDateTimeTo();
							// ordinary day OT
							// this.excludeEarlyTimeIn(employeeScheduleUploading);
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, overtimeFiling, null,
									null);

							resultList.add(this.computeOvertime("OT125%", otStart, nightDiffStart, overtimeFiling, null,
									employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));
							resultList.add(this.computeNightDiff(overtimeFilingList, null, overtimeFiling, null,
									otStart, otEnd, nightDiffStart, timeTo, employeeScheduleUploading, false, true,
									isRestDay, isFromManualEditing));
						}
					} else {
						for (OvertimeFilingClient overtimeFilingClient : overtimeFilingClientList) {
							this.isNotEarlyOvertime = true;
							Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
							Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
							// ordinary day OT
							// this.excludeEarlyTimeIn(employeeScheduleUploading);
							this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null,
									overtimeFilingClient, null);

							resultList.add(
									this.computeOvertime("OT125%", otStart, nightDiffStart, null, overtimeFilingClient,
											employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));
							resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null,
									overtimeFilingClient, otStart, otEnd, nightDiffStart, timeTo,
									employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
						}
					}
				}
			}
		}

		if (isFromManualEditing) {
			String message = "No overtime type encoded: ";
			if (!this.overtimeTypeNotEncodedForManualEditList.isEmpty()) {
				for (String overtimeTypeName : this.overtimeTypeNotEncodedForManualEditList) {
					message = message.concat("\n• " + overtimeTypeName);
				}
				AlertUtil.showInformationAlert(message, this.stage);
			}
		}

		return resultList;
	}

	public EmployeeScheduleUploading computeOvertimeRegular() {
		EmployeeScheduleUploading result = new EmployeeScheduleUploading();
		return result;
	}

	public void breakdownPHLSHSSOvertime150(OvertimeFilingClient overtimeFilingClient,
			EmployeeScheduleUploading employeeScheduleUploading,
			List<EmployeeScheduleUploadingOvertimeBreakdown> resultList, Timestamp nightDiffStart, boolean isRestDay,
			boolean isFromManualEditing, Timestamp timeTo, List<OvertimeFilingClient> overtimeFilingClientList) {
		this.isNotEarlyOvertime = true;
		Timestamp otStart = overtimeFilingClient.getDateTimeFrom();
		Timestamp otEnd = overtimeFilingClient.getDateTimeTo();
		// ordinary day OT*
		this.computeEarlyOvertime(otStart, otEnd, employeeScheduleUploading, null, overtimeFilingClient, "OT150%");

		resultList.add(this.computeOvertime("OT150%", otStart, nightDiffStart, null, overtimeFilingClient,
				employeeScheduleUploading, false, false, isRestDay, isFromManualEditing));

		resultList.add(this.computeNightDiff(null, overtimeFilingClientList, null, overtimeFilingClient, otStart, otEnd,
				nightDiffStart, timeTo, employeeScheduleUploading, false, true, isRestDay, isFromManualEditing));
	}

	public void computeEarlyOvertime(Timestamp otStart, Timestamp otEnd,
			EmployeeScheduleUploading employeeScheduleUploading, OvertimeFiling overtimeFiling,
			OvertimeFilingClient overtimeFilingClient, String firstOvertimeToCompute) {

		Integer earlyOvertimeMins = 0;

		if (this.timeInSched == null && firstOvertimeToCompute != null) {
			if (this.isOvertimeConfigEncoded(firstOvertimeToCompute)) {
				EmployeeScheduleEncodingOvertime OTconfig = encodedOvertimeConfigByEmployeeList.stream()
						.filter(p -> p.getOvertimeType().getOvertimeName().equals(firstOvertimeToCompute)).findFirst()
						.get();
				this.timeInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						OTconfig.getTimeStart());
			}
		}

		if (this.totalMinutesLeftBio < 0) {
			this.totalMinutesLeftBio = 0;
		}

		if (this.timeInSched != null) {
			if (otStart.before(timeInSched)) {
				if (otEnd.before(timeInSched) || otEnd.equals(timeInSched)) {
					// early ot
					if (otStart.before(employeeScheduleUploading.getTimeInEntry())) {
						// check if otStart and timeIn is not the same 
						// or otStart before actual timeIn is invalid
						this.isWithError = true;
					}

					this.isNotEarlyOvertime = false;
					earlyOvertimeMins = this.computeTotalMinsEntry(otStart, otEnd);

					this.earlyOvertimeTimeIn = otStart;
					this.earlyOvertimeTimeOut = otEnd;
					if (overtimeFiling != null) {
						this.earlyOvertimeFiling = overtimeFiling;
					} else {
						this.earlyOvertimeFilingClient = overtimeFilingClient;
					}
				} else {
					// still have early ot but only 1 filing: early ot + ot125/ot169 + ot10
					if (otStart.before(timeInSched)) {
						earlyOvertimeMins = this.computeTotalMinsEntry(otStart, timeInSched);
						this.earlyOvertimeTimeIn = otStart;
						this.earlyOvertimeTimeOut = timeInSched;

						if (overtimeFiling != null) {
							this.earlyOvertimeFiling = overtimeFiling;
						} else {
							this.earlyOvertimeFilingClient = overtimeFilingClient;
						}
					}
				}
			} else {
				// else no early ot
			}
		}

		if (earlyOvertimeMins != 0) {
			this.totalMinutesLeftBio = this.totalMinutesLeftBio + earlyOvertimeMins;
		}
	}

	public boolean isDateEntryDoubleHoliday(EmployeeScheduleUploading employeeScheduleUploading) {
		// List<HolidayEncoding> regularHolidayList =
		// this.getHolidayEncodingMain().getAllHolidayRegular();
		List<HolidayEncoding> holidaysByClientList = this.getHolidayEncodingMain()
				.getAllHolidayByClientName(employeeScheduleUploading.getClient().getClientName());

		List<HolidayEncoding> holidaysByDateEntryList = holidaysByClientList.stream().filter(
				p -> p.getDate().equals(employeeScheduleUploading.getDateEntry()) || (p.getOverrideDate() != null
						&& p.getOverrideDate().equals(employeeScheduleUploading.getDateEntry())))
				.collect(Collectors.toList());

		if (holidaysByDateEntryList.size() > 1) {
			return true;
		}

		return false;
	}

	public List<EmployeeScheduleUploadingOvertimeBreakdown> breakdownOvertimeFromEdit(
			EmployeeScheduleUploading employeeScheduleUploading, List<OvertimeFiling> overtimeFilingList,
			List<OvertimeFilingClient> overtimeFilingClientList, Stage stage, User userEditing) {

		this.initializeObjects();
		this.stage = stage;
		this.userEditing = userEditing;
		this.isWithError = false;

		List<EmployeeScheduleUploadingOvertimeBreakdown> resultList = new ArrayList<>();

		Boolean isRestDay = employeeScheduleUploading.getIsRestDay() != null
				? employeeScheduleUploading.getIsRestDay() == 1 ? true : false
				: false;
		Boolean isRegularHoliday = employeeScheduleUploading.getIsHoliday() != null
				? employeeScheduleUploading.getIsHoliday() == 1 ? true : false
				: false;
		Boolean isSpecialHoliday = employeeScheduleUploading.getIsHoliday() != null
				? employeeScheduleUploading.getIsHoliday() == 2 ? true : false
				: false;
		Boolean isRegularSchedule = employeeScheduleUploading.getIsRegularSchedule() == null ? false
				: employeeScheduleUploading.getIsRegularSchedule() == 1 ? true : false;
		Boolean isDoubleHoliday = this.isDateEntryDoubleHoliday(employeeScheduleUploading);

		this.checkIfOvertimeStillExists(employeeScheduleUploading);

		this.fetchOvertimeConfig(employeeScheduleUploading, isRegularSchedule);

		Timestamp timeTo = this.computeEntryTotalMins(overtimeFilingList, overtimeFilingClientList,
				employeeScheduleUploading, isRegularSchedule, isRegularHoliday, isSpecialHoliday, isRestDay);

		resultList = this.startBreakdownOvertime(isRestDay, isRegularHoliday, isSpecialHoliday, isDoubleHoliday,
				overtimeFilingList, overtimeFilingClientList, employeeScheduleUploading, timeTo, true);

		List<EmployeeScheduleUploadingOvertimeBreakdown> nullResultsList = resultList.stream()
				.filter(p -> p == null || p.getOvertimeType() == null || p.getTotalMin() == null)
				.collect(Collectors.toList());

		resultList.removeAll(nullResultsList);

		return resultList;
	}

	public boolean isEntryHalfDay(Timestamp timeFrom, Timestamp timeTo, Timestamp timeInSched, Timestamp lunchOutSched,
			Timestamp lunchInSched, Timestamp timeOutSched) {

		return true;
	}

	public Timestamp[] computeInitialTotalMinsLeft(boolean isRegularSchedule, boolean isRegularHoliday,
			boolean isSpecialHoliday, boolean isRestDay, EmployeeScheduleUploading employeeScheduleUploading,
			Timestamp timeFrom, Timestamp timeTo, Timestamp timeToEntry, List<OvertimeFiling> overtimeFilingList,
			List<OvertimeFilingClient> overtimeFilingClientList, Integer totalMinsOTFilingGap) {
		Integer totalMinsEntry = 0;
		Integer totalMinsWorkDay = 0;

		Timestamp[] result = new Timestamp[2];
		result[0] = new Timestamp(new Date().getTime());
		result[1] = new Timestamp(new Date().getTime());
		String timeFormat = "HH:mm:00";

		if (isRegularSchedule) {
			if (employeeScheduleUploading.getScheduleEncoding() != null) {
				Timestamp timeInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						Time.valueOf(new SimpleDateFormat(timeFormat)
								.format(employeeScheduleUploading.getScheduleEncoding().getTimeIn())));
				Timestamp timeOutSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						Time.valueOf(new SimpleDateFormat(timeFormat)
								.format(employeeScheduleUploading.getScheduleEncoding().getTimeOut())));

				// time work is more than 8 hrs -- with early ot & after work ot
				if (timeFrom.before(timeInSched) && timeTo.after(timeInSched)) {
					totalMinsWorkDay = this.computeTotalMinsEntry(timeInSched, timeOutSched);
				} else {
					// pang check if half day
					if (timeFrom.after(timeInSched) && timeTo.after(timeInSched)) {
						totalMinsWorkDay = this.computeTotalMinsEntry(timeFrom, timeOutSched);
					} else {
						totalMinsWorkDay = 540;
					}
				}

				// early ot
				if (timeFrom.before(timeInSched) && timeToEntry.before(timeInSched)) {

					if (overtimeFilingList != null && !overtimeFilingList.isEmpty()) {
						timeFrom = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								this.getTimeFromTimestamp(overtimeFilingList.get(0).getDateTimeFrom()));
					} else {
						if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {
							timeFrom = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
									this.getTimeFromTimestamp(overtimeFilingClientList.get(0).getDateTimeFrom()));
						}
					}
					timeTo = timeInSched;
				}

				if (timeToEntry.before(timeTo)) {
					timeTo = timeToEntry;
				}

				if (overtimeFilingList != null && !overtimeFilingList.isEmpty()) {
					if (employeeScheduleUploading.getTimeInEntry().after(overtimeFilingList.get(0).getDateTimeFrom())) {
						timeFrom = employeeScheduleUploading.getTimeInEntry();
					}
				} else {
					if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {
						if (employeeScheduleUploading.getTimeInEntry()
								.after(overtimeFilingClientList.get(0).getDateTimeFrom())) {
							timeFrom = employeeScheduleUploading.getTimeInEntry();
						}
					}
				}
			}
		} else if (!isRegularSchedule) {
			Timestamp timeInSched = new Timestamp(new Date().getTime());
			Timestamp timeOutSched = new Timestamp(new Date().getTime());

			if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
				timeInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						Time.valueOf(new SimpleDateFormat(timeFormat)
								.format(employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeIn())));
				timeOutSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						Time.valueOf(new SimpleDateFormat(timeFormat).format(
								employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeOut())));

				// to confirm how much time work is consumed, save total mins work day

				// time work is more than 8 hrs -- with early ot & after work ot
				if (timeFrom.before(timeInSched) && timeTo.after(timeInSched)) {
					totalMinsWorkDay = this.computeTotalMinsEntry(timeInSched, timeOutSched);
				} else {
					// pang check if half day
					if (timeFrom.after(timeInSched) && timeTo.after(timeInSched)) {
						totalMinsWorkDay = this.computeTotalMinsEntry(timeFrom, timeOutSched);
					} else {
						totalMinsWorkDay = 540;
					}
				}

				// to confirm which parameters to use
				// to compute timeFrom - timeTo total mins of overtime

				// only early ot is filed
				if (timeFrom.before(timeInSched) && timeToEntry.before(timeInSched)) {
					if (overtimeFilingList != null) {
						timeFrom = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								this.getTimeFromTimestamp(overtimeFilingList.get(0).getDateTimeFrom()));
					} else {
						timeFrom = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								this.getTimeFromTimestamp(overtimeFilingClientList.get(0).getDateTimeFrom()));
					}

					timeTo = timeInSched;
				}

				// biometrics entry time out is before overtime filing time end
				// end computation until biometrics entry time out
				// instead of allowed overtime
				if (timeToEntry.before(timeTo)) {
					timeTo = timeToEntry;
				}

				// biometrics entry time in is after the first overtime filing time start
				// start computation from biometrics time in until allowed overtime/entry time
				// out
				if (overtimeFilingList != null && !overtimeFilingList.isEmpty()) {
					if (employeeScheduleUploading.getTimeInEntry().after(overtimeFilingList.get(0).getDateTimeFrom())) {
						timeFrom = employeeScheduleUploading.getTimeInEntry();
					}
				} else {
					if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()) {
						if (employeeScheduleUploading.getTimeInEntry()
								.after(overtimeFilingClientList.get(0).getDateTimeFrom())) {
							timeFrom = employeeScheduleUploading.getTimeInEntry();
						}
					}
				}
			}
		} else {
			// biometrics entry time out is before overtime filing time end
			// end computation until biometrics entry time out
			// instead of allowed overtime
			if (timeToEntry.before(timeTo)) {
				timeTo = timeToEntry;
			}
		}

		totalMinsEntry = this.computeTotalMinsEntry(timeFrom, timeTo);
		totalMinsEntry = totalMinsEntry - totalMinsOTFilingGap;

		if (totalMinsWorkDay >= 540 && totalMinsEntry >= 540) {
			if ((overtimeFilingClientList != null && overtimeFilingClientList.size() > 1)
					|| (overtimeFilingList != null && overtimeFilingList.size() > 1)) {
				totalMinsEntry = totalMinsEntry - 480;
			} else {
				if (!isRestDay && !isRegularHoliday) {
					totalMinsEntry = totalMinsEntry - 480;
				}
			}
		} else {
			if (totalMinsWorkDay < 540 && !isRestDay && !isRegularHoliday && !isSpecialHoliday) {
				if (totalMinsWorkDay == 300) {
					// half day -- 8am - 12pm / 12pm-5pm / 10am - 5pm
					totalMinsEntry = totalMinsEntry - 240;
				}
				// half day -- 10am - 5pm
				else {
					// half day but with early OT OR/AND additional OT with/without gap
					// exclude lunch mins
					totalMinsWorkDay = totalMinsWorkDay - 60;
					totalMinsEntry = totalMinsEntry - totalMinsWorkDay;
				}
			}
		}

		this.totalMinutesLeftBio = totalMinsEntry;

		result[0] = timeFrom;
		result[1] = timeTo;

		return result;
	}

	public boolean isEntryHalfDay(Timestamp lunchInSched, Timestamp lunchOutSched, Timestamp timeFrom,
			Timestamp timeTo) {

		if (timeFrom.after(lunchOutSched) && (timeFrom.after(lunchInSched) || timeFrom.equals(lunchInSched))) {
			return true;
		}

		if (timeTo.before(lunchOutSched) || timeTo.equals(lunchOutSched) && timeTo.before(lunchInSched)) {
			return true;
		}

		return false;
	}

	public void computeLunch(boolean isRegularSchedule, EmployeeScheduleUploading employeeScheduleUploading,
			Timestamp timeFrom, Timestamp timeTo, boolean isRestDay) {
		if (this.totalMinutesLeftBio >= 480) {
			// work mins + lunch mins [480 + 60 = 540]
			Integer lunchMinutes = 60;
			this.totalMinutesLeftBio = this.totalMinutesLeftBio - lunchMinutes;
		} else {
			// double check bio entry if it overlaps lunch sched
			if (isRegularSchedule) {
				if (employeeScheduleUploading.getScheduleEncoding() != null) {
					if (employeeScheduleUploading.getScheduleEncoding().getIsBreakExempted() == 1) {
						Integer lunchMinutes = 60;
						this.totalMinutesLeftBio = this.totalMinutesLeftBio - lunchMinutes;
					} else {
						Timestamp lunchInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								employeeScheduleUploading.getScheduleEncoding().getLunchIn());
						Timestamp lunchOutSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								employeeScheduleUploading.getScheduleEncoding().getLunchOut());

						this.startComputeLunch(timeFrom, timeTo, lunchInSched, lunchOutSched);
					}
				}
			} else {
				if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
					if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getIsBreakExempted() == 1) {
						Integer lunchMinutes = 60;
						this.totalMinutesLeftBio = this.totalMinutesLeftBio - lunchMinutes;
					} else {
						Timestamp lunchInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getLunchIn());
						Timestamp lunchOutSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
								employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getLunchOut());

						this.startComputeLunch(timeFrom, timeTo, lunchInSched, lunchOutSched);
					}
				}
			}
		}
	}

	public void startComputeLunch(Timestamp timeFrom, Timestamp timeTo, Timestamp lunchInSched,
			Timestamp lunchOutSched) {
		if (timeFrom.before(lunchInSched) && timeTo.after(lunchInSched) && timeTo.after(lunchOutSched)) {
			Integer lunchMinutes = 60;
			this.totalMinutesLeftBio = this.totalMinutesLeftBio - lunchMinutes;
		}

		if (timeFrom.before(lunchInSched) && timeTo.after(lunchInSched) && timeTo.before(lunchOutSched)) {
			Integer lunchMinutesConsumed = this.computeTotalMinsEntry(lunchInSched, timeTo);
			this.totalMinutesLeftBio = this.totalMinutesLeftBio - lunchMinutesConsumed;
		}

		if (timeFrom.after(lunchInSched) && timeTo.after(lunchInSched) && timeTo.after(lunchOutSched)) {
			Integer lunchMinutesConsumed = this.computeTotalMinsEntry(timeFrom, lunchOutSched);
			this.totalMinutesLeftBio = this.totalMinutesLeftBio - lunchMinutesConsumed;
		}
	}

	// TODO
	public EmployeeScheduleUploadingOvertimeBreakdown computeFirstOvertime(String overtimeName, Timestamp otStart,
			Timestamp otEnd, OvertimeFiling overtimeFiling, OvertimeFilingClient overtimeFilingClient,
			EmployeeScheduleUploading employeeScheduleUploading, Boolean isFirstBreakdown, Boolean isLastBreakdown,
			Boolean isRestDay, boolean isFromManualEditing, List<OvertimeFiling> overtimeFilingList,
			List<OvertimeFilingClient> overtimeFilingClientList) {
		EmployeeScheduleUploadingOvertimeBreakdown result = new EmployeeScheduleUploadingOvertimeBreakdown();
		if (this.isOvertimeConfigEncoded(overtimeName)) {
			EmployeeScheduleEncodingOvertime OTconfig = encodedOvertimeConfigByEmployeeList.stream()
					.filter(p -> p.getOvertimeType().getOvertimeName().equals(overtimeName)).findFirst().get();

			if ((overtimeFilingList != null && overtimeFilingList.size() == 1)
					|| (overtimeFilingClientList != null && overtimeFilingClientList.size() == 1)
							&& this.isNotEarlyOvertime) {
				result = this.computeTotalMinsOvertime(OTconfig, overtimeFiling, overtimeFilingClient,
						employeeScheduleUploading, 480, isFirstBreakdown, isLastBreakdown, isRestDay,
						isFromManualEditing);
			} else {
				Timestamp otConfigStart = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						OTconfig.getTimeStart());
				Timestamp otConfigEnd = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						OTconfig.getTimeEnd());

				if ((otStart.equals(otConfigStart) || otStart.after(otConfigStart))
						&& (otEnd.before(otConfigEnd) || otEnd.equals(otConfigEnd))) {
					result = this.computeTotalMinsOvertime(OTconfig, overtimeFiling, overtimeFilingClient,
							employeeScheduleUploading, 480, isFirstBreakdown, isLastBreakdown, isRestDay,
							isFromManualEditing);
				}
			}

			this.overtimeTypeNotEncodedForManualEditList.remove(overtimeName);

		} else {
			if (isFromManualEditing) {
				this.overtimeTypeNotEncodedForManualEditList.add(overtimeName);
				return result;
			}

			// add list report
			// not from manual editing
			this.overtimeTypeConfigNotEncodedHashSet.add(overtimeName);
		}
		return result;
	}

	public EmployeeScheduleUploadingOvertimeBreakdown computeOvertime(String overtimeName, Timestamp otStart,
			Timestamp nightDiffStart, OvertimeFiling overtimeFiling, OvertimeFilingClient overtimeFilingClient,
			EmployeeScheduleUploading employeeScheduleUploading, Boolean isFirstBreakdown, Boolean isLastBreakdown,
			Boolean isRestDay, boolean isFromManualEditing) {
		EmployeeScheduleUploadingOvertimeBreakdown result = new EmployeeScheduleUploadingOvertimeBreakdown();

		if (this.isOvertimeConfigEncoded(overtimeName)) {

			EmployeeScheduleEncodingOvertime OTconfig = this.encodedOvertimeConfigByEmployeeList.stream()
					.filter(p -> p.getOvertimeType().getOvertimeName().equals(overtimeName)).findFirst().get();

			// all ot filing only before ot10/night diff
			if (otStart.before(nightDiffStart)) {
				// normal ot125 / early ot as ot125 (ot169, etc...)
				if (this.otFilingListSize == 1 || this.otFilingClientListSize == 1) {
					result = this.computeTotalMinsOvertime(OTconfig, overtimeFiling, overtimeFilingClient,
							employeeScheduleUploading, OTconfig.getTotalMin(), isFirstBreakdown, isLastBreakdown,
							isRestDay, isFromManualEditing);
				} else {
					// more than 1 ot filing, check if ot filing is with ot125/ot169/etc and
					// (not early ot)

					// no ot125, ot filing: (early ot + ot10) OR (ot125 + ot10)
					// otfilinggap: ot125 >= 300mins : from 17:00 - 22:00 : 5pm - 10pm[onwards]
					if (this.totalMinsOTFilingGap >= 300) {
						this.earlyOvertimeFiling = null;
						this.earlyOvertimeFilingClient = null;
						result = this.computeTotalMinsOvertime(OTconfig, overtimeFiling, overtimeFilingClient,
								employeeScheduleUploading, OTconfig.getTotalMin(), isFirstBreakdown, isLastBreakdown,
								isRestDay, isFromManualEditing);
					} else {
						// with ot125, ot filing: (early ot + ot125 + ot10)

						// first loop, if this ot is early ot, return null and continue to next loop
						if (!this.isNotEarlyOvertime) {
							this.isNotEarlyOvertime = true;
							return null;
						} else {
							// second loop, save all: (early ot + ot125 + ot10)
							result = this.computeTotalMinsOvertime(OTconfig, overtimeFiling, overtimeFilingClient,
									employeeScheduleUploading, OTconfig.getTotalMin(), isFirstBreakdown,
									isLastBreakdown, isRestDay, isFromManualEditing);
						}
					}
				}
			}

			this.overtimeTypeNotEncodedForManualEditList.remove(overtimeName);

		} else {
			if (isFromManualEditing) {
				this.overtimeTypeNotEncodedForManualEditList.add(overtimeName);
				return result;
			}
			this.overtimeTypeConfigNotEncodedHashSet.add(overtimeName);
		}
		return result;
	}

	public boolean isOvertimeConfigEncoded(String overtimeName) {
		return this.encodedOvertimeConfigByEmployeeList.stream()
				.filter(p -> p.getOvertimeType().getOvertimeName().equals(overtimeName)).findFirst().isPresent();
	}

	public void fetchOvertimeConfig(EmployeeScheduleUploading employeeScheduleUploading, Boolean isRegularSchedule) {
		this.timeInSched = null;
		this.encodedOvertimeConfigByEmployeeList.clear();

		if (isRegularSchedule) {
			if (employeeScheduleUploading.getScheduleEncoding() != null) {
				this.timeInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						Time.valueOf(new SimpleDateFormat("HH:mm:ss")
								.format(employeeScheduleUploading.getScheduleEncoding().getTimeIn())));
			}
			// reg sched ot config
			this.encodedOvertimeConfigByEmployeeList
					.addAll(this.getEmployeeScheduleEncodingOvertimeMain().getAllOvertimeRegularByEmployeeId(
							employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode()));
		} else {

			if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
				this.timeInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
						employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeIn());
			} else {
				if (employeeScheduleUploading.getScheduleEncoding() != null) {
					this.timeInSched = this.entryToTimestamp(employeeScheduleUploading.getDateEntry(),
							Time.valueOf(new SimpleDateFormat("HH:mm:ss")
									.format(employeeScheduleUploading.getScheduleEncoding().getTimeIn())));
				} else {
					// ot types and sched nila as this.timeInSched
				}
			}

			// irreg sched ot config
			this.encodedOvertimeConfigByEmployeeList.addAll(this.getEmployeeScheduleEncodingOvertimeMain()
					.getAllOvertimeIrregularByEmployeeIdAndPrikeyIrregularSchedule(
							employeeScheduleUploading.getEmployeeScheduleEncodingIrregular(),
							employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode()));

			if (this.encodedOvertimeConfigByEmployeeList.isEmpty()
					|| this.encodedOvertimeConfigByEmployeeList.size() == 0) {
				// empty irreg sched ot config
				this.encodedOvertimeConfigByEmployeeList
						.addAll(this.getEmployeeScheduleEncodingOvertimeMain().getAllOvertimeRegularByEmployeeId(
								employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode()));
			} else {
				// with irreg sched ot config,
				// check if all ot type has equivalent irreg ot sched,
				// dont get reg ot config with same irreg ot config
				// else get all reg ot sched to irreg sched ot config
				List<EmployeeScheduleEncodingOvertime> regOvertimeConfigList = new ArrayList<>();

				regOvertimeConfigList
						.addAll(this.getEmployeeScheduleEncodingOvertimeMain().getAllOvertimeRegularByEmployeeId(
								employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode()));

				if (!regOvertimeConfigList.isEmpty()) {

					for (EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime : this.encodedOvertimeConfigByEmployeeList) {
						boolean isInRegularOTConfig = regOvertimeConfigList.stream()
								.filter(p -> p.getOvertimeType().getPrimaryKey()
										.equals(employeeScheduleEncodingOvertime.getOvertimeType().getPrimaryKey()))
								.findFirst().isPresent();

						if (isInRegularOTConfig) {
							EmployeeScheduleEncodingOvertime regularOTConfig = regOvertimeConfigList.stream()
									.filter(p -> p.getOvertimeType().getPrimaryKey()
											.equals(employeeScheduleEncodingOvertime.getOvertimeType().getPrimaryKey()))
									.findFirst().get();

							regOvertimeConfigList.remove(regularOTConfig);
							regOvertimeConfigList.add(employeeScheduleEncodingOvertime);
						}
					}
					this.encodedOvertimeConfigByEmployeeList.clear();
					this.encodedOvertimeConfigByEmployeeList.addAll(regOvertimeConfigList);
				}
			}
		}

		if (encodedOvertimeConfigByEmployeeList.isEmpty() && encodedOvertimeConfigByEmployeeList.size() == 0) {
			// generate report
			// add this employee to a list then generate a report that this emp dont have ot
			// config both reg and irreg sched
			this.overtimeTypeConfigNotEncodedHashSet.add("All overtime type");
		}
	}

	public EmployeeScheduleUploadingOvertimeBreakdown computeNightDiff(List<OvertimeFiling> overtimeFilingList,
			List<OvertimeFilingClient> overtimeFilingClientList, OvertimeFiling overtimeFiling,
			OvertimeFilingClient overtimeFilingClient, Timestamp otStart, Timestamp otEnd, Timestamp nightDiffStart,
			Timestamp timeTo, EmployeeScheduleUploading employeeScheduleUploading, Boolean isFirstBreakdown,
			Boolean isLastBreakdown, Boolean isRestDay, boolean isFromManualEditing) {

		EmployeeScheduleUploadingOvertimeBreakdown result = new EmployeeScheduleUploadingOvertimeBreakdown();
		EmployeeScheduleEncodingOvertime OT10config = new EmployeeScheduleEncodingOvertime();

		if (this.isOvertimeConfigEncoded("OT10%")) {
			OT10config = this.encodedOvertimeConfigByEmployeeList.stream()
					.filter(p -> p.getOvertimeType().getOvertimeName().equals("OT10%")).findFirst().get();

			if ((overtimeFilingList != null && overtimeFilingList.size() > 1)
					|| (overtimeFilingClientList != null && overtimeFilingClientList.size() > 1)) {
				if ((otStart.equals(nightDiffStart) || otStart.after(nightDiffStart)) && otEnd.after(nightDiffStart)) {
					this.totalMinutesLeftBio = 0;
					this.totalMinutesLeftBio = this.computeTotalMinsEntry(otStart, timeTo);

					result = this.computeTotalMinsOvertime(OT10config, overtimeFiling, overtimeFilingClient,
							employeeScheduleUploading, OT10config.getTotalMin(), isFirstBreakdown, isLastBreakdown,
							isRestDay, isFromManualEditing);
				} else if (otStart.before(nightDiffStart) && otEnd.after(nightDiffStart)) {
					this.totalMinutesLeftBio = 0;
					this.totalMinutesLeftBio = this.computeTotalMinsEntry(nightDiffStart, timeTo);

					result = this.computeTotalMinsOvertime(OT10config, overtimeFiling, overtimeFilingClient,
							employeeScheduleUploading, OT10config.getTotalMin(), isFirstBreakdown, isLastBreakdown,
							isRestDay, isFromManualEditing);
				}
			} else {
				this.totalMinutesLeftBio = 0;
				if (otStart.equals(nightDiffStart) || otStart.after(nightDiffStart)) {
					this.totalMinutesLeftBio = this.computeTotalMinsEntry(otStart, timeTo);

					result = this.computeTotalMinsOvertime(OT10config, overtimeFiling, overtimeFilingClient,
							employeeScheduleUploading, OT10config.getTotalMin(), isFirstBreakdown, true, isRestDay,
							isFromManualEditing);
				} else {
					this.totalMinutesLeftBio = this.computeTotalMinsEntry(nightDiffStart, timeTo);

					result = this.computeTotalMinsOvertime(OT10config, overtimeFiling, overtimeFilingClient,
							employeeScheduleUploading, OT10config.getTotalMin(), isFirstBreakdown, true, isRestDay,
							isFromManualEditing);
				}
			}

			this.overtimeTypeNotEncodedForManualEditList.remove("OT10%");

		} else {
			if (isFromManualEditing) {
				this.overtimeTypeNotEncodedForManualEditList.add("OT10%");
				return result;
			}
			this.overtimeTypeConfigNotEncodedHashSet.add("OT10%");
		}

		return result;
	}

	public void excludeEarlyTimeIn(EmployeeScheduleUploading employeeScheduleUploading) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:00");

		if (this.timeInSched != null) {
			if (employeeScheduleUploading.getTimeInEntry().before(timeInSched)) {

				String earlyTimeInEntryTime = timeFormat
						.format(this.getTimeFromTimestamp(employeeScheduleUploading.getTimeInEntry()));

				Timestamp earlyTimeFrom = this.entryToTimestamp(
						DateFormatter
								.toDate(employeeScheduleUploading.getTimeInEntry().toLocalDateTime().toLocalDate()),
						Time.valueOf(earlyTimeInEntryTime));

				Integer earlyTimeIn = this.computeTotalMinsEntry(earlyTimeFrom, timeInSched);

				this.totalMinutesLeftBio = this.totalMinutesLeftBio - earlyTimeIn;
			}
		}
	}

	public EmployeeScheduleUploadingOvertimeBreakdown computeTotalMinsOvertime(
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime, OvertimeFiling overtimeFiling,
			OvertimeFilingClient overtimeFilingClient, EmployeeScheduleUploading employeeScheduleUploading,
			Integer overtimeDurationInMin, Boolean isFirstBreakdown, Boolean isLastBreakdown, Boolean isRestDay,
			boolean isFromManualEditing) {

		EmployeeScheduleUploadingOvertimeBreakdown result = new EmployeeScheduleUploadingOvertimeBreakdown();

		boolean isWithRulesApplied = true;

		if (overtimeFilingClient != null) {
			if (this.clientPrikeyBreakdownOT30OT169List
					.contains(overtimeFilingClient.getClient().getCompany().getCompanyCode())) {
				isWithRulesApplied = false;
			}
		}

		Integer initialTotalMinsLeftThisOvertime = 0;
		Integer difference = 0;
		Integer totalMinsThisOvertime = 0;

		if (!isLastBreakdown) {
			initialTotalMinsLeftThisOvertime = this.totalMinutesLeftBio;
			this.totalMinutesLeftBio = this.totalMinutesLeftBio - overtimeDurationInMin;
			difference = initialTotalMinsLeftThisOvertime - this.totalMinutesLeftBio;
		}

		if (isFirstBreakdown) {
			if (this.totalMinutesLeftBio > 0) {
				totalMinsThisOvertime = overtimeDurationInMin;
			} else {
				totalMinsThisOvertime = initialTotalMinsLeftThisOvertime;
			}
		}

		if (!isFirstBreakdown && !isLastBreakdown) {
			if (isWithRulesApplied) {
				totalMinsThisOvertime = this.defaultOvertimeRules(difference, initialTotalMinsLeftThisOvertime,
						totalMinsThisOvertime, isRestDay);

			} else {
				// different set of rules of overtime for each clients
				totalMinsThisOvertime = this.clientOvertimeRules(employeeScheduleUploading.getClient(),
						initialTotalMinsLeftThisOvertime, totalMinsThisOvertime, isRestDay);
			}
		}

		if (isLastBreakdown) {
			if (this.totalMinutesLeftBio > 0 || !this.isNotEarlyOvertime) {
				totalMinsThisOvertime = this.totalMinutesLeftBio;
			}
		}

		if (totalMinsThisOvertime > 0) {
			result = this.saveOvertimeBreakdown(employeeScheduleEncodingOvertime, employeeScheduleUploading,
					overtimeFiling, overtimeFilingClient, totalMinsThisOvertime, isFromManualEditing);

		}
		return result;
	}

	public Integer defaultOvertimeRules(Integer difference, Integer initialTotalMinsLeftThisOvertime,
			Integer totalMinsThisOvertime, boolean isRestDay) {
		// default rules: ot125 > 180 mins = -30mins
		// additional rule: -15mins
		// total: -45 mins

		if (difference >= 180) {
			if (initialTotalMinsLeftThisOvertime > 180) {
				initialTotalMinsLeftThisOvertime = initialTotalMinsLeftThisOvertime - 45;
				totalMinsThisOvertime = initialTotalMinsLeftThisOvertime;
			} else {
				if (initialTotalMinsLeftThisOvertime > 15 && !isRestDay) {
					// less than 3hrs
					if (this.isNotEarlyOvertime) {
						// not early ot
						initialTotalMinsLeftThisOvertime = initialTotalMinsLeftThisOvertime - 15;
					}
					// early ot
					this.totalMinsEarlyOvertime = 0;
				}
				totalMinsThisOvertime = initialTotalMinsLeftThisOvertime;
			}
		}
		return totalMinsThisOvertime;
	}

	public Integer clientOvertimeRules(Client client, Integer initialTotalMinsLeftThisOvertime,
			Integer totalMinsThisOvertime, boolean isRestDay) {
		// rules for ot125 - ot10 by client
		switch (client.getCompany().getCompanyCode()) {
		case 323: // phlshs
			return this.phlshssOvertimeRules(isRestDay, initialTotalMinsLeftThisOvertime, totalMinsThisOvertime);
		default:
			return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime;
		}
	}

	public Integer phlshssOvertimeRules(boolean isRestDay, Integer initialTotalMinsLeftThisOvertime,
			Integer totalMinsThisOvertime) {
		Integer breakfastLunchSupper = 60;

		if (isRestDay) {
			// more than 8hrs & excess ot & night diff
			if (initialTotalMinsLeftThisOvertime > 720) {
				return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime - (breakfastLunchSupper * 2);
			}

			// more than 8hrs and 1hr excess
			if (initialTotalMinsLeftThisOvertime > 540) {
				return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime - breakfastLunchSupper;
			}

			// more than 8hrs but less than excess
			if (initialTotalMinsLeftThisOvertime > 480 && initialTotalMinsLeftThisOvertime < 540) {
				Integer excess = initialTotalMinsLeftThisOvertime - 480;
				return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime - excess;
			}

		} else {
			// more than excess ot & night diff & additional 3hrs before early ot
			if (initialTotalMinsLeftThisOvertime > 660) {
				return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime - (breakfastLunchSupper * 3);
			}

			// more than excess ot & night diff
			if (initialTotalMinsLeftThisOvertime > 420) {
				return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime - (breakfastLunchSupper * 2);
			}

			// more than excess ot
			if (initialTotalMinsLeftThisOvertime > 180) {
				return totalMinsThisOvertime = initialTotalMinsLeftThisOvertime - breakfastLunchSupper;
			}
		}

		return initialTotalMinsLeftThisOvertime;
	}

	public EmployeeScheduleUploadingOvertimeBreakdown saveOvertimeBreakdown(
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime,
			EmployeeScheduleUploading employeeScheduleUploading, OvertimeFiling overtimeFiling,
			OvertimeFilingClient overtimeFilingClient, Integer totalMinsThisOvertime, boolean isFromManualEditing) {

		boolean isAlreadyExist = this.employeeScheduleUploadingOvertimeBreakdownList.stream().filter(
				p -> p.getEmployeeScheduleUploading().getPrimaryKey().equals(employeeScheduleUploading.getPrimaryKey())
						&& p.getOvertimeType().getPrimaryKey()
								.equals(employeeScheduleEncodingOvertime.getOvertimeType().getPrimaryKey()))
				.findFirst().isPresent();

		if (this.isWithError) {
			RemarksReference errorRemark = new RemarksReference();
			errorRemark = this.getRemarksReferenceMain().getDataById(3);
			EmployeeScheduleUploading existingUploading = this.getEmployeeScheduleUploadingMain()
					.getDataById(employeeScheduleUploading.getPrimaryKey());
			existingUploading.setRemarksReference(errorRemark);

			this.getEmployeeScheduleUploadingMain().updateMainObject(existingUploading);
			return null;
		}

		if (isAlreadyExist) {
			EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown = this.employeeScheduleUploadingOvertimeBreakdownList
					.stream()
					.filter(p -> p.getEmployeeScheduleUploading().getPrimaryKey()
							.equals(employeeScheduleUploading.getPrimaryKey())
							&& p.getOvertimeType().getPrimaryKey()
									.equals(employeeScheduleEncodingOvertime.getOvertimeType().getPrimaryKey()))
					.findFirst().get();

			this.employeeScheduleUploadingOvertimeBreakdownList.remove(employeeScheduleUploadingOvertimeBreakdown);

			return this.updateObject(employeeScheduleUploadingOvertimeBreakdown, employeeScheduleUploading,
					employeeScheduleEncodingOvertime, overtimeFiling, overtimeFilingClient, totalMinsThisOvertime,
					isFromManualEditing, employeeScheduleEncodingOvertime.getOvertimeType());
		} else {
			return this.createObject(employeeScheduleUploading, employeeScheduleEncodingOvertime, overtimeFiling,
					overtimeFilingClient, totalMinsThisOvertime, employeeScheduleEncodingOvertime.getOvertimeType(),
					isFromManualEditing);
		}

	}

	public Time timestampToTime(Timestamp timestamp) {

		LocalTime localTime = timestamp.toLocalDateTime().toLocalTime();
		Time time = Time.valueOf(localTime);

		return time;
	}

	public OvertimeTypeMain getOvertimeTypeMain() {
		return overtimeTypeMain;
	}

	public void setOvertimeTypeMain(OvertimeTypeMain overtimeTypeMain) {
		this.overtimeTypeMain = overtimeTypeMain;
	}

	public EmployeeScheduleUploadingMain getEmployeeScheduleUploadingMain() {
		return employeeScheduleUploadingMain;
	}

	public void setEmployeeScheduleUploadingMain(EmployeeScheduleUploadingMain employeeScheduleUploadingMain) {
		this.employeeScheduleUploadingMain = employeeScheduleUploadingMain;
	}

	public UserMain getUserMain() {
		return userMain;
	}

	public void setUserMain(UserMain userMain) {
		this.userMain = userMain;
	}

	public Integer getTotalMinutesLeftBio() {
		return totalMinutesLeftBio;
	}

	public void setTotalMinutesLeftBio(Integer totalMinutesLeftBio) {
		this.totalMinutesLeftBio = totalMinutesLeftBio;
	}

	public EmployeeScheduleEncodingOvertimeMain getEmployeeScheduleEncodingOvertimeMain() {
		return employeeScheduleEncodingOvertimeMain;
	}

	public void setEmployeeScheduleEncodingOvertimeMain(
			EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain) {
		this.employeeScheduleEncodingOvertimeMain = employeeScheduleEncodingOvertimeMain;
	}

	public OvertimeFilingMain getOvertimeFilingMain() {
		return overtimeFilingMain;
	}

	public void setOvertimeFilingMain(OvertimeFilingMain overtimeFilingMain) {
		this.overtimeFilingMain = overtimeFilingMain;
	}

	public HashMap<Integer, HashSet<String>> getEmployeesWithoutOvertimeConfigHashMap() {
		return employeesWithoutOvertimeConfigHashMap;
	}

	public void setEmployeesWithoutOvertimeConfigHashMap(
			HashMap<Integer, HashSet<String>> employeesWithoutOvertimeConfigHashMap) {
		this.employeesWithoutOvertimeConfigHashMap = employeesWithoutOvertimeConfigHashMap;
	}

	public HolidayEncodingMain getHolidayEncodingMain() {
		return holidayEncodingMain;
	}

	public void setHolidayEncodingMain(HolidayEncodingMain holidayEncodingMain) {
		this.holidayEncodingMain = holidayEncodingMain;
	}

	public RemarksReferenceMain getRemarksReferenceMain() {
		return remarksReferenceMain;
	}

	public void setRemarksReferenceMain(RemarksReferenceMain remarksReferenceMain) {
		this.remarksReferenceMain = remarksReferenceMain;
	}

	public List<Integer> getClientPrikeyBreakdownOT30OT169List() {
		return clientPrikeyBreakdownOT30OT169List;
	}

	public void setClientPrikeyBreakdownOT30OT169List(List<Integer> clientPrikeyBreakdownOT30OT169List) {
		this.clientPrikeyBreakdownOT30OT169List = clientPrikeyBreakdownOT30OT169List;
	}

	public List<Integer> getClientPrikeyRulesBreakdownNotApplicableList() {
		return clientPrikeyRulesBreakdownNotApplicableList;
	}

	public void setClientPrikeyRulesBreakdownNotApplicableList(
			List<Integer> clientPrikeyRulesBreakdownNotApplicableList) {
		this.clientPrikeyRulesBreakdownNotApplicableList = clientPrikeyRulesBreakdownNotApplicableList;
	}

	public OvertimeFilingClientMain getOvertimeFilingClientMain() {
		return overtimeFilingClientMain;
	}

	public void setOvertimeFilingClientMain(OvertimeFilingClientMain overtimeFilingClientMain) {
		this.overtimeFilingClientMain = overtimeFilingClientMain;
	}

	public User getUserEditing() {
		return userEditing;
	}

	public void setUserEditing(User userEditing) {
		this.userEditing = userEditing;
	}

}

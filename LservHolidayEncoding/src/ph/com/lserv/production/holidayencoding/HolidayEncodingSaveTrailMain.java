package ph.com.lserv.production.holidayencoding;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.recordprocedure.RecordProcedureMain;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;
import ph.com.lserv.production.holidayencoding.view.EditHolidayEncodingController;
import ph.com.lserv.production.holidayencodingtrail.HolidayEncodingTrailMain;
import ph.com.lserv.production.holidayencodingtrail.model.HolidayEncodingTrail;

public class HolidayEncodingSaveTrailMain extends HolidayEncodingMain {
	RecordProcedureMain recordProcedureMain;
	HolidayEncodingTrailMain holidayEncodingTrailMain;
	EditHolidayEncodingController editController;
	HolidayEncodingMain holidayEncodingMain;
	
	public HolidayEncodingSaveTrailMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public boolean createMainObject(HolidayEncoding holidayEncoding) {
		return this.createTrailRecord(ModificationType.ADD, holidayEncoding);
	}

	@Override
	public boolean updateMainObject(HolidayEncoding holidayEncoding) {
		return this.createTrailRecord(ModificationType.EDIT, holidayEncoding);
	}

	@Override
	public boolean deleteMainObject(HolidayEncoding holidayEncoding) {
		return this.createTrailRecord(ModificationType.DELETE, holidayEncoding);
	}
	
	public AnchorPane getEditLayoutHolidayEncoding() throws IOException {
		FxmlUtil<EditHolidayEncodingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/EditHolidayEncoding.fxml"));
		AnchorPane rootlayout = fxmlUtil.getFxmlPane();
		this.editController = fxmlUtil.getController();
		editController.setMainApplication(this);
		return rootlayout;
	}

	@Override
	public void initializeObjects() {
		super.initializeObjects();
		try {
			this.holidayEncodingMain = new HolidayEncodingMain();
			this.holidayEncodingTrailMain = new HolidayEncodingTrailMain();
			this.recordProcedureMain = new RecordProcedureMain();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
			e.printStackTrace();
		}
	}

	public boolean createTrailRecord(ModificationType modificationType, HolidayEncoding holidayEncoding) {
		HolidayEncodingTrail holidayEncodingTrail = new HolidayEncodingTrail();

		ObjectCopyUtil.copyProperties(holidayEncoding, holidayEncodingTrail, HolidayEncoding.class);

		if (holidayEncoding.getPrimaryKey() == null) {
			modificationType = ModificationType.ADD;
		}

		switch (modificationType) {
		case ADD:
			holidayEncodingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(1));
			this.holidayEncodingMain.createMainObject(holidayEncodingTrail);
			break;
		case EDIT:
			holidayEncodingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(2));
			
			this.holidayEncodingMain.updateMainObject(holidayEncodingTrail);
			
			break;
		case DELETE:
				holidayEncodingTrail.setChangedByUser(this.getUser().getUserName());
				holidayEncodingTrail.setChangedOnDate(new Date());
				holidayEncodingTrail.setChangedInComputer(this.getComputerName());
				holidayEncodingTrail.setUser(this.getUser());
				holidayEncodingTrail.setRecordProcedure(this.recordProcedureMain.getRecordProcedureByID(3));
				this.holidayEncodingMain.deleteMainObject(holidayEncodingTrail);
			break;

		default:
			break;
		}

		if (this.holidayEncodingTrailMain.createMainObject(holidayEncodingTrail)) {
			return true;
		}
		return false;
	}

	public RecordProcedureMain getRecordProcedureMain() {
		return recordProcedureMain;
	}

	public void setRecordProcedureMain(RecordProcedureMain recordProcedureMain) {
		this.recordProcedureMain = recordProcedureMain;
	}

	public EditHolidayEncodingController getEditController() {
		return editController;
	}

	public void setEditController(EditHolidayEncodingController editController) {
		this.editController = editController;
	}

}

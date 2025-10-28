package ph.com.lserv.production.holidayencoding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.client.ClientMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lserv.production.holidayencoding.data.HolidayEncodingDao;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;
import ph.com.lserv.production.holidayencoding.view.BrowseHolidayEncodingController;
import ph.com.lserv.production.holidayencoding.view.EditHolidayEncodingController;
import ph.com.lserv.production.holidaytypereference.HolidayTypeReferenceMain;

public class HolidayEncodingMain extends MasterMain<HolidayEncoding> {
	public ObservableList<Client> observableListClient;
	public ObservableList<HolidayEncoding> observableListHoliday;
	ClientMain clientMain;
	Client client;
	HolidayTypeReferenceMain holidayTypeReferenceMain;
	String clientName = "";

	public HolidayEncodingMain() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(HolidayEncoding.class);
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public boolean createMainObject(HolidayEncoding holidayEncoding) {
		return new HolidayEncodingDao(sqlSessionFactory).createData(holidayEncoding) > 0;
	}

	@Override
	public boolean updateMainObject(HolidayEncoding holidayEncoding) {
		return new HolidayEncodingDao(sqlSessionFactory).updateData(holidayEncoding) > 0;
	}

	@Override
	public boolean deleteMainObject(HolidayEncoding holidayEncoding) {
		return new HolidayEncodingDao(sqlSessionFactory).deleteData(holidayEncoding) > 0;
	}

	public List<HolidayEncoding> getAllData() {
		return new HolidayEncodingDao(sqlSessionFactory).getAllData();
	}

	public List<HolidayEncoding> getAllHolidayByClientName(String clientName) {
		return new HolidayEncodingDao(sqlSessionFactory).getAllHolidayByClientName(clientName);
	}

	public List<HolidayEncoding> getAllHolidayRegular() {
		return new HolidayEncodingDao(sqlSessionFactory).getAllHolidayRegular();
	}

	public List<HolidayEncoding> getAllHolidayTypeReference() {
		return new HolidayEncodingDao(sqlSessionFactory).getAllHolidayTypeReference();
	}

	public List<HolidayEncoding> getHolidayByPrikeyUser(int prikeyUser) {
		return new HolidayEncodingDao(sqlSessionFactory).getHolidayByPrikeyUser(prikeyUser);
	}

	public HolidayEncoding getHolidayByPrikey(int prikey) {
		return new HolidayEncodingDao(sqlSessionFactory).getHolidayByPrikey(prikey);
	}

	public AnchorPane showBrowseHolidayEncoding() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseHolidayEncodingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseHolidayEncoding.fxml"));
		AnchorPane browseLayout = fxmlUtil.getFxmlPane();
		BrowseHolidayEncodingController controller = fxmlUtil.getController();
		controller.setMainApplication(this);

		return browseLayout;
	}

	public boolean showEditHolidayEncoding(HolidayEncoding holidayEncoding, ModificationType modificationType) {
		try {
			FxmlUtil<EditHolidayEncodingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditHolidayEncoding.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditHolidayEncodingController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " - Holiday Encoding", this.primaryStage,
					editLayout, holidayEncoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.showBrowseHolidayEncoding();
	}

	@Override
	public void initializeObjects() {
		this.observableListClient = FXCollections.observableArrayList();
		this.observableListHoliday = FXCollections.observableArrayList();
		this.client = new Client();
		try {
			this.clientMain = new ClientMain();
			this.holidayTypeReferenceMain = new HolidayTypeReferenceMain();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void populateClient() {

		if (!observableListClient.isEmpty()) {
			this.observableListClient.clear();
		}
		this.observableListClient.addAll(this.clientMain.getClientByUser(this.getUser()));
	}

	public void getHolidays(boolean isGetRegularHoliday, Client client) {
		this.observableListHoliday.clear();

		if (isGetRegularHoliday) {
			this.observableListHoliday.addAll(this.getAllHolidayRegular());
		} else {
			this.observableListHoliday.addAll(this.getAllHolidayRegular());
			this.observableListHoliday.addAll(this.getAllHolidayByClientName(client.getClientCode()));
		}

		ObservableListUtil.sort(this.observableListHoliday, p -> new SimpleDateFormat("MM-dd").format(p.getDate()));

	}

	public ObservableList<HolidayEncoding> getObservableListHoliday() {
		return observableListHoliday;
	}

	public void setObservableListHoliday(ObservableList<HolidayEncoding> observableListHoliday) {
		this.observableListHoliday = observableListHoliday;
	}

	public ObservableList<Client> getObservableListClient() {
		return observableListClient;
	}

	public void setObservableListClient(ObservableList<Client> observableListClient) {
		this.observableListClient = observableListClient;
	}

	public ClientMain getClientMain() {
		return clientMain;
	}

	public void setClientMain(ClientMain clientMain) {
		this.clientMain = clientMain;
	}

	public HolidayTypeReferenceMain getHolidayTypeReferenceMain() {
		return holidayTypeReferenceMain;
	}

	public void setHolidayTypeReferenceMain(HolidayTypeReferenceMain holidayTypeReferenceMain) {
		this.holidayTypeReferenceMain = holidayTypeReferenceMain;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}

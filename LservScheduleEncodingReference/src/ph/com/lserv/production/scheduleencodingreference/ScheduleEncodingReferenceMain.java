package ph.com.lserv.production.scheduleencodingreference;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lserv.production.scheduleencodingreference.data.ScheduleEncodingReferenceDao;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class ScheduleEncodingReferenceMain extends MasterMain<ScheduleEncodingReference> {

	public ScheduleEncodingReferenceMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(ScheduleEncodingReference.class);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public boolean createMainObject(ScheduleEncodingReference scheduleEncodingReference) {
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).createData(scheduleEncodingReference) > 0;
	}

	@Override
	public boolean updateMainObject(ScheduleEncodingReference scheduleEncodingReference) {
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).updateData(scheduleEncodingReference) > 0;
	}

	@Override
	public boolean deleteMainObject(ScheduleEncodingReference scheduleEncodingReference) {
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).deleteData(scheduleEncodingReference) > 0;
	}
	
	public boolean deleteDataByPrikeyReferenceClient(Integer PrikeyReferenceClient) {
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).deleteDataByPrikeyReferenceClient(PrikeyReferenceClient) > 0;
	}
	
	public ScheduleEncodingReference getScheduleByScheduleNameAndClientCode(String schedName, Client clientCode) {
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).getScheduleByScheduleNameAndClientCode(schedName, clientCode);
	}
	
	public ScheduleEncodingReference getDataById(int id) {
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).getDataById(id);
	}
	
	public List<ScheduleEncodingReference> getAllScheduleByClientCode(Client client){
		return new ScheduleEncodingReferenceDao(sqlSessionFactory).getAllScheduleByClientCode(client);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeObjects() {
		// TODO Auto-generated method stub
		
	}

}

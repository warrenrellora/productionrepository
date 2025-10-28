package ph.com.lserv.production.holidayencodingtrail;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lserv.production.holidayencodingtrail.data.HolidayEncodingTrailDao;
import ph.com.lserv.production.holidayencodingtrail.model.HolidayEncodingTrail;

public class HolidayEncodingTrailMain extends MasterMain<HolidayEncodingTrail> {

	public HolidayEncodingTrailMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(HolidayEncodingTrail.class);
	}

	public static void main(String[] args) {

	}

	@Override
	public boolean createMainObject(HolidayEncodingTrail holidayEncodingTrail) {
		return new HolidayEncodingTrailDao(sqlSessionFactory).createData(holidayEncodingTrail) > 0;
	}

	@Override
	public boolean updateMainObject(HolidayEncodingTrail holidayEncodingTrail) {
		return new HolidayEncodingTrailDao(sqlSessionFactory).updateData(holidayEncodingTrail) > 0;
	}

	@Override
	public boolean deleteMainObject(HolidayEncodingTrail holidayEncodingTrail) {
		return new HolidayEncodingTrailDao(sqlSessionFactory).deleteData(holidayEncodingTrail) > 0;
	}

	public List<HolidayEncodingTrail> getAllData() {
		return new HolidayEncodingTrailDao(sqlSessionFactory).getAllData();
	}

	public HolidayEncodingTrail getDataById(Integer id) {
		return new HolidayEncodingTrailDao(sqlSessionFactory).getDataById(id);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return null;
	}

	@Override
	public void initializeObjects() {

	}

}

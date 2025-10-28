package ph.com.lserv.production.remarksreference;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lserv.production.remarksreference.data.RemarksReferenceDao;
import ph.com.lserv.production.remarksreference.model.RemarksReference;

public class RemarksReferenceMain extends MasterMain<RemarksReference> {

	public RemarksReferenceMain() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(RemarksReference.class);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean createMainObject(RemarksReference remarksReference) {
		return new RemarksReferenceDao(sqlSessionFactory).createData(remarksReference) > 0;
	}

	@Override
	public boolean updateMainObject(RemarksReference remarksReference) {
		return new RemarksReferenceDao(sqlSessionFactory).updateData(remarksReference) > 0;
	}

	@Override
	public boolean deleteMainObject(RemarksReference remarksReference) {
		return new RemarksReferenceDao(sqlSessionFactory).deleteData(remarksReference) > 0;
	}

	public List<RemarksReference> getAllData() {
		return new RemarksReferenceDao(sqlSessionFactory).getAllData();
	}

	public RemarksReference getDataById(Integer id) {
		return new RemarksReferenceDao(sqlSessionFactory).getDataById(id);
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

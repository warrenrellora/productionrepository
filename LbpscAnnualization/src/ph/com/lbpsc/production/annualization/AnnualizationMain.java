package ph.com.lbpsc.production.annualization;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.annualization.data.AnnualizationDao;
import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.util.ErrorLog;

public class AnnualizationMain extends MasterMain<Annualization> {

	public AnnualizationMain() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(Annualization.class);
	}

	@Override
	public void initializeObjects() {
		// TODO
	}

	@Override
	public boolean createMainObject(Annualization annualization) {
		return this.createAnnualization(annualization);
	}

	@Override
	public boolean updateMainObject(Annualization annualization) {
		return this.updateAnnualization(annualization);
	}

	@Override
	public boolean deleteMainObject(Annualization annualization) {
		return this.deleteAnnualization(annualization);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		// TODO
		return null;
	}

	public boolean createAnnualization(Annualization annualization) {
		boolean returnValue = false;
		try {
			AnnualizationDao annualizationDao = new AnnualizationDao(sqlSessionFactory);
			returnValue = (annualizationDao.createData(annualization) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public boolean createMultipleAnnualization(List<Annualization> annualizationList) {
		boolean returnValue = false;
		try {
			AnnualizationDao annualizationDao = new AnnualizationDao(sqlSessionFactory);
			returnValue = (annualizationDao.createMultipleData(annualizationList) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public boolean updateAnnualization(Annualization annualization) {
		boolean returnValue = false;
		try {
			AnnualizationDao annualizationDao = new AnnualizationDao(sqlSessionFactory);
			returnValue = (annualizationDao.updateData(annualization) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public boolean deleteAnnualization(Annualization annualization) {
		boolean returnValue = false;
		try {
			AnnualizationDao annualizationDao = new AnnualizationDao(sqlSessionFactory);
			returnValue = (annualizationDao.deleteData(annualization) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public boolean deleteMultipleAnnualization(List<Annualization> annualizationList) {
		boolean returnValue = false;
		try {
			AnnualizationDao annualizationDao = new AnnualizationDao(sqlSessionFactory);
			returnValue = (annualizationDao.deleteMultipleData(annualizationList) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, this.primaryStage);
		}
		return returnValue;
	}

	public Annualization getEmployeePayrollByBatchDate(Employee employee, Date payFrom, Date payTo) {
		return new AnnualizationDao(sqlSessionFactory).getPayrollDataByBatchDate(employee, payFrom, payTo);
	}

	public Annualization getAnnualizationByEmployeeId(Employee employee, Integer annnualizationYear,
			boolean isPrevious) {
		return new AnnualizationDao(sqlSessionFactory).getDataByEmployeeCode(employee, annnualizationYear, isPrevious);
	}

	public Annualization getAnnualizationPayrollByBatchDate(Employee employee, Date batchDateFrom, Date batchDateTo) {
		return new AnnualizationDao(sqlSessionFactory).getAnnualizationPayrollByBatchDate(employee, batchDateFrom,
				batchDateTo);
	}

	public List<Annualization> getAnnualizationByClient(Client client, Integer annnualizationYear) {
		return new AnnualizationDao(sqlSessionFactory).getDataByClient(client, annnualizationYear);
	}

	public List<Annualization> getAnnualizationByAnnualizationYear(List<Client> clientList, Integer annualizationYear) {
		return new AnnualizationDao(sqlSessionFactory).getAnnualizationByAnnualizationYear(clientList,
				annualizationYear);
	}

	public Annualization getAnnualizationByKey(Integer propkey) {
		return new AnnualizationDao(sqlSessionFactory).getDataByKey(propkey);
	}

	public Annualization getAnnualizationByQuitclaim(Employee employee, Integer annnualizationYear,
			Integer prikeyQuitclaim) {
		return new AnnualizationDao(sqlSessionFactory).getAnnualizationByQuitclaim(employee, annnualizationYear,
				prikeyQuitclaim);
	}
}
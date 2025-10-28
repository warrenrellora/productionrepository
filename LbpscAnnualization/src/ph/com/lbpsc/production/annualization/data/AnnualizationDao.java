package ph.com.lbpsc.production.annualization.data;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.annualization.data.mapper.AnnualizationMapper;
import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.employee.model.Employee;

public class AnnualizationDao implements IDAO<Annualization> {
	private SqlSessionFactory sqlSessionFactory = null;
	private AnnualizationMapper mapper;
	private List<Annualization> annualizationList;

	public AnnualizationDao() {
	}

	public AnnualizationDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<Annualization> getAllData() {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualizationList = mapper.getAllAnnualization();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationList;
	}

	public int createData(Annualization annualization) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			cnt = mapper.createAnnualization(annualization);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	public int createMultipleData(List<Annualization> annualizationList) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			for (Annualization annualization : annualizationList) {
				mapper = sqlSession.getMapper(AnnualizationMapper.class);
				cnt = mapper.createAnnualization(annualization);
			}
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	public int updateData(Annualization annualization) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			cnt = mapper.updateAnnualization(annualization);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	public int deleteData(Annualization annualization) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			cnt = mapper.deleteAnnualization(annualization);
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	public int deleteMultipleData(List<Annualization> annualizationList) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			for (Annualization annualization : annualizationList) {
				mapper = sqlSession.getMapper(AnnualizationMapper.class);
				cnt = mapper.deleteAnnualization(annualization);
			}
			sqlSession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return cnt;
	}

	public Annualization getDataByKey(Integer propkey) {
		Annualization annualization = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualization = mapper.getAnnualizationById(propkey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualization;
	}

	public Annualization getPayrollDataByBatchDate(Employee employee, Date payFrom, Date payTo) {
		Annualization annualization = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualization = mapper.getEmployeePayrollByBatchDate(employee, payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualization;
	}

	public Annualization getDataByEmployeeCode(Employee employee, Integer annnualizationYear, boolean isPrevious) {
		Annualization annualization = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualization = mapper.getAnnualizationByEmployeeId(employee, annnualizationYear, isPrevious);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualization;
	}

	public List<Annualization> getDataByClient(Client client, Integer annnualizationYear) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualizationList = mapper.getAnnualizationByClient(client, annnualizationYear);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationList;
	}

	public List<Annualization> getAnnualizationByAnnualizationYear(List<Client> clientList, Integer annualizationYear) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualizationList = mapper.getAnnualizationByAnnualizationYear(clientList, annualizationYear);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationList;
	}

	public Annualization getAnnualizationPayrollByBatchDate(Employee employee, Date batchDateFrom, Date batchDateTo) {
		Annualization annualization = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualization = mapper.getAnnualizationPayrollByBatchDate(employee, batchDateFrom, batchDateTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualization;
	}

	public Annualization getAnnualizationByQuitclaim(Employee employee, Integer annnualizationYear,
			Integer prikeyQuitclaim) {
		Annualization annualization = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationMapper.class);
			annualization = mapper.getAnnualizationByQuitclaim(employee, annnualizationYear, prikeyQuitclaim);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualization;
	}

}

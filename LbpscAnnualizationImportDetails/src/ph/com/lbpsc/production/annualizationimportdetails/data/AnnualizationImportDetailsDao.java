package ph.com.lbpsc.production.annualizationimportdetails.data;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.annualizationimportdetails.data.mapper.AnnualizationImportDetailsMapper;
import ph.com.lbpsc.production.annualizationimportdetails.model.AnnualizationImportDetails;

public class AnnualizationImportDetailsDao implements IDAO<AnnualizationImportDetails> {
	private SqlSessionFactory sqlSessionFactory = null;
	private AnnualizationImportDetailsMapper mapper;
	private List<AnnualizationImportDetails> annualizationImportDetailsList;

	public AnnualizationImportDetailsDao() {
	}

	public AnnualizationImportDetailsDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<AnnualizationImportDetails> getAllData() {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			annualizationImportDetailsList = mapper.getAllAnnualizationImportDetails();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationImportDetailsList;
	}

	public int createData(AnnualizationImportDetails annualizationImportDetails) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			cnt = mapper.createAnnualizationImportDetails(annualizationImportDetails);
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

	public int updateData(AnnualizationImportDetails annualizationImportDetails) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			cnt = mapper.updateAnnualizationImportDetails(annualizationImportDetails);
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

	public int deleteData(AnnualizationImportDetails annualizationImportDetails) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			cnt = mapper.deleteAnnualizationImportDetails(annualizationImportDetails);
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

	public AnnualizationImportDetails getDataByKey(Integer propkey) {
		AnnualizationImportDetails annualizationImportDetails = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			annualizationImportDetails = mapper.getAnnualizationImportDetailsById(propkey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationImportDetails;
	}

	public List<AnnualizationImportDetails> getAnnualizationImportDetails(
			AnnualizationImportDetails annualizationImportDetails) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			annualizationImportDetailsList = mapper.getAnnualizationImportDetails(annualizationImportDetails);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationImportDetailsList;
	}

	public List<AnnualizationImportDetails> getAnnualizationImportDetailsByDate(
			AnnualizationImportDetails annualizationImportDetails) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
			annualizationImportDetailsList = mapper.getAnnualizationImportDetailsByDate(annualizationImportDetails);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationImportDetailsList;
	}

	public int createMultipleData(List<AnnualizationImportDetails> annualizationImportDetailsList) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			for (AnnualizationImportDetails annualizationImportDetails : annualizationImportDetailsList) {
				mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
				cnt = mapper.createAnnualizationImportDetails(annualizationImportDetails);
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

	public int deleteMultipleData(List<AnnualizationImportDetails> annualizationImportDetailsList) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			for (AnnualizationImportDetails annualizationImportDetails : annualizationImportDetailsList) {
				mapper = sqlSession.getMapper(AnnualizationImportDetailsMapper.class);
				cnt = mapper.deleteAnnualizationImportDetails(annualizationImportDetails);
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
}

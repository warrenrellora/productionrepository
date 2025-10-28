package ph.com.lbpsc.production.annualizationdetails.data;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.annualizationbreakdown.model.AnnualizationBreakdown;
import ph.com.lbpsc.production.annualizationdetails.data.mapper.AnnualizationDetailsMapper;
import ph.com.lbpsc.production.annualizationdetails.model.AnnualizationDetails;

public class AnnualizationDetailsDao implements IDAO<AnnualizationDetails> {
	private SqlSessionFactory sqlSessionFactory = null;
	private AnnualizationDetailsMapper mapper;
	private List<AnnualizationDetails> annualizationDetailsList;

	public AnnualizationDetailsDao() {
	}

	public AnnualizationDetailsDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public List<AnnualizationDetails> getAllData() {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			annualizationDetailsList = mapper.getAllAnnualizationDetails();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationDetailsList;
	}

	public int createData(AnnualizationDetails annualizationDetails) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			cnt = mapper.createAnnualizationDetails(annualizationDetails);
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

	public int updateData(AnnualizationDetails annualizationDetails) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			cnt = mapper.updateAnnualizationDetails(annualizationDetails);
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

	public int deleteData(AnnualizationDetails annualizationDetails) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			cnt = mapper.deleteAnnualizationDetails(annualizationDetails);
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

	public AnnualizationDetails getDataByKey(Integer propkey) {
		AnnualizationDetails annualizationDetails = null;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			annualizationDetails = mapper.getAnnualizationDetailsById(propkey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationDetails;
	}

	public List<AnnualizationDetails> getAnnualizationDetailsByBreakdown(Annualization annualization,
			AnnualizationBreakdown annualizationBreakdown) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			annualizationDetailsList = mapper.getAnnualizationDetailsByBreakdown(annualization, annualizationBreakdown);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return annualizationDetailsList;
	}

	public int deleteAnnualizationDetailsList(List<AnnualizationDetails> annualizationDetailsList) {
		int cnt = 0;
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(AnnualizationDetailsMapper.class);
			cnt = mapper.deleteAnnualizationDetailsList(annualizationDetailsList);
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

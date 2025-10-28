package ph.com.lserv.production.remarksreference.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.remarksreference.data.mapper.RemarksReferenceMapper;
import ph.com.lserv.production.remarksreference.model.RemarksReference;

public class RemarksReferenceDao implements IDAO<RemarksReference> {
	RemarksReferenceMapper mapper;
	SqlSessionFactory sqlSessionFactory;
	List<RemarksReference> remarksReferenceList = new ArrayList<>();
	RemarksReference remarksReference;
	
	public RemarksReferenceDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<RemarksReference> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(RemarksReferenceMapper.class);
			remarksReferenceList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return remarksReferenceList;
	}

	@Override
	public int createData(RemarksReference remarksReference) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(RemarksReferenceMapper.class);
			returnValue = mapper.createData(remarksReference);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	}

	@Override
	public int updateData(RemarksReference remarksReference) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(RemarksReferenceMapper.class);
			returnValue = mapper.updateData(remarksReference);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	}

	@Override
	public int deleteData(RemarksReference remarksReference) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(RemarksReferenceMapper.class);
			returnValue = mapper.deleteData(remarksReference);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	}

	@Override
	public RemarksReference getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(RemarksReferenceMapper.class);
			remarksReference = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return remarksReference;
	}

}

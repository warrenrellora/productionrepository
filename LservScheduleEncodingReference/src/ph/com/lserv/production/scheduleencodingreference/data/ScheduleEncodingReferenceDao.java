package ph.com.lserv.production.scheduleencodingreference.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lserv.production.scheduleencodingreference.data.mapper.ScheduleEncodingReferenceMapper;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class ScheduleEncodingReferenceDao implements IDAO<ScheduleEncodingReference>{
	SqlSessionFactory sqlSessionFactory;
	ScheduleEncodingReferenceMapper mapper;
	ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
	List<ScheduleEncodingReference> listScheduleEncodingReference = new ArrayList<>();
	
	public ScheduleEncodingReferenceDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<ScheduleEncodingReference> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingReferenceMapper.class);
			listScheduleEncodingReference = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listScheduleEncodingReference;
	}

	@Override
	public int createData(ScheduleEncodingReference scheduleEncodingReference) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingReferenceMapper.class);
			returnValue = mapper.createData(scheduleEncodingReference);
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
	public int updateData(ScheduleEncodingReference scheduleEncodingReference) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingReferenceMapper.class);
			returnValue = mapper.updateData(scheduleEncodingReference);
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
	public int deleteData(ScheduleEncodingReference scheduleEncodingReference) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingReferenceMapper.class);
			returnValue = mapper.deleteData(scheduleEncodingReference);
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
	
	public int deleteDataByPrikeyReferenceClient(Integer PrikeyReferenceClient) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingReferenceMapper.class);
			returnValue = mapper.deleteDataByPrikeyReferenceClient(PrikeyReferenceClient);
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
	public ScheduleEncodingReference getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingReferenceMapper.class);
			scheduleEncodingReference = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingReference;
	}
	
	public ScheduleEncodingReference getScheduleByScheduleNameAndClientCode(String schedName, Client clientCode) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(ScheduleEncodingReferenceMapper.class);
			scheduleEncodingReference = this.mapper.getScheduleByScheduleNameAndClientCode(schedName, clientCode);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return scheduleEncodingReference;
	}
	
	public List<ScheduleEncodingReference> getAllScheduleByClientCode(Client client){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(ScheduleEncodingReferenceMapper.class);
			listScheduleEncodingReference = mapper.getAllScheduleByClientCode(client);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listScheduleEncodingReference;
	}

}

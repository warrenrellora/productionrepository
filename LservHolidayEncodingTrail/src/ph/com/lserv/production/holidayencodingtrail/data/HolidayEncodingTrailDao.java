package ph.com.lserv.production.holidayencodingtrail.data;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.holidayencodingtrail.data.mapper.HolidayEncodingTrailMapper;
import ph.com.lserv.production.holidayencodingtrail.model.HolidayEncodingTrail;

public class HolidayEncodingTrailDao implements IDAO<HolidayEncodingTrail> {
	SqlSessionFactory sqlSessionFactory;
	HolidayEncodingTrailMapper mapper;
	List<HolidayEncodingTrail> holidayEncodingTrailList;
	HolidayEncodingTrail holidayEncodingTrail;
	
	public HolidayEncodingTrailDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<HolidayEncodingTrail> getAllData() {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();
		try {
			mapper = sqlSession.getMapper(HolidayEncodingTrailMapper.class);
			holidayEncodingTrailList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return holidayEncodingTrailList;
	}

	@Override
	public int createData(HolidayEncodingTrail holidayEncodingTrail) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingTrailMapper.class);
			returnValue = mapper.createData(holidayEncodingTrail);
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
	public int updateData(HolidayEncodingTrail holidayEncodingTrail) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingTrailMapper.class);
			returnValue = mapper.updateData(holidayEncodingTrail);
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
	public int deleteData(HolidayEncodingTrail holidayEncodingTrail) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingTrailMapper.class);
			returnValue = mapper.deleteData(holidayEncodingTrail);
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
	public HolidayEncodingTrail getDataById(int id) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(HolidayEncodingTrailMapper.class);
			holidayEncodingTrail = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return holidayEncodingTrail;
	}


}

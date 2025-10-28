package ph.com.lserv.production.holidayencoding.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lserv.production.holidayencoding.data.mapper.HolidayEncodingMapper;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;

public class HolidayEncodingDao implements IDAO<HolidayEncoding> {
	SqlSessionFactory sqlSessionFactory;
	HolidayEncodingMapper mapper;
	HolidayEncoding holidayEncoding = new HolidayEncoding();
	List<HolidayEncoding> listHolidayEncoding = new ArrayList<>();

	public HolidayEncodingDao(SqlSessionFactory sqlSessionFactory) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public List<HolidayEncoding> getAllHolidayTypeReference(){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			listHolidayEncoding = mapper.getAllHolidayTypeReference();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listHolidayEncoding;
	}
	
	public List<HolidayEncoding> getAllHolidayRegular(){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			listHolidayEncoding = mapper.getAllHolidayRegular();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listHolidayEncoding;
	}
	
	public List<HolidayEncoding> getAllHolidayByClientName(String clientName){
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			listHolidayEncoding = mapper.getAllHolidayByClientName(clientName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listHolidayEncoding;
	}

	@Override
	public List<HolidayEncoding> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			listHolidayEncoding = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listHolidayEncoding;
	}

	@Override
	public int createData(HolidayEncoding holidayEncoding) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			returnValue = mapper.createData(holidayEncoding);
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
	public int updateData(HolidayEncoding holidayEncoding) {
		// TODO Auto-generated method stub
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			returnValue = mapper.updateData(holidayEncoding);
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
	public int deleteData(HolidayEncoding holidayEncoding) {
		// TODO Auto-generated method stub
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			returnValue = mapper.deleteData(holidayEncoding);
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
	public HolidayEncoding getDataById(int id) {
		// TODO Auto-generated method stub
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(HolidayEncodingMapper.class);
			holidayEncoding = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return holidayEncoding;
	}
	
	public List<HolidayEncoding> getHolidayByPrikeyUser(int prikeyUser) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(HolidayEncodingMapper.class);
			listHolidayEncoding = mapper.getHolidayByPrikeyUser(prikeyUser);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return listHolidayEncoding;
	}
	
	public HolidayEncoding getHolidayByPrikey(int prikey) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(HolidayEncodingMapper.class);
			holidayEncoding = this.mapper.getHolidayByPrikey(prikey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return holidayEncoding;
	}
	
}

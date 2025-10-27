package ph.com.lserv.production.employeescheduleuploading.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lserv.production.employeescheduleuploading.data.mapper.EmployeeScheduleUploadingMapper;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;

public class EmployeeScheduleUploadingDao implements IDAO<EmployeeScheduleUploading> {
	EmployeeScheduleUploadingMapper mapper;
	SqlSessionFactory sqlSessionFactory;
	List<EmployeeScheduleUploading> employeeScheduleUploadingList = new ArrayList<>();
	List<Integer> employeeIdList = new ArrayList<>();
	List<String> clientCdList = new ArrayList<>();
	EmployeeScheduleUploading employeeScheduleUploading;

	public EmployeeScheduleUploadingDao(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public List<EmployeeScheduleUploading> getAllData() {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getAllData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<Integer> getAllEmployeeIdByPayFromPayTo(Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeIdList = mapper.getAllEmployeeIdByPayFromPayTo(payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeIdList;
	}

	public List<Integer> getAllEmployeeIdByClientCodePayFromPayTo(String clientCode, Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeIdList = mapper.getAllEmployeeIdByClientCodePayFromPayTo(clientCode, payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeIdList;
	}

	public List<Integer> getAllEmployeeIdByClientCodeDepartmentPayFromPayTo(String clientCode, Integer departmentNo,
			Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeIdList = mapper.getAllEmployeeIdByClientCodeDepartmentPayFromPayTo(clientCode, departmentNo,
					payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeIdList;
	}

	public List<Integer> getAllValidatedEmployeeIdByPayFromPayTo(Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeIdList = mapper.getAllValidatedEmployeeIdByPayFromPayTo(payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeIdList;
	}

	public List<Integer> getAllEmpIdExcludingSelectedDeptPay(Integer deptNo, String clientCode, Date payFrom) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeIdList = mapper.getAllEmpIdExcludingSelectedDeptPay(deptNo, clientCode, payFrom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeIdList;
	}

	public List<EmployeeScheduleUploading> getDataByPayFromPayTo(Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getDataByPayFromPayTo(payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getDataByClientPayFromPayTo(Client client, Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getDataByClientPayFromPayTo(client, payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getDataByClientPayFromPayToAll(Client client, Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getDataByClientPayFromPayToAll(client, payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getDataByDateEntryEmployeePayFromPayTo(String dateEntry,
			Integer employeeCode, Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getDataByDateEntryEmployeePayFromPayTo(dateEntry, employeeCode,
					payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getDataByClientDepartmentPayFromPayTo(Client client, Department department,
			Date payFrom, Date payTo, Integer isValidated) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getDataByClientDepartmentPayFromPayTo(client, department, payFrom,
					payTo, isValidated);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getDataByEmployeeIdPayFrom(Integer employeeCode, Date payFrom) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getDataByEmployeeIdPayFrom(employeeCode, payFrom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getAllScheduleEntriesByPayFromPayTo(Date payFrom, Date payTo,
			Integer isRegularSchedule) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getAllScheduleEntriesByPayFromPayTo(payFrom, payTo,
					isRegularSchedule);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	public List<EmployeeScheduleUploading> getAllScheduleWithoutEntriesByPayFromPayTo(Date payFrom, Date payTo) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getAllScheduleWithoutEntriesByPayFromPayTo(payFrom, payTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	}

	@Override
	public int createData(EmployeeScheduleUploading employeeScheduleUploading) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.createData(employeeScheduleUploading);
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

	public int createMultipleData(List<EmployeeScheduleUploading> biometricsToSaveList) {
		// int returnValue = 0;
		//
		// SqlSession sqlsession =
		// this.sqlSessionFactory.openSession(ExecutorType.BATCH);
		//
		// try {
		// mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
		// int index = 0;
		// while (index < biometricsToSaveList.size()) {
		// System.out.println("createMultipleData: " + index);
		// mapper.createData(biometricsToSaveList.get(index));
		// index++;
		// }
		// System.out.println("tapos na");
		// returnValue = 1;
		// sqlsession.commit();
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (sqlsession != null) {
		// sqlsession.close();
		// }
		// }
		//
		// return returnValue;

		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.createMultipleData(biometricsToSaveList);
			System.out.println("returnValue: " + returnValue);
			System.out.println("tapos na maginsert");
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
	public int updateData(EmployeeScheduleUploading employeeScheduleUploading) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.updateData(employeeScheduleUploading);
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

	public int updateMultipleData(List<EmployeeScheduleUploading> biometricsToUpdateList) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession(ExecutorType.BATCH);

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			int index = 0;
			while (index < biometricsToUpdateList.size()) {
				mapper.updateData(biometricsToUpdateList.get(index));
				index++;
			}
			returnValue = 1;
			// returnValue = mapper.updateMultipleData(biometricsToUpdateList);
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

	public int updateDataByPayFromPayTo(Date payFrom, Date payTo) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.updateDataByPayFromPayTo(payFrom, payTo);
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
	public int deleteData(EmployeeScheduleUploading employeeScheduleUploading) {
		// TODO Auto-generated method stub
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.deleteData(employeeScheduleUploading);
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

	public int deleteMultipleData(List<EmployeeScheduleUploading> biometricsToDeleteList) {
		int returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession(ExecutorType.BATCH);

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			int index = 0;
			while (index < biometricsToDeleteList.size()) {
				mapper.deleteData(biometricsToDeleteList.get(index));
				index++;
			}
			returnValue = 1;
			// returnValue = mapper.updateMultipleData(biometricsToUpdateList);
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
	public EmployeeScheduleUploading getDataById(int id) {
		// TODO Auto-generated method stub
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploading = this.mapper.getDataById(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleUploading;
	}

	public EmployeeScheduleUploading getDataByEmployeeIdAndDateEntry(Integer employeeID, Date dateEntry) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploading = this.mapper.getDataByEmployeeIdAndDateEntry(employeeID, dateEntry);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleUploading;
	}

	public EmployeeScheduleUploading getDataByEmpIdDateEntryPayFrom(Integer employeeCode, Date dateEntry,
			Date payFrom) {
		SqlSession sqlSession = this.sqlSessionFactory.openSession();

		try {
			this.mapper = sqlSession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploading = this.mapper.getDataByEmpIdDateEntryPayFrom(employeeCode, dateEntry, payFrom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		return employeeScheduleUploading;
	}

	public Integer countFrequencyOfLateByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		Integer returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.countFrequencyOfLateByEmpIdPayFrom(employeeID, payFrom);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	};

	public Integer countTotalAbsentByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		Integer returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.countTotalAbsentByEmpIdPayFrom(employeeID, payFrom);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	};

	public Integer computeTotalLateByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		Integer returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.computeTotalLateByEmpIdPayFrom(employeeID, payFrom);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	};

	public Integer countWorkingDaysByEmpIdPayFromPayTo(Integer employeeID, Date payFrom, Date payTo) {
		Integer returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.countWorkingDaysByEmpIdPayFromPayTo(employeeID, payFrom, payTo);
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

	public Integer countHolidaysByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		Integer returnValue = 0;

		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			returnValue = mapper.countHolidaysByEmpIdPayFrom(employeeID, payFrom);
			sqlsession.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return returnValue;
	};

	public List<EmployeeScheduleUploading> getAllEntriesWithLateByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			employeeScheduleUploadingList = mapper.getAllEntriesWithLateByEmpIdPayFrom(employeeID, payFrom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return employeeScheduleUploadingList;
	};

	public List<String> getAllClientCdByPayFrom(Date payFrom) {
		SqlSession sqlsession = this.sqlSessionFactory.openSession();

		try {
			mapper = sqlsession.getMapper(EmployeeScheduleUploadingMapper.class);
			clientCdList = mapper.getAllClientCdByPayFrom(payFrom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sqlsession != null) {
				sqlsession.close();
			}
		}

		return clientCdList;
	}

}

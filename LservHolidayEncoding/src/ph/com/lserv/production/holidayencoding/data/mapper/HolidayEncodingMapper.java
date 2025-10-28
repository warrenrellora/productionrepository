package ph.com.lserv.production.holidayencoding.data.mapper;

import java.util.List;

import ph.com.lserv.production.holidayencoding.data.IDAO;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;

public interface HolidayEncodingMapper extends IDAO<HolidayEncoding> {
	
	public List<HolidayEncoding> getAllHolidayByClientName(String clientName);
	
	public List<HolidayEncoding> getAllHolidayRegular();
	
	public List<HolidayEncoding> getAllHolidayTypeReference();
	
	public List<HolidayEncoding> getHolidayByPrikeyUser(int prikeyUser);
	
	public HolidayEncoding getHolidayByPrikey(int prikey);
}

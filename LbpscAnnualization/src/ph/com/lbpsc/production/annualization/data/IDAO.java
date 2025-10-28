package ph.com.lbpsc.production.annualization.data;

import java.util.List;

public interface IDAO<E> {
	public List<E> getAllData();

	public int createData(E objData);

	public int updateData(E objData);

	public int deleteData(E objData);

	public E getDataByKey(Integer propKey);
}

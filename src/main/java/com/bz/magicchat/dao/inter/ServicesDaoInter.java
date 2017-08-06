package com.bz.magicchat.dao.inter;

import com.bz.magicchat.domain.Services;

public interface ServicesDaoInter {
	public Services findSerivceById(int service_id);
	public int registerService(Services service);
}

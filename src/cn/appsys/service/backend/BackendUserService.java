package cn.appsys.service.backend;


import cn.appsys.pojo.BackendUser;

public interface BackendUserService {
	/**
	 * 通过userCode获取User
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	public BackendUser getLoginUser(String userCode,String password)throws Exception;
}

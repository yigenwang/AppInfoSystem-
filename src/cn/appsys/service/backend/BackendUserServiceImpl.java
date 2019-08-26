package cn.appsys.service.backend;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.backenduser.BackendUserMapper;
import cn.appsys.pojo.BackendUser;

@Service
public class BackendUserServiceImpl implements BackendUserService {
	@Resource
	private BackendUserMapper backMapper;

	@Override
	public BackendUser getLoginUser(String userCode, String password)
			throws Exception {
		BackendUser user = null;
		user = backMapper.getLoginUser(userCode);
		if (null != user) {
			if (!user.getUserPassword().equals(password)) {
				user = null;
			}
		}
		return user;
	}

}

package cn.appsys.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppVersion;

@Service
public class AppVersionServiceImpl implements AppVersionService {
	@Resource
	private AppVersionMapper versionMapper;
	@Resource
	private AppInfoMapper appInfoMapper;

	@Override
	public List<AppVersion> getAppVersionList(Integer appId) {
		try {
			return versionMapper.getAppVersionList(appId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int add(AppVersion appVersion) {
		int num = 0;
		Integer versionId = null;
		try {
			if (versionMapper.add(appVersion) > 0) {
				num = 1;
				versionId = appVersion.getId();
			}
			if (appInfoMapper.updateVersionId(versionId, appVersion.getAppId()) > 0) {
				num = 1;
			} else {
				num = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	public AppVersion getAppVersionById(Integer id) {
		try {
			return versionMapper.getAppVersionById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deleteApkFile(Integer id) throws Exception {
		boolean flag = false;
		if (versionMapper.deleteApkFile(id) > 0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean modify(AppVersion appVersion) throws Exception {
		boolean flag = false;
		if (versionMapper.modify(appVersion) > 0) {
			flag = true;
		}
		return flag;
	}
}

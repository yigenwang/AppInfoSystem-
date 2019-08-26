package cn.appsys.service.developer;

import java.util.List;

import cn.appsys.pojo.AppVersion;

public interface AppVersionService {
	public List<AppVersion> getAppVersionList(Integer appId);

	public int add(AppVersion appVersion);

	public AppVersion getAppVersionById(Integer id);

	/**
	 * 删除apk文件
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean deleteApkFile(Integer id) throws Exception;

	/**
	 * 修改app版本信息
	 * 
	 * @param appVersion
	 * @return
	 * @throws Exception
	 */
	public boolean modify(AppVersion appVersion) throws Exception;
}

package cn.appsys.service.developer;

import java.util.List;

import cn.appsys.pojo.AppInfo;

public interface AppInfoService {
	public List<AppInfo> getAppInfoList(String softwareName, Integer status,
			Integer categoryLevel1, Integer categoryLevel2,
			Integer categoryLevel3, Integer devId, Integer flatformId,
			Integer currentPageNo, Integer pageSize);

	public int getAppInfoCount(String querySoftwareName, Integer queryStatus,
			Integer queryCategoryLevel1, Integer queryCategoryLevel2,
			Integer queryCategoryLevel3, Integer queryFlatformId, Integer devId)
			throws Exception;

	public AppInfo getAppInfo(Integer id, String APKName);

	public int add(AppInfo appInfo);

	public int modify(AppInfo appInfo);

	/**
	 * 删除logo图片
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int deleteAppLogo(Integer id) throws Exception;

	/**
	 * 根据appId删除app应用
	 * 
	 * @param delId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteAppInfoById(Integer delId) throws Exception;

	public boolean appsysdeleteAppById(Integer id) throws Exception;

	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo)
			throws Exception;
}

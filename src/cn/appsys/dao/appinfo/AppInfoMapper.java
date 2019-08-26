package cn.appsys.dao.appinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.appsys.pojo.AppInfo;

public interface AppInfoMapper {
	public List<AppInfo> getAppInfoList(
			@Param("softwareName") String softwareName,
			@Param("status") Integer status,
			@Param("flatformId") Integer flatformId,
			@Param("categoryLevel1") Integer categoryLevel1,
			@Param("categoryLevel2") Integer categoryLevel2,
			@Param("categoryLevel3") Integer categoryLevel3,
			@Param("devId") Integer devId,
			@Param("currentPageNo") Integer currentPageNo,
			@Param("pageSize") Integer pageSize);

	public int getAppInfoCount(
			@Param(value = "softwareName") String querySoftwareName,
			@Param(value = "status") Integer queryStatus,
			@Param(value = "categoryLevel1") Integer queryCategoryLevel1,
			@Param(value = "categoryLevel2") Integer queryCategoryLevel2,
			@Param(value = "categoryLevel3") Integer queryCategoryLevel3,
			@Param(value = "flatformId") Integer queryFlatformId,
			@Param(value = "devId") Integer devId) throws Exception;

	public AppInfo getAppInfo(@Param(value = "id") Integer id,
			@Param(value = "APKName") String APKName);

	public int add(AppInfo appInfo) throws Exception;

	public int modify(AppInfo appInfo) throws Exception;

	public int deleteAppLogo(@Param(value = "id") Integer id) throws Exception;

	/**
	 * 根据appId，更新最新versionId
	 * 
	 * @param versionId
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public int updateVersionId(@Param(value = "versionId") Integer versionId,
			@Param(value = "id") Integer appId) throws Exception;

	public int deleteAppInfoById(@Param(value = "id") Integer delId)
			throws Exception;

	public int updateSatus(@Param(value = "status") Integer status,
			@Param(value = "id") Integer id) throws Exception;
}

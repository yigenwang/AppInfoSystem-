package cn.appsys.service.developer;

import java.io.File;
import java.sql.Date;
import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;

@Service
public class AppInfoServiceImpl implements AppInfoService {
	@Resource
	private AppInfoMapper mapper;
	@Resource
	private AppVersionMapper versionMapper;

	@Override
	public List<AppInfo> getAppInfoList(String softwareName, Integer status,
			Integer categoryLevel1, Integer categoryLevel2,
			Integer categoryLevel3, Integer flatformId, Integer devId,
			Integer currentPageNo, Integer pageSize) {
		return mapper.getAppInfoList(softwareName, status, flatformId,
				categoryLevel1, categoryLevel2, categoryLevel3, devId,
				currentPageNo, pageSize);
	}

	@Override
	public int getAppInfoCount(String querySoftwareName, Integer queryStatus,
			Integer queryCategoryLevel1, Integer queryCategoryLevel2,
			Integer queryCategoryLevel3, Integer queryFlatformId, Integer devId)
			throws Exception {
		return mapper.getAppInfoCount(querySoftwareName, queryStatus,
				queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3,
				queryFlatformId, devId);
	}

	@Override
	public AppInfo getAppInfo(Integer id, String APKName) {
		return mapper.getAppInfo(id, APKName);
	}

	@Override
	public int add(AppInfo appInfo) {
		try {
			return mapper.add(appInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int modify(AppInfo appInfo) {
		try {
			return mapper.modify(appInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int deleteAppLogo(Integer id) throws Exception {
		return mapper.deleteAppLogo(id);
	}

	@Override
	public boolean deleteAppInfoById(Integer delId) throws Exception {
		boolean flag = false;
		if (mapper.deleteAppInfoById(delId) > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 业务：根据appId删除APP信息 1、通过appId，查询app_verion表中是否有数据
	 * 2、若版本表中有该app应用对应的版本信息，则进行级联删除，先删版本信息（app_version），后删app基本信息（app_info）
	 * 3、若版本表中无该app应用对应的版本信息，则直接删除app基本信息（app_info）。 注意：事务控制，上传文件的删除
	 */
	@Override
	public boolean appsysdeleteAppById(Integer id) throws Exception {
		boolean flag = false;
		int versionCount = versionMapper.getVersionCountByAppId(id);
		List<AppVersion> appVersionList = null;
		if (versionCount > 0) {
			// 1、 如果存在则先删除版本信息
			// (1) 删除上传的文件
			appVersionList = versionMapper.getAppVersionList(id);
			for (AppVersion appVersion : appVersionList) {
				if (appVersion.getApkLocPath() != null
						&& !appVersion.getApkLocPath().equals("")) {
					File file = new File(appVersion.getApkLocPath());
					if (file.exists()) {
						if (!file.delete()) {
							throw new Exception();
						}
					}
				}
				// (2) 删除表数据
				versionMapper.deleteVersionByAppId(id);
			}
			// 2、再删除app基础信息
			// （1）删除上传的图片
			AppInfo appInfo = mapper.getAppInfo(id, null);
			if (appInfo.getLogoPicPath() != null
					&& !appInfo.getLogoPicPath().equals("")) {
				File file = new File(appInfo.getLogoPicPath());
				if (file.exists()) {
					if (!file.delete()) {
						throw new Exception();
					}
				}
			}
			// (2)删除表数据
			if (mapper.deleteAppInfoById(id) > 0) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfo)
			throws Exception {
		AppInfo appInfos = mapper.getAppInfo(appInfo.getId(), null);
		Integer operator = appInfo.getModifyBy();
		if (operator < 0 || appInfo.getId() < 0) {
			throw new Exception();
		}
		if (null == appInfos) {
			return false;
		} else {
			switch (appInfos.getStatus()) {
			case 2:
				// 当状态为审核通过，可以进行上架操作
				onSale(appInfos, operator, 4, 2);
				break;
			case 4:
				// 当状态为已上架，可以进行下架操作
				offSale(appInfos, operator, 5);
				break;
			case 5:
				// 当状态为已下架，可以进行上架操作
				onSale(appInfos, operator, 4, 2);
				break;
			default:
				return false;
			}
		}
		return true;
	}

	private void onSale(AppInfo appInfo, Integer operator,
			Integer appInfStatus, Integer versionStatus) throws Exception {
		offSale(appInfo, operator, appInfStatus);
		setSaleSwitchToAppVersion(appInfo, operator, versionStatus);
	}

	private boolean offSale(AppInfo appInfo, Integer operator,
			Integer appInfStatus) throws Exception {
		AppInfo _appInfo = new AppInfo();
		_appInfo.setId(appInfo.getId());
		_appInfo.setStatus(appInfStatus);
		_appInfo.setModifyBy(operator);
		_appInfo.setOffSaleDate(new Date(System.currentTimeMillis()));
		mapper.modify(_appInfo);
		return true;
	}

	private boolean setSaleSwitchToAppVersion(AppInfo appInfo,
			Integer operator, Integer saleStatus) throws Exception {
		AppVersion appVersion = new AppVersion();
		appVersion.setId(appInfo.getVersionId());
		appVersion.setPublishStatus(saleStatus);
		appVersion.setModifyBy(operator);
		appVersion.setModifyDate(new Date(System.currentTimeMillis()));
		versionMapper.modify(appVersion);
		return false;
	}
}

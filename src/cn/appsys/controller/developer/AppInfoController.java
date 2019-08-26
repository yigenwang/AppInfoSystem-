package cn.appsys.controller.developer;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value = "dev/flatform/app")
public class AppInfoController {
	private Logger logger = Logger.getLogger(AppInfoController.class);

	@Resource
	private DataDictionaryService dictionaryService;
	@Resource
	private AppInfoService appInfoService;
	@Resource
	private AppCategoryService appCategoryService;
	@Resource
	private AppVersionService appVersionService;

	@RequestMapping(value = "/list")
	public String getAppInfoList(
			Model model,
			HttpSession session,
			@RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
			@RequestParam(value = "queryStatus", required = false) String _queryStatus,
			@RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
			@RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
			@RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
			@RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {

		logger.info("getAppInfoList -- > querySoftwareName: "
				+ querySoftwareName);
		logger.info("getAppInfoList -- > queryStatus: " + _queryStatus);
		logger.info("getAppInfoList -- > queryCategoryLevel1: "
				+ _queryCategoryLevel1);
		logger.info("getAppInfoList -- > queryCategoryLevel2: "
				+ _queryCategoryLevel2);
		logger.info("getAppInfoList -- > queryCategoryLevel3: "
				+ _queryCategoryLevel3);
		logger.info("getAppInfoList -- > queryFlatformId: " + _queryFlatformId);
		logger.info("getAppInfoList -- > pageIndex: " + pageIndex);

		Integer devId = ((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId();
		List<AppInfo> appInfoList = null;
		List<DataDictionary> statusList = null;
		List<DataDictionary> flatFormList = null;
		List<AppCategory> categoryLevel1List = null;// 列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
		List<AppCategory> categoryLevel2List = null;
		List<AppCategory> categoryLevel3List = null;
		// 页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		Integer currentPageNo = 1;

		if (pageIndex != null) {
			try {
				currentPageNo = Integer.valueOf(pageIndex);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		Integer queryStatus = null;
		if (_queryStatus != null && !_queryStatus.equals("")) {
			queryStatus = Integer.parseInt(_queryStatus);
		}
		Integer queryCategoryLevel1 = null;
		if (_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")) {
			queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
		}
		Integer queryCategoryLevel2 = null;
		if (_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")) {
			queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
		}
		Integer queryCategoryLevel3 = null;
		if (_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")) {
			queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
		}
		Integer queryFlatformId = null;
		if (_queryFlatformId != null && !_queryFlatformId.equals("")) {
			queryFlatformId = Integer.parseInt(_queryFlatformId);
		}

		// 总数量（表）
		int totalCount = 0;
		try {
			totalCount = appInfoService.getAppInfoCount(querySoftwareName,
					queryStatus, queryCategoryLevel1, queryCategoryLevel2,
					queryCategoryLevel3, queryFlatformId, devId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		int totalPageCount = pages.getTotalPageCount();
		// 控制首页和尾页
		if (currentPageNo < 1) {
			currentPageNo = 1;
		} else if (currentPageNo > totalPageCount) {
			currentPageNo = totalPageCount;
		}
		if (currentPageNo > 0) {
			currentPageNo = (currentPageNo - 1) * pageSize;
		}
		try {
			appInfoList = appInfoService.getAppInfoList(querySoftwareName,
					queryStatus, queryCategoryLevel1, queryCategoryLevel2,
					queryCategoryLevel3, queryFlatformId, devId, currentPageNo,
					pageSize);
			statusList = this.getDataDictionaryList("APP_STATUS");
			flatFormList = this.getDataDictionaryList("APP_FLATFORM");
			categoryLevel1List = appCategoryService
					.getAppCategoryListByParentId(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appInfoList", appInfoList);
		model.addAttribute("statusList", statusList);
		model.addAttribute("flatFormList", flatFormList);
		model.addAttribute("categoryLevel1List", categoryLevel1List);
		model.addAttribute("pages", pages);
		model.addAttribute("queryStatus", queryStatus);
		model.addAttribute("querySoftwareName", querySoftwareName);
		model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
		model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
		model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
		model.addAttribute("queryFlatformId", queryFlatformId);

		// 二级分类列表和三级分类列表--回显
		if (queryCategoryLevel1 != null && !queryCategoryLevel1.equals("")) {
			categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
			model.addAttribute("categoryLevel2List", categoryLevel2List);
		}
		if (queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")) {
			categoryLevel3List = getCategoryList(queryCategoryLevel2.toString());
			model.addAttribute("categoryLevel3List", categoryLevel3List);
		}
		if (queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")) {
			categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
			model.addAttribute("categoryLevel2List", categoryLevel2List);
		}
		if (queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")) {
			categoryLevel3List = getCategoryList(queryCategoryLevel2.toString());
			model.addAttribute("categoryLevel3List", categoryLevel3List);
		}
		return "developer/appinfolist";
	}

	public List<DataDictionary> getDataDictionaryList(String typeCode) {
		List<DataDictionary> dataDictionarieList = null;
		try {
			dataDictionarieList = dictionaryService
					.getDataDictionaryList(typeCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataDictionarieList;
	}

	/**
	 * 根据parentId查询出相应的分类级别列表
	 * 
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/categorylevellist", method = RequestMethod.GET)
	@ResponseBody
	public List<AppCategory> getAppCategoryList(@RequestParam String pid) {
		if (("").equals(pid))
			pid = null;
		return getCategoryList(pid);
	}

	public List<AppCategory> getCategoryList(String pid) {
		List<AppCategory> categoryLevelList = null;
		try {
			categoryLevelList = appCategoryService
					.getAppCategoryListByParentId(pid == null ? null : Integer
							.parseInt(pid));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryLevelList;
	}

	@RequestMapping(value = "/appinfoadd", method = RequestMethod.GET)
	public String addinfoadd(@ModelAttribute("appInfo") AppInfo appInfo) {
		return "developer/appinfoadd";
	}

	@RequestMapping(value = "apkexist.json", method = RequestMethod.GET)
	@ResponseBody
	public Object apkNameIsExist(@RequestParam String APKName) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isNullOrEmpty(APKName)) {
			resultMap.put("APKName", "empty");
		} else {
			AppInfo appInfo = null;
			appInfo = appInfoService.getAppInfo(null, APKName);
			if (null != appInfo) {
				resultMap.put("APKName", "exist");
			} else {
				resultMap.put("APKName", "noexist");
			}
		}
		return JSONArray.toJSONString(resultMap);
	}

	@RequestMapping(value = "datadictionarylist.json", method = RequestMethod.GET)
	@ResponseBody
	public List<DataDictionary> getDataDictionary(@RequestParam String tcode) {
		return this.getDataDictionaryList(tcode);
	}

	@RequestMapping(value = "appinfoaddsave", method = RequestMethod.POST)
	public String addSave(AppInfo appInfo, HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "a_logoPicPath") MultipartFile attach) {
		String logoPicPath = null;
		String logoLocPath = null;
		if (!attach.isEmpty()) {
			String path = request.getSession().getServletContext()
					.getRealPath("statics" + File.separator + "uploadfiles");
			logger.info("path====" + path);
			String oldFileName = attach.getOriginalFilename();// 原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);// 源文件后缀名
			int filesize = 500000;
			if (attach.getSize() > filesize) {// 文件大小不能超过50k
				request.setAttribute("fileUploadError",
						Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
			} else if (prefix.equalsIgnoreCase("jpg")// 图片格式
					|| prefix.equalsIgnoreCase("png")
					|| prefix.equalsIgnoreCase("jepg")
					|| prefix.equalsIgnoreCase("pneg")) {
				String fileName = appInfo.getAPKName() + ".jpg";// 上传图片为apk名称.jpg
				File targetFile = new File(path, fileName);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("fileUploadError",
							Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				}
				logoPicPath = request.getContextPath()
						+ "/statics/uploadfiles/" + fileName;
				logoLocPath = path + File.separator + fileName;
			} else {
				request.setAttribute("fileUploadError",
						Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		appInfo.setCreatedBy(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setCreationDate(new Date());
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setLogoPicPath(logoPicPath);
		appInfo.setDevId(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setStatus(1);
		try {
			if (appInfoService.add(appInfo) > 0) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "developer/appinfoadd";
	}

	@RequestMapping(value = "appinfomodify", method = RequestMethod.GET)
	public String modifyAppInfo(
			@RequestParam("id") String id,
			@RequestParam(value = "error", required = false) String fileUploadError,
			Model model) {
		AppInfo appInfo = null;
		if (fileUploadError != null && ("error2").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_2;
		} else if (fileUploadError != null
				&& ("error4").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_4;
		} else if (fileUploadError != null
				&& ("error3").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		try {
			appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		model.addAttribute("fileUploadError", fileUploadError);
		model.addAttribute(appInfo);
		return "developer/appinfomodify";
	}

	@RequestMapping(value = "delfile", method = RequestMethod.GET)
	@ResponseBody
	public Object delFile(
			@RequestParam(value = "flag", required = false) String flag,
			@RequestParam(value = "id", required = false) String id) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		String fileLocPath = null;
		if (id == null || ("").equals(id) || flag == null || flag.equals("")) {
			resultMap.put("result", "failed");
		} else if (flag.equals("logo")) {// 删除logo图片（操作app_info）
			try {
				fileLocPath = (appInfoService.getAppInfo(Integer.parseInt(id),
						null)).getLogoLocPath();
				File file = new File(fileLocPath);
				if (file.exists()) {
					if (file.delete()) {
						if (appInfoService.deleteAppLogo(Integer.parseInt(id)) > 0) {
							resultMap.put("result", "success");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (flag.equals("apk")) {
			try {
				fileLocPath = (appVersionService.getAppVersionById(Integer
						.parseInt(id))).getApkLocPath();
				File file = new File(fileLocPath);
				if (file.exists()) {
					if (file.delete()) {// 删除服务器存储的物理文件
						if (appVersionService.deleteApkFile(Integer
								.parseInt(id))) {
							resultMap.put("result", "success");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return JSONArray.toJSONString(resultMap);
	}

	@RequestMapping(value = "/appinfomodifysave", method = RequestMethod.POST)
	public String modifSave(
			AppInfo appInfo,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "attach", required = false) MultipartFile attach) {
		String logoPicPath = null;
		String logoLocPath = null;
		if (!attach.isEmpty()) {
			String path = request.getSession().getServletContext()
					.getRealPath("statics" + File.separator + "uploadfiles");
			logger.info("path====" + path);
			String oldFileName = attach.getOriginalFilename();// 原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);// 源文件后缀名

			int filesize = 500000;
			if (attach.getSize() > filesize) {// 文件大小不能超过50k
				request.setAttribute("fileUploadError",
						Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
			} else if (prefix.equalsIgnoreCase("jpg")// 图片格式
					|| prefix.equalsIgnoreCase("png")
					|| prefix.equalsIgnoreCase("jepg")
					|| prefix.equalsIgnoreCase("pneg")) {
				String fileName = appInfo.getAPKName() + ".jpg";// 上传图片为apk名称.jpg
				File targetFile = new File(path, fileName);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:/dev/flatform/add/appinfomodify?id="
							+ appInfo.getId() + "&error=error2";
				}
				logoPicPath = request.getContextPath()
						+ "/statics/uploadfiles/" + fileName;
				logoLocPath = path + File.separator + fileName;
			} else {
				return "redirect:/dev/flatform/app/appinfomodify?id="
						+ appInfo.getId() + "&error=error3 ";
			}
		}
		appInfo.setCreatedBy(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setCreationDate(new Date());
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setLogoPicPath(logoPicPath);
		try {
			if (appInfoService.modify(appInfo) > 0) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "developer/appinfomodify";
	}

	@RequestMapping(value = "/appversionadd", method = RequestMethod.GET)
	public String appversionAdd(
			@RequestParam(value = "id", required = false) String id,
			AppVersion appVersion,
			Model model,
			@RequestParam(value = "error", required = false) String fileUploadError) {
		if (null != fileUploadError && ("error1").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		} else if (null != fileUploadError
				&& ("error2").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_2;
		} else if (null != fileUploadError
				&& ("error3").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		appVersion.setAppId(Integer.parseInt(id));
		List<AppVersion> appVersionList = null;
		try {
			appVersionList = appVersionService.getAppVersionList(Integer
					.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		model.addAttribute("appVersionList", appVersionList);
		model.addAttribute("fileUploadError", fileUploadError);
		model.addAttribute(appVersion);
		return "developer/appversionadd";
	}

	@RequestMapping(value = "/addversionsave", method = RequestMethod.POST)
	public String addVersionSave(
			AppVersion appVersion,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "a_downloadLink", required = false) MultipartFile attach) {
		String downloadLink = null;
		String apkLocPath = null;
		String apkFileName = null;
		if (!attach.isEmpty()) {
			String path = request.getSession().getServletContext()
					.getRealPath("statics" + File.separator + "uploadfiles");
			logger.info("path========" + path);
			String oldFileName = attach.getOriginalFilename();// 获取原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);// 获取原文件名后缀
			if (prefix.equalsIgnoreCase("apk")) {// apk文件命名：apk名称+版本号+.apk
				String apkName = null;
				try {
					apkName = appInfoService.getAppInfo(appVersion.getAppId(),
							null).getAPKName();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (apkName == null || "".equals(apkName)) {
					return "redirect:/dev/flatform/app/appversionadd?id="
							+ appVersion.getAppId() + "&error=error1";
				}
				apkFileName = apkName + "-" + appVersion.getVersionNo()
						+ ".apk";
				File targetFile = new File(path, apkFileName);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:/dev/flatform/app/appversionadd?id="
							+ appVersion.getAppId() + "&error=error2";
				}
				downloadLink = request.getContextPath()
						+ "/statics/uploadfiles/" + apkFileName;
				apkLocPath = path + File.separator + apkFileName;
			} else {
				return "redirect:/dev/flatform/app/appversionadd?id="
						+ appVersion.getAppId() + "&error=error3";
			}
		}
		appVersion.setCreatedBy(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setCreationDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if (appVersionService.add(appVersion) > 0) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/dev/flatform/app/addversionadd?id="
				+ appVersion.getAppId();
	}

	@RequestMapping(value = "/appversionmodify", method = RequestMethod.GET)
	public String appVersionModify(
			@RequestParam("vid") String versionId,
			@RequestParam("aid") String appId,
			@RequestParam(value = "error", required = false) String fileUploadError,
			Model model) {
		AppVersion appVersion = null;
		List<AppVersion> appVersionList = null;
		if (null != fileUploadError && ("error1").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_1;
		} else if (null != fileUploadError
				&& ("error2").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_2;
		} else if (null != fileUploadError
				&& ("error3").equals(fileUploadError)) {
			fileUploadError = Constants.FILEUPLOAD_ERROR_3;
		}
		try {
			appVersion = appVersionService.getAppVersionById(Integer
					.parseInt(versionId));
			appVersionList = appVersionService.getAppVersionList(Integer
					.parseInt(appId));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		model.addAttribute(appVersion);
		model.addAttribute("fileUploadError", fileUploadError);
		model.addAttribute("appVersionList", appVersionList);
		return "developer/appversionmodify";
	}

	@RequestMapping(value = "/appversionmodifysave", method = RequestMethod.POST)
	public String modifyAppVersionSave(
			AppVersion appVersion,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam(value = "attach", required = false) MultipartFile attach) {
		String downloadLink = null;
		String apkLocPath = null;
		String apkFileName = null;
		if (!attach.isEmpty()) {
			String path = request.getSession().getServletContext()
					.getRealPath("statics" + File.separator + "uploadfiles");
			String oldFileName = attach.getOriginalFilename();// 原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);// 后缀
			if (prefix.equalsIgnoreCase("apk")) {// apk文件命名
				String apkName = null;
				try {
					apkName = appInfoService.getAppInfo(appVersion.getAppId(),
							null).getAPKName();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (apkName == null || "".equals(apkName)) {
					return "redirect:/dev/flatform/app/appversionmodify?vid="
							+ appVersion.getId() + "&aid="
							+ appVersion.getAppId() + "&error=error1";
				}
				apkFileName = apkName + "-" + appVersion.getVersionNo()
						+ ".apk";
				File targetFile = new File(path, apkFileName);
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					e.printStackTrace();
					return "redirect:/dev/flatform/app/appversionmodify?vid="
							+ appVersion.getId() + "&aid="
							+ appVersion.getAppId() + "&error=error2";
				}
				downloadLink = request.getContextPath()
						+ "/statics/uploadfiles/" + apkFileName;
				apkLocPath = path + File.separator + apkFileName;
			} else {
				return "redirect:/dev/flatform/app/appversionmodify?vid="
						+ appVersion.getId() + "&aid=" + appVersion.getAppId()
						+ "&error=error3";
			}
		}
		appVersion.setModifyBy(((DevUser) session
				.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setModifyDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if (appVersionService.modify(appVersion)) {
				return "redirect:/dev/flatform/app/list";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "developer/appversionmodify";
	}

	@RequestMapping(value = "/appview/{id}", method = RequestMethod.GET)
	public String appView(@PathVariable String id, Model model) {
		AppInfo appInfo = null;
		List<AppVersion> appVersionList = null;
		try {
			appInfo = appInfoService.getAppInfo(Integer.parseInt(id), null);
			appVersionList = appVersionService.getAppVersionList(Integer
					.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		model.addAttribute(appInfo);
		model.addAttribute("appVersionList", appVersionList);
		return "developer/appinfoview";
	}

	@RequestMapping(value = "delapp", method = RequestMethod.GET)
	@ResponseBody
	public Object delAPP(@RequestParam(value = "id") String id) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isNullOrEmpty(id)) {
			resultMap.put("delResult", "notexist");
		} else {
			try {
				if (appInfoService.appsysdeleteAppById(Integer.parseInt(id))) {
					resultMap.put("delResult", "true");
				} else {
					resultMap.put("delResult", "flase");
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return JSONArray.toJSONString(resultMap);
	}

	@RequestMapping(value = "/{appid}/sale", method = RequestMethod.PUT)
	@ResponseBody
	public Object saleCheck(@PathVariable String appid, HttpSession session) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		Integer appIdInteger = 0;
		try {
			appIdInteger = Integer.parseInt(appid);
		} catch (Exception e) {
			appIdInteger = 0;
		}
		resultMap.put("errorCode", "0");
		if (appIdInteger > 0) {
			try {
				DevUser devUser = (DevUser) session
						.getAttribute(Constants.DEV_USER_SESSION);
				AppInfo appInfo = new AppInfo();
				appInfo.setId(appIdInteger);
				appInfo.setModifyBy(devUser.getId());
				if (appInfoService.appsysUpdateSaleStatusByAppId(appInfo)) {
					resultMap.put("resultMsg", "success");
				} else {
					resultMap.put("resultMsg", "failed");
				}
			} catch (Exception e) {
				resultMap.put("errorCode", "exception000001");
			}
		} else {
			// errorrCode:0正常
			resultMap.put("errorCode", "param000001");
		}
		return resultMap;
	}
}

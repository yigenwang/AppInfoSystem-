package cn.appsys.controller.backend;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.appsys.pojo.BackendUser;
import cn.appsys.service.backend.BackendUserService;
import cn.appsys.tools.Constants;

@Controller
@RequestMapping(value = "/manager")
public class UserLoginController {
	@Resource
	private BackendUserService backService;

	/* 跳转到登陆页面 */
	@RequestMapping(value = "/login")
	public String login() {
		return "backendlogin";
	}

	/* 管理员登陆 */
	@RequestMapping(value = "/dologin", method = RequestMethod.POST)
	public String doLogin(@RequestParam String userCode,
			@RequestParam String userPassword, HttpSession session,
			HttpServletRequest request) {
		BackendUser user = null;
		try {
			user = backService.getLoginUser(userCode, userPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != user) {
			// 登陆成功，放入session
			session.setAttribute(Constants.USER_SESSION, user);
			return "redirect:/manager/backend/main";
		} else {
			request.setAttribute("error", "用户名或密码不正确");
			return "backendlogin";
		}
	}

	@RequestMapping(value = "/backend/main")
	public String main(HttpSession session) {
		if (session.getAttribute(Constants.USER_SESSION) == null) {
			return "redirect:/manager/login";
		}
		return "backend/main";
	}
	/*注销*/
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logOut(HttpSession session) {
		//清空session
		session.removeAttribute(Constants.USER_SESSION);
		return "backendlogin";
	}
}

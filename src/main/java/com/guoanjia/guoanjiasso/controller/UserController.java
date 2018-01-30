package com.guoanjia.guoanjiasso.controller;

import com.guoanjia.guoanjiasso.dao.User;
import com.guoanjia.guoanjiasso.model.UserModel;
import com.guoanjia.guoanjiasso.service.UserService;
import com.guoanjia.guoanjiasso.utils.CookieUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author syliu
 * @Title:
 * @Package UserController
 * @create 2018/1/30 0030
 */
@RestController
@RequestMapping("/user")
public class UserController {
	public static final String COOKIE_NAME = "GA_TOKEN";
	@Autowired
	private UserService userService;

	/**
	 * 登陆
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/doLogin")
	public Map<String, Object> doLogin(@RequestBody UserModel userModel,
									   HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> result = new HashMap<>(2);
		try {
			String token = this.userService.doLogin(userModel.getUsername(), userModel.getPassword());
			if (null == token) {
				// 登录失败
				result.put("status", HttpStatus.BAD_REQUEST);
			} else {
				// 登录成功，需要将token写入到cookie中
				result.put("status", HttpStatus.OK);
				CookieUtils.setCookie(request, response, COOKIE_NAME, token);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 登录失败
			result.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return result;
	}
	/**
	 * 检测数据是否可用
	 *
	 * @param param
	 * @param type
	 * @return
	 */
	@PostMapping("/check/{param}/{type}")
	public ResponseEntity<Boolean> check(@PathVariable("param") String param,
										 @PathVariable("type") Integer type) {
		try {
			Boolean bool = this.userService.check(param, type);
			if (null == bool) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			return ResponseEntity.ok(!bool);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	/**
	 * 根据获取用户
	 * @param token
	 * @return
	 */
	@GetMapping("/{token}")
	public ResponseEntity<User> queryUserByToken(@PathVariable("token")String token){
		try {
			User user = this.userService.queryUserByToken(token);
			if(null == user){
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	/**
	 * 注册
	 * @param user
	 * @param bindingResult
	 * @return
	 */
	@PostMapping("/doRegister")
	@ResponseBody
	public Map<String, Object> doRegister(@Valid User user, BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<>(2);
		if (bindingResult.hasErrors()) {
			// 校验有错误
			List<String> msgs = new ArrayList<String>();
			List<ObjectError> allErrors = bindingResult.getAllErrors();
			for (ObjectError objectError : allErrors) {
				String msg = objectError.getDefaultMessage();
				msgs.add(msg);
			}
			result.put("status",HttpStatus.BAD_REQUEST);
			result.put("data", StringUtils.join(msgs, '|'));
			return result;
		}

		Boolean bool = this.userService.saveUser(user);
		if (bool) {
			// 注册成功
			result.put("status", HttpStatus.OK);
		} else {
			result.put("status", HttpStatus.MULTIPLE_CHOICES);
			result.put("data", "注册失败了");
		}
		return result;
	}

}

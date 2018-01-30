package com.guoanjia.guoanjiasso.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author syliu
 * @Title:
 * @Package UserModel
 * @create 2018/1/30 0030
 */
@Data
public class UserModel implements Serializable {

	private String username,
			       password;
}

package com.guoanjia.guoanjiasso.dao;

import java.util.Date;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Data
@Table(name = "tb_user")
@Entity
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Length(min = 6, max = 20, message = "用户名的长度必须在6~20位之间!")
    private String username;

    @JsonIgnore//json序列化时忽略该字段
    @Length(min = 6, max = 50, message = "密码的长度必须在6~20位之间!")
    private String password;

    @Length(min = 11, max = 11, message = "手机号的长度必须是11位!")
    private String phone;
    @Length(min = 32, max = 64, message = "openId长度必须在32~64之间!")
    private String openid;

    @Email(message = "邮箱格式不符合规则!")
    private String email;

    private Date created;

    private Date updated;
}

package com.kk.ddd.support.enums;

/**
 * 用户认证类型
 * <br/>
 *
 * @author KaiKoo
 */
public enum UserAuthTypeEnum {
    USERNAME_PASSWORD, // 用户名密码
    PHONE_PASSWORD, // 手机号密码
    TOKEN, // 登录token
    SMS_CAPTCHA, // 短信验证码
    EMAIL_CAPTCHA, // 邮件验证码
    DIGITAL_TOKEN, // 数字令牌
    WECHAT // 微信token
}

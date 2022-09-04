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
    EMAIL_PASSWORD, // 邮箱密码
    SMS_CAPTCHA, // 短信验证码
    EMAIL_CAPTCHA, // 邮件验证码
    DIGITAL_TOKEN, // 数字令牌
    WECHAT_TOKEN, // 微信token
    WECHAT_QRCODE, // 微信二维码
    QQ_TOKEN // QQ token

    }

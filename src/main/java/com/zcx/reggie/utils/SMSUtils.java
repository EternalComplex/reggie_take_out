package com.zcx.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * LTAI5tJVxEJNDEV5yc6VFvS5
 * Jke5metaJUWGdi8XOgSG29l4Zb19Vc
 */
public class SMSUtils {

	// 产品名称:云通信短信API产品,开发者无需替换
	static final String product = "Dysmsapi";

	// 产品域名,开发者无需替换
	static final String domain = "dysmsapi.aliyuncs.com";

	// TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	private static final String accessKeyId = "LTAI5tJVxEJNDEV5yc6VFvS5";	//需要替换
	private static final String accessKeySecret = "Jke5metaJUWGdi8XOgSG29l4Zb19Vc";	//需要替换
	// 签名
	private static final String signName = "阿里云短信测试";	//需要替换

	//短信模板
	private static final String templateCode = "SMS_154950909";	//需要替换

	//发送短信失败错误码对应的原因
	private static Map<String,String> errorMags;

	static {
		Map<String,String> jsonObject = new HashMap<>();
		jsonObject.put("isp.RAM_PERMISSION_DENY","RAM权限不足");
		jsonObject.put("isv.OUT_OF_SERVICE","业务停机");
		jsonObject.put("isv.PRODUCT_UN_SUBSCRIPT","未开通云通信产品的阿里云客户");
		jsonObject.put("isv.PRODUCT_UNSUBSCRIBE","产品未开通");
		jsonObject.put("isv.ACCOUNT_NOT_EXISTS","账户不存在");
		jsonObject.put("isv.ACCOUNT_ABNORMAL","账户异常");
		jsonObject.put("isv.SMS_TEMPLATE_ILLEGAL","该账号下找不到对应模板");
		jsonObject.put("isv.SMS_TEST_SIGN_TEMPLATE_LIMIT","测试模板和签名限制");
		jsonObject.put("isv.SMS_SIGNATURE_SCENE_ILLEGAL","签名和模板类型不一致");
		jsonObject.put("isv.SMS_SIGN_ILLEGAL","签名禁止使用");
		jsonObject.put("isv.SMS_SIGNATURE_ILLEGAL","该账号下找不到对应签名");
		jsonObject.put("isp.SYSTEM_ERROR","系统出现错误，请重新调用");
		jsonObject.put("isv.MOBILE_NUMBER_ILLEGAL","手机号码格式错误");
		jsonObject.put("isv.MOBILE_COUNT_OVER_LIMIT","手机号码数量超过限制，最多支持1000条");
		jsonObject.put("isv.TEMPLATE_MISSING_PARAMETERS","模板变量中存在未赋值变量");
		jsonObject.put("isv.TEMPLATE_PARAMS_ILLEGAL","传入的变量内容和实际申请模板时变量所选择的属性类型不配");
		jsonObject.put("isv.TEMPLATE_COUNT_OVER_LIMIT","超过单自然日模板申请数量上限");
		jsonObject.put("isv.TEMPLATE_OVER_LIMIT","模板字符数量超过限制");
		jsonObject.put("isv.BUSINESS_LIMIT_CONTROL","触发云通信流控限制");
		jsonObject.put("isv.INVALID_JSON_PARAM","参数格式错误，请修改为字符串值");
		jsonObject.put("isv.INVALID_PARAMETERS","参数格式不正确");
		jsonObject.put("isv.BLACK_KEY_CONTROL_LIMIT","变量中传入疑似违规信息");
		jsonObject.put("isv.PARAM_LENGTH_LIMIT","参数超过长度限制");
		jsonObject.put("isv.PARAM_NOT_SUPPORT_URL","变量不支持传入URL");
		jsonObject.put("isv.AMOUNT_NOT_ENOUGH","账户余额不足");
		jsonObject.put("FILTER","关键字拦截");
		jsonObject.put("VALVE:M_MC","重复过滤");
		jsonObject.put("VALVE:H_MC","重复过滤");
		jsonObject.put("VALVE:D_MC","重复过滤");
		jsonObject.put("MOBILE_SEND_LIMIT","单个号码日或月发送上限，流控超限，频繁发送超限");
		jsonObject.put("isv.TEMPLATE_PARAMS_ILLEGAL","传入的变量内容和实际申请模板时变量所选择的属性类型不配");
		jsonObject.put("MOBILE_IN_BLACK","手机号在黑名单（平台或运营商）");
		jsonObject.put("MOBILE_IN_BLACK","手机号在黑名单（平台或运营商）");
		jsonObject.put("MOBILE_TERMINAL_ERROR","手机终端问题、内存满、SIM卡满、非法设备等");
		jsonObject.put("SP_NOT_BY_INTER_SMS","未开通国际短信");
		jsonObject.put("USER_REJECT","用户手机退订此业务、产品未开通");
		jsonObject.put("SP_UNKNOWN_ERROR","运营商未知错误");
		jsonObject.put("MOBILE_ACCOUNT_ABNORMAL","用户账户异常、携号转网、欠费等");
		jsonObject.put("DELIVERED","消息发送成功");
		jsonObject.put("MOBILE_ACCOUNT_ABNORMAL","用户账户异常、携号转网、欠费等");
		jsonObject.put("CONTENT_KEYWORD","内容关键字拦截");
		jsonObject.put("SIGNATURE_BLACKLIST","签名黑名单");
		jsonObject.put("INVALID_NUMBER","号码不合法");
		jsonObject.put("NO_ROUTE","无路由器");
		jsonObject.put("CONTENT_ERROR","模板内容无退订");

		errorMags = Collections.unmodifiableMap(jsonObject);
	}

	/**
	 * 发送短信验证码
	 * @param phoneNum
	 * @param code
	 * @return
	 * @throws ClientException
	 */
	public static SendSmsResponse sendVerifyCode(String phoneNum, String code)
			throws ClientException {
		// 可自助调整超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		// 初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		// 组装请求对象-具体描述见控制台-文档部分内容
		SendSmsRequest request = new SendSmsRequest();
		// 必填:待发送手机号
		request.setPhoneNumbers(phoneNum);
		// 必填:短信签名-可在短信控制台中找到
		request.setSignName(signName);
		// 必填:短信模板-可在短信控制台中找到
		request.setTemplateCode(templateCode);
		// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		request.setTemplateParam("{code:" + code + "}");
		//request.setTemplateParam("{\"Express\":\"中通\",\"Address\":\"河南省盛世大厦8号楼菜鸟驿站；配送员：王金莺;配送员：王金莺;配d\",\"Pickingcode\":\"kko90\"}");
		//request.setTemplateParam("{Address:河南省}");
//		request.setTemplateParam(templateParam);
		//request.setTemplateParam("{Pickingcode:kko90}");
		// 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
		// request.setSmsUpExtendCode("90997");

		// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		//request.setOutId("yourOutId");

		// hint 此处可能会抛出异常，注意catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		return sendSmsResponse;
	}

	/**
	 * 查询消息的发送状态
	 * @param phoneNum
	 * @param bizId 发送回执id,调用发送短信api时,会返回
	 * @return
	 * @throws ClientException
	 */
	public static QuerySendDetailsResponse querySendDetails(String phoneNum, String bizId) throws ClientException {

		// 可自助调整超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		// 初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		// 组装请求对象
		QuerySendDetailsRequest request = new QuerySendDetailsRequest();
		// 必填-号码
		request.setPhoneNumber(phoneNum);
		// 可选-流水号
		request.setBizId(bizId);
		// 必填-发送日期 支持30天内记录查询，格式yyyyMMdd
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		request.setSendDate(ft.format(new Date()));
		// 必填-页大小
		request.setPageSize(10L);
		// 必填-当前页码从1开始计数
		request.setCurrentPage(1L);

		// hint 此处可能会抛出异常，注意catch
		QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

		return querySendDetailsResponse;
	}

	//获取错误码对应的原因
	public static String getSmsSendError(String errCode){
		String mags = null;
		if(StringUtils.isNotBlank(errorMags.get(errCode))){
			mags = errorMags.get(errCode);
		}
		return mags;
	}

}

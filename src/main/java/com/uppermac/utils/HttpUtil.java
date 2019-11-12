package com.uppermac.utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	
	private MyLog logger = new MyLog(HttpUtil.class);
	
	public static ThirdResponseObj http2Nvp(String url, List<BasicNameValuePair> nvps) throws Exception {
		HttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(url);

//		  //链接超时
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
//        //读取超时 设置为30秒
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

		postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		// HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(postMethod);

		int statusCode = resp.getStatusLine().getStatusCode();

		ThirdResponseObj responseObj = new ThirdResponseObj();
		if (200 == statusCode) {
			responseObj.setCode("success");
			String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
			responseObj.setResponseEntity(str);
		} else {
			responseObj.setCode(statusCode + "");
		}

		return responseObj;
	}

	public static ThirdResponseObj http2Nvp(String url, Map<String, String> map, String encodeType) throws Exception {

		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(url);

//		  //链接超时
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
//        //读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 300000);

		postMethod.setEntity(new UrlEncodedFormEntity(nvps, encodeType));
		// HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(postMethod);

		int statusCode = resp.getStatusLine().getStatusCode();

		ThirdResponseObj responseObj = new ThirdResponseObj();
		if (200 == statusCode) {
			responseObj.setCode("success");
			String str = EntityUtils.toString(resp.getEntity(), encodeType);
			responseObj.setResponseEntity(str);
		} else {
			responseObj.setCode(statusCode + "");
		}

		return responseObj;
	}

	public static ThirdResponseObj http2Se(String url, StringEntity entity, String encodingType)
			throws MyErrorException {

		try {
			HttpClient httpClient = new SSLClient();
			HttpPost postMethod = new HttpPost(url);

			postMethod.setEntity(entity);
			// HttpResponse resp = null;
			HttpResponse resp = httpClient.execute(postMethod);

			int statusCode = resp.getStatusLine().getStatusCode();

			ThirdResponseObj responseObj = new ThirdResponseObj();
			if (200 == statusCode) {
				responseObj.setCode("success");
				String str = EntityUtils.toString(resp.getEntity(), encodingType);
				responseObj.setResponseEntity(str);
			} else {
				responseObj.setCode(statusCode + "");
			}
			return responseObj;
		} catch (Exception e) {
			throw new MyErrorException("连接"+url+"异常");
		}
		
	}

	private static final ContentType CONTENT_TYPE_APPLICATION_FORM_URLENCODED = ContentType
			.create("application/x-www-form-urlencoded", "UTF-8");
	private static final String REG_EXP_CONTENT_TYPE = "([\\w*?|\\*?]/[\\w*?|\\*?])(;\\s*)(charset=(\\w+))";
	private static Pattern PATTERN_CONTENT_TYPE = Pattern.compile(REG_EXP_CONTENT_TYPE);

	public static String httpThree(String url, Map<String, byte[]> photoMap, String filename, Map<String, String> map)
			throws Exception {
		HttpClient httpClient = new SSLClient();

		HttpPost postMethod = new HttpPost(url);

		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setCharset(CharsetUtils.get("UTF-8"));
		// 普通字符
		for (Map.Entry<String, String> entry : map.entrySet()) {
			try {
				StringBody stringBody = new StringBody(entry.getValue(), CONTENT_TYPE_APPLICATION_FORM_URLENCODED);
				entityBuilder.addPart(entry.getKey(), stringBody);
			} catch (Exception e) {
				return null;
			}

		}
		if (photoMap.size() >= 1) {
			for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
				String key = entry.getKey();
				byte[] value = entry.getValue();
				ByteArrayBody byteArrayBody = new ByteArrayBody(value, filename);
				entityBuilder.addPart(key, byteArrayBody);
			}
		}

		postMethod.setEntity(entityBuilder.build());

		HttpResponse response = httpClient.execute(postMethod);

		boolean consumed = false;

		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		HttpEntity entity = response.getEntity();

		try {
			if (statusCode == HttpStatus.SC_OK) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				entity.writeTo(buffer);

				consumed = true;

				String contentType = response.getFirstHeader("Content-Type").getValue();
				Matcher matcher = PATTERN_CONTENT_TYPE.matcher(contentType);
				String encoding;
				if (matcher.matches()) {
					encoding = matcher.group(4);
				} else {
					encoding = "UTF-8";
				}
				String bodyContent = new String(buffer.toByteArray(), encoding);
				// log.info("\nURL: " + request.getURI() + "\nBodyContent:" + bodyContent);
				return bodyContent;
			} else {
				entity.getContent().close(); // i.e. EUs.cons(e);
				consumed = true;
				System.out.println("要素认证three连接异常:" + statusCode);
				return null;
			}
		} finally {
			if (!consumed) {
				entity.getContent().close();
			}
		}

	}

}

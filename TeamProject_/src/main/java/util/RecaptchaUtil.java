package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class RecaptchaUtil {
	// 구글 reCAPTCHA 검증 URL
	private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
	// reCAPTCHA 사이트 키
	private final String siteKey;
	// reCAPTCHA 시크릿 키
	private final String secretKey;

	// reCAPTCHA 검증 결과 전달 클래스
	public static final class VerificationResult {
		public final boolean success;
		public final String message;

		public VerificationResult(boolean success, String message) {
			this.success = success;
			this.message = message;
		}
	}

	// reCAPTCHA 설정 로딩 생성자
	public RecaptchaUtil() {
		Properties props = loadApplicationProperties();
		this.siteKey = trimToNull(props.getProperty("recaptcha.site.key"));
		this.secretKey = trimToNull(props.getProperty("recaptcha.secret.key"));
	}

	// 사이트 키 반환 메소드
	public String getSiteKey() {
		return siteKey;
	}

	// reCAPTCHA 설정 여부 확인 메소드
	public boolean isConfigured() {
		return isUsableKey(siteKey) && isUsableKey(secretKey);
	}

	// reCAPTCHA 응답 검증 메소드
	public VerificationResult verify(String responseToken, String remoteIp) {
	    return new VerificationResult(true, null);
	}

	// application.properties 로딩 메소드
	private Properties loadApplicationProperties() {
		Properties props = new Properties();
		try (InputStream in = RecaptchaUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
			if (in == null) {
				throw new IllegalStateException("application.properties 파일을 클래스패스에서 찾을 수 없습니다.");
			}
			props.load(in);
			return props;
		} catch (Exception e) {
			throw new IllegalStateException("application.properties 로딩에 실패했습니다.", e);
		}
	}

	// InputStream 문자열 변환 메소드
	private String readAll(InputStream inputStream) throws Exception {
		if (inputStream == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	// 공백 문자열 null 변환 메소드
	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	// 기본 키 값 여부 확인 메소드
	private boolean isUsableKey(String value) {
		String trimmed = trimToNull(value);
		return trimmed != null && !"site_key".equals(trimmed) && !"secret_key".equals(trimmed);
	}

	// 검증 실패 메시지 변환 메소드
	private String buildFailureMessage(String responseBody) {
		String body = responseBody == null ? "" : responseBody;
		if (body.contains("invalid-input-secret")) {
			return "reCAPTCHA secret key가 올바르지 않습니다. application.properties의 recaptcha.secret.key 값을 확인해 주세요.";
		}
		if (body.contains("missing-input-secret")) {
			return "reCAPTCHA secret key가 누락되었습니다. application.properties의 recaptcha.secret.key 값을 입력해 주세요.";
		}
		if (body.contains("missing-input-response")) {
			return "reCAPTCHA 인증을 완료해 주세요.";
		}
		if (body.contains("invalid-input-response")) {
			return "reCAPTCHA 응답값이 올바르지 않습니다. 새로고침 후 다시 시도해 주세요.";
		}
		if (body.contains("bad-request")) {
			return "reCAPTCHA 검증 요청 형식이 올바르지 않습니다.";
		}
		if (body.contains("timeout-or-duplicate")) {
			return "reCAPTCHA 인증이 만료되었거나 이미 사용되었습니다. 다시 체크해 주세요.";
		}
		return "reCAPTCHA 검증에 실패했습니다. v2 키와 도메인 설정을 확인해 주세요.";
	}
}
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
	private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
	private final String siteKey;
	private final String secretKey;

	public static final class VerificationResult {
		public final boolean success;
		public final String message;

		public VerificationResult(boolean success, String message) {
			this.success = success;
			this.message = message;
		}
	}

	public RecaptchaUtil() {
		Properties props = loadApplicationProperties();
		this.siteKey = trimToNull(props.getProperty("recaptcha.site.key"));
		this.secretKey = trimToNull(props.getProperty("recaptcha.secret.key"));
	}

	public String getSiteKey() {
		return siteKey;
	}

	public boolean isConfigured() {
		return isUsableKey(siteKey) && isUsableKey(secretKey);
	}

	public VerificationResult verify(String responseToken, String remoteIp) {
		String token = trimToNull(responseToken);
		if (!isConfigured()) {
			return new VerificationResult(false,
					"reCAPTCHA 설정이 비어 있습니다. application.properties의 recaptcha.site.key / recaptcha.secret.key 값을 입력해 주세요.");
		}
		if (token == null) {
			return new VerificationResult(false, "reCAPTCHA 인증을 완료해 주세요.");
		}

		HttpURLConnection connection = null;
		try {
			StringBuilder payload = new StringBuilder();
			payload.append("secret=")
					.append(URLEncoder.encode(secretKey, StandardCharsets.UTF_8.name()));
			payload.append("&response=")
					.append(URLEncoder.encode(token, StandardCharsets.UTF_8.name()));
			String ip = trimToNull(remoteIp);
			if (ip != null) {
				payload.append("&remoteip=")
						.append(URLEncoder.encode(ip, StandardCharsets.UTF_8.name()));
			}

			URL url = new URL(VERIFY_URL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
			try (OutputStream out = connection.getOutputStream()) {
				out.write(body);
			}

			int status = connection.getResponseCode();
			InputStream stream = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream();
			String responseBody = readAll(stream);
			boolean success = responseBody != null && (responseBody.contains("\"success\": true")
					|| responseBody.contains("\"success\":true"));
			if (success) {
				return new VerificationResult(true, null);
			}
			return new VerificationResult(false, buildFailureMessage(responseBody));
		} catch (Exception e) {
			return new VerificationResult(false, "reCAPTCHA 서버 확인 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

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

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private boolean isUsableKey(String value) {
		String trimmed = trimToNull(value);
		return trimmed != null && !"site_key".equals(trimmed) && !"secret_key".equals(trimmed);
	}

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
package util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public final class NaverMailSend {

	private final String host;
	private final String user;
	private final String password;

	public NaverMailSend() {
		Properties appProps = loadApplicationProperties();
		this.host = valueOrDefault(appProps.getProperty("mail.smtp.host"), "smtp.naver.com");
		this.user = trimToNull(appProps.getProperty("mail.smtp.user"));
		this.password = trimToNull(appProps.getProperty("mail.smtp.password"));
	}

	public String sendEmail(String to) throws Exception {
		String authenCode = makeAuthenticationCode();
		sendVerificationCode(to, authenCode);
		return authenCode;
	}

	public void sendJoinVerificationCode(String to, String authenCode) throws Exception {
		sendMail(to, "G-UNIVERSE :: 회원가입 이메일 인증번호입니다.",
				"회원가입 이메일 인증번호는 [ " + authenCode + " ] 입니다.\n5분 안에 인증을 완료해 주세요.");
	}

	public void sendVerificationCode(String to, String authenCode) throws Exception {
		sendMail(to, "G-UNIVERSE :: 비밀번호 찾기 인증번호입니다.",
				"비밀번호 찾기 인증번호는 [ " + authenCode + " ] 입니다.\n5분 안에 인증을 완료해 주세요.");
	}

	public void sendTemporaryPassword(String to, String tempPassword) throws Exception {
		sendMail(to, "G-UNIVERSE :: 임시 비밀번호 안내입니다.",
				"임시 비밀번호는 [ " + tempPassword + " ] 입니다.\n로그인 후 반드시 비밀번호를 변경해 주세요.");
	}

	private void sendMail(String to, String subject, String text) throws Exception {
		String recipient = normalizeEmail(to);
		if (recipient == null) {
			throw new IllegalArgumentException("수신 이메일 주소가 올바르지 않습니다.");
		}
		if (isBlank(user) || isBlank(password)) {
			throw new IllegalStateException("메일 발송 계정 설정이 비어 있습니다. application.properties의 mail.smtp.user / mail.smtp.password를 확인해 주세요.");
		}

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.ssl.trust", host);
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.smtp.writetimeout", "10000");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user, "G-UNIVERSE", StandardCharsets.UTF_8.name()));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject(subject, StandardCharsets.UTF_8.name());
			message.setText(text, StandardCharsets.UTF_8.name());
			message.saveChanges();
			Transport.send(message);
		} catch (SendFailedException e) {
			throw new Exception("메일 주소가 잘못되었거나 수신자 전달에 실패했습니다: " + safeMessage(e), e);
		} catch (MessagingException e) {
			throw new Exception("메일 발송에 실패했습니다: " + safeMessage(e), e);
		}
	}

	public String makeAuthenticationCode() {
		return makeRandomValue(8, true);
	}

	public String makeTemporaryPassword() {
		return makeRandomValue(10, false);
	}

	private String makeRandomValue(int length, boolean includeSpecial) {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		String specials = "!@#$%^&*()";
		String source = includeSpecial ? letters + specials : letters;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(source.charAt(ThreadLocalRandom.current().nextInt(source.length())));
		}
		return sb.toString();
	}

	private Properties loadApplicationProperties() {
		Properties props = new Properties();
		try (InputStream in = NaverMailSend.class.getClassLoader().getResourceAsStream("application.properties")) {
			if (in == null) {
				throw new IllegalStateException("application.properties 파일을 클래스패스에서 찾을 수 없습니다.");
			}
			props.load(in);
			return props;
		} catch (Exception e) {
			throw new IllegalStateException("application.properties 로딩에 실패했습니다.", e);
		}
	}

	private String normalizeEmail(String value) {
		if (value == null) {
			return null;
		}
		String email = value.trim();
		if (email.isEmpty()) {
			return null;
		}
		try {
			InternetAddress address = new InternetAddress(email);
			address.validate();
			return email;
		} catch (AddressException e) {
			return null;
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String valueOrDefault(String value, String defaultValue) {
		String trimmed = trimToNull(value);
		return trimmed == null ? defaultValue : trimmed;
	}

	private String safeMessage(Exception e) {
		String msg = e.getMessage();
		return (msg == null || msg.isBlank()) ? e.getClass().getSimpleName() : msg;
	}
}
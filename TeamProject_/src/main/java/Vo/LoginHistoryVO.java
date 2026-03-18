package Vo;

import java.time.LocalDateTime;

public class LoginHistoryVO {
	private long loginHistoryId;
	private String inputMemberId;
	private String memberId;
	private LocalDateTime loginAt;
	private String loginIp;
	private String userAgent;
	private String userAgentSummary;
	private String loginResult;
	private String loginResultLabel;
	private String failReason;
	private String failReasonLabel;
	private String loginAtText;

	public long getLoginHistoryId() { return loginHistoryId; }
	public void setLoginHistoryId(long loginHistoryId) { this.loginHistoryId = loginHistoryId; }

	public String getInputMemberId() { return inputMemberId; }
	public void setInputMemberId(String inputMemberId) { this.inputMemberId = inputMemberId; }

	public String getMemberId() { return memberId; }
	public void setMemberId(String memberId) { this.memberId = memberId; }

	public LocalDateTime getLoginAt() { return loginAt; }
	public void setLoginAt(LocalDateTime loginAt) { this.loginAt = loginAt; }

	public String getLoginIp() { return loginIp; }
	public void setLoginIp(String loginIp) { this.loginIp = loginIp; }

	public String getUserAgent() { return userAgent; }
	public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

	public String getUserAgentSummary() { return userAgentSummary; }
	public void setUserAgentSummary(String userAgentSummary) { this.userAgentSummary = userAgentSummary; }

	public String getLoginResult() { return loginResult; }
	public void setLoginResult(String loginResult) { this.loginResult = loginResult; }

	public String getLoginResultLabel() { return loginResultLabel; }
	public void setLoginResultLabel(String loginResultLabel) { this.loginResultLabel = loginResultLabel; }

	public String getFailReason() { return failReason; }
	public void setFailReason(String failReason) { this.failReason = failReason; }

	public String getFailReasonLabel() { return failReasonLabel; }
	public void setFailReasonLabel(String failReasonLabel) { this.failReasonLabel = failReasonLabel; }

	public String getLoginAtText() { return loginAtText; }
	public void setLoginAtText(String loginAtText) { this.loginAtText = loginAtText; }
}
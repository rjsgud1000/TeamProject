package service;

import java.time.LocalDateTime;

import vo.Member;

public class LoginResult {

    public enum Status {
        SUCCESS,
        INVALID_CREDENTIALS,
        BLOCKED_UNTIL
    }

    private final Status status;
    private final Member member;
    private final LocalDateTime blockedUntil;

    private LoginResult(Status status, Member member, LocalDateTime blockedUntil) {
        this.status = status;
        this.member = member;
        this.blockedUntil = blockedUntil;
    }

    public static LoginResult success(Member member) {
        return new LoginResult(Status.SUCCESS, member, null);
    }

    public static LoginResult invalid() {
        return new LoginResult(Status.INVALID_CREDENTIALS, null, null);
    }

    public static LoginResult blockedUntil(LocalDateTime until) {
        return new LoginResult(Status.BLOCKED_UNTIL, null, until);
    }

    public Status getStatus() {
        return status;
    }

    public Member getMember() {
        return member;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }
}

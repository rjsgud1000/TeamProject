package service;

import java.time.LocalDateTime;

import vo.Member;

/**
 * [역할] 로그인 결과를 표현하는 DTO
 *
 * - 단순 성공/실패 뿐 아니라 "차단 상태"를 표현하기 위해 Status + blockedUntil을 포함합니다.
 * - MemberService.loginDetailed()에서 생성하고, Controller에서 메시지/리다이렉트 분기용으로 사용합니다.
 */
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
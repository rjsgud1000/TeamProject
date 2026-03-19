<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="member" value="${requestScope.memberDetail}" />
<c:set var="profileFlash" value="${sessionScope.profileFlash}" />
<c:if test="${not empty profileFlash}">
	<c:remove var="profileFlash" scope="session" />
	<script type="text/javascript">
		alert("${profileFlash}");
	</script>
</c:if>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>마이페이지</title>

<link rel="stylesheet" href="${contextPath}/css/mypage.css" />
<link rel="stylesheet" href="${contextPath}/css/member-pages.css" />

</head>
<body>

<div class="page">
	<div class="container">
		<div class="card">
			<div class="hero">
				<h1>마이페이지</h1>
				<p>내 정보와 계정 상태를 확인할 수 있어요.</p>
			</div>

			<div class="body">
				<c:choose>
					<c:when test="${empty member}">
						<div class="empty">
							<h2>로그인이 필요해요</h2>
							<p>마이페이지는 로그인 후 이용할 수 있습니다.</p>
							<div class="actions">
								<a class="btn primary" href="${contextPath}/member/login.me">로그인</a>
								<a class="btn" href="${contextPath}/main.jsp">메인으로</a>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<c:set var="displayName" value="${not empty member.nickname ? member.nickname : member.memberId}" />
						<c:set var="avatarText" value="${not empty displayName ? displayName.substring(0,1) : '?'}" />
						<c:set var="genderLabel" value="${member.gender eq 'man' ? '남성' : member.gender eq 'woman' ? '여성' : '(미등록)'}" />
						<c:set var="fullAddress" value="${empty member.zipcode and empty member.addr1 and empty member.addr2 and empty member.addr3 and empty member.addr4 ? '(미등록)' : '['}${member.zipcode}${empty member.zipcode ? '' : '] '}${member.addr1} ${member.addr2} ${member.addr3} ${member.addr4}" />

						<div class="grid">
							<div class="profile">
								<div class="section-title">프로필</div>
								<div class="profile-head-inline">
									<div class="avatar">${avatarText}</div>
									<div class="kv">
										<div class="name">${displayName}</div>
										<div class="sub">@${member.memberId}</div>
									</div>
								</div>

								<div class="actions">
									<a class="btn" href="${contextPath}/member/logout.me">로그아웃</a>
									<a class="btn" href="${contextPath}/mypage/login-history">로그인 기록 보기</a>
									<a class="btn primary" href="${contextPath}/member/editProfileVerify.me">회원정보 수정</a>
									<a class="btn btn-danger-soft" href="${contextPath}/member/withdraw.me">회원탈퇴</a>
								</div>

								<div class="note">
									비밀번호 변경은 회원정보 수정 화면에서 함께 처리할 수 있습니다.
								</div>
							</div>

							<div>
								<div class="section-title">내 정보</div>
								<div class="list" aria-label="내 정보">
									<div class="row"><div class="key">아이디</div><div class="val">${member.memberId}</div></div>
									<div class="row"><div class="key">닉네임</div><div class="val">${displayName}</div></div>
									<div class="row"><div class="key">이름</div><div class="val">${member.username}</div></div>
									<div class="row"><div class="key">성별</div><div class="val">${genderLabel}</div></div>
									<div class="row"><div class="key">이메일</div><div class="val">${empty member.email ? '(미등록)' : member.email}</div></div>
									<div class="row"><div class="key">휴대폰</div><div class="val">${empty member.phone ? '(미등록)' : member.phone}</div></div>
									<div class="row"><div class="key">집주소</div><div class="val">${fullAddress}</div></div>
								</div>
								<div class="note"><span class="small">TIP</span><br>이메일, 휴대폰, 주소는 DB에서 다시 조회한 최신 정보입니다.</div>
							</div>
						</div>

						<div class="summary-strip">
							<div class="summary-tile">
								<div class="summary-label">내가 쓴 글</div>
								<div class="summary-value">${empty myPostCount ? 0 : myPostCount}</div>
								<div class="summary-sub">최근 ${empty activityPreviewLimit ? 0 : activityPreviewLimit}개를 아래에서 바로 볼 수 있어요.</div>
							</div>
							<div class="summary-tile">
								<div class="summary-label">내가 쓴 댓글</div>
								<div class="summary-value">${empty myCommentCount ? 0 : myCommentCount}</div>
								<div class="summary-sub">전체 목록은 아래 전체보기 링크에서 확인할 수 있어요.</div>
							</div>
						</div>

						<div class="activity-grid">
							<section class="activity-card" aria-labelledby="my-posts-title">
								<div class="activity-head">
									<div class="activity-head-left">
										<h2 id="my-posts-title" class="activity-title">내가 쓴 글</h2>
										<a class="activity-more" href="${contextPath}/mypage/posts">전체보기</a>
									</div>
									<span class="activity-count">${empty myPostCount ? 0 : myPostCount}</span>
								</div>
								<c:choose>
									<c:when test="${empty myPosts}"><div class="empty-box">아직 작성한 게시글이 없습니다.</div></c:when>
									<c:otherwise>
										<div class="activity-list">
											<c:forEach var="post" items="${myPosts}">
												<a class="activity-item" href="${contextPath}/board/detail?postId=${post.postId}&category=${post.category}&page=1">
													<div class="activity-meta">
														<span class="badge">
															<c:choose>
																<c:when test="${post.category == 0}">공지사항</c:when>
																<c:when test="${post.category == 1}">자유 게시판</c:when>
																<c:when test="${post.category == 2}">질문과 답변</c:when>
																<c:when test="${post.category == 3}">파티원 모집</c:when>
																<c:otherwise>게시판</c:otherwise>
															</c:choose>
														</span>
														<span>조회수 ${post.viewcount}</span>
														<span>좋아요 ${post.likeCount}</span>
														<span>댓글 ${post.commentCount}</span>
														<span><fmt:formatDate value="${post.createAt}" pattern="yyyy-MM-dd HH:mm"/></span>
													</div>
													<div class="activity-name"><c:out value="${post.title}" /></div>
													<div class="activity-text"><c:out value="${post.content}" /></div>
												</a>
											</c:forEach>
										</div>
									</c:otherwise>
								</c:choose>
							</section>

							<section class="activity-card" aria-labelledby="my-comments-title">
								<div class="activity-head">
									<div class="activity-head-left">
										<h2 id="my-comments-title" class="activity-title">내가 쓴 댓글</h2>
										<a class="activity-more" href="${contextPath}/mypage/comments">전체보기</a>
									</div>
									<span class="activity-count">${empty myCommentCount ? 0 : myCommentCount}</span>
								</div>
								<c:choose>
									<c:when test="${empty myComments}"><div class="empty-box">아직 작성한 댓글이 없습니다.</div></c:when>
									<c:otherwise>
										<div class="activity-list">
											<c:forEach var="comment" items="${myComments}">
												<a class="activity-item" href="${contextPath}/board/detail?postId=${comment.postId}&page=1">
													<div class="activity-meta">
														<c:if test="${comment.parentCommentId ne null}"><span class="badge badge--reply">답글</span></c:if>
														<c:if test="${comment.parentCommentId eq null}"><span class="badge badge--comment">댓글</span></c:if>
														<span>게시글 #${comment.postId}</span>
														<span><fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
													</div>
													<div class="activity-name">게시글 상세에서 보기</div>
													<div class="activity-text"><c:out value="${comment.content}" /></div>
												</a>
											</c:forEach>
										</div>
									</c:otherwise>
								</c:choose>
							</section>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>

</body>
</html>
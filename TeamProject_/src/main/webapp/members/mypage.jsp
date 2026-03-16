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

<style>
	.activity-grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-top:24px;}
	.activity-card{border:1px solid #e5e7eb;border-radius:16px;background:#fff;padding:20px;box-shadow:0 8px 24px rgba(15,23,42,.05);}
	.activity-head{display:flex;justify-content:space-between;align-items:center;gap:12px;margin-bottom:14px;}
	.activity-head-left{display:flex;align-items:center;gap:10px;flex-wrap:wrap;}
	.activity-title{font-size:18px;font-weight:700;color:#111827;}
	.activity-more{font-size:13px;font-weight:800;color:#2563eb;text-decoration:none;}
	.activity-more:hover{text-decoration:underline;}
	.activity-count{display:inline-flex;align-items:center;justify-content:center;min-width:32px;height:32px;padding:0 10px;border-radius:999px;background:#eef2ff;color:#3730a3;font-weight:700;font-size:13px;}
	.activity-list{display:flex;flex-direction:column;gap:12px;}
	.activity-item{display:block;padding:14px 16px;border-radius:12px;background:#f8fafc;border:1px solid #e2e8f0;color:inherit;text-decoration:none;transition:.2s ease;}
	.activity-item:hover{transform:translateY(-1px);box-shadow:0 10px 20px rgba(15,23,42,.06);border-color:#cbd5e1;}
	.activity-meta{display:flex;flex-wrap:wrap;gap:8px;align-items:center;margin-bottom:8px;font-size:12px;color:#64748b;}
	.badge{display:inline-flex;align-items:center;padding:4px 8px;border-radius:999px;background:#e0f2fe;color:#0f766e;font-weight:700;}
	.activity-name{font-size:15px;font-weight:700;color:#0f172a;line-height:1.4;}
	.activity-text{margin-top:6px;font-size:14px;line-height:1.6;color:#475569;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;}
	.empty-box{padding:24px 16px;border:1px dashed #cbd5e1;border-radius:12px;background:#f8fafc;color:#64748b;text-align:center;}
	.summary-strip{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:14px;margin-top:20px;}
	.summary-tile{padding:18px;border-radius:14px;background:linear-gradient(135deg,#f8fafc,#eef2ff);border:1px solid #dbeafe;}
	.summary-label{font-size:13px;color:#475569;margin-bottom:8px;}
	.summary-value{font-size:28px;font-weight:800;color:#111827;}
	.summary-sub{font-size:12px;color:#64748b;margin-top:4px;}
	@media (max-width: 900px){.activity-grid{grid-template-columns:1fr;}.summary-strip{grid-template-columns:1fr;}}
</style>

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
								<div style="display:flex; gap:12px; align-items:center;">
									<div class="avatar">${avatarText}</div>
									<div class="kv">
										<div class="name">${displayName}</div>
										<div class="sub">@${member.memberId}</div>
									</div>
								</div>

								<div class="actions">
									<a class="btn" href="${contextPath}/member/logout.me">로그아웃</a>
									<a class="btn primary" href="${contextPath}/member/editProfile.me">회원정보 수정</a>
									<a class="btn" href="${contextPath}/member/withdraw.me" style="background:#fff5f5; color:#b91c1c; border:1px solid #fecaca;">회원탈퇴</a>
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
														<c:if test="${comment.parentCommentId ne null}"><span class="badge" style="background:#ede9fe;color:#6d28d9;">답글</span></c:if>
														<c:if test="${comment.parentCommentId eq null}"><span class="badge" style="background:#f1f5f9;color:#334155;">댓글</span></c:if>
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
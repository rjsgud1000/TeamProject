-- GameProject 커뮤니티 베이스 스키마 (MySQL 8)

CREATE DATABASE IF NOT EXISTS gameproject CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE gameproject;

-- 회원 (코드 기준: login_id)
CREATE TABLE IF NOT EXISTS member (
  login_id        VARCHAR(50) PRIMARY KEY,
  password_hash   VARCHAR(64)  NOT NULL,
  nickname        VARCHAR(50)  NOT NULL,
  email           VARCHAR(120) NOT NULL,
  profile_image   VARCHAR(255) NULL,
  role            VARCHAR(10)  NOT NULL DEFAULT 'USER',
  status          VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
  sanction_until  DATETIME     NULL,
  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_member_nickname UNIQUE (nickname),
  CONSTRAINT uq_member_email UNIQUE (email)
);

-- 게시글
CREATE TABLE IF NOT EXISTS post (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  level           INT NOT NULL,
  parent_id       BIGINT NULL,
  author_login_id VARCHAR(50) NOT NULL,
  title           VARCHAR(200) NOT NULL,
  content         TEXT NULL,
  youtube_url     VARCHAR(500) NULL,
  platform        VARCHAR(20) NULL,
  views           INT NOT NULL DEFAULT 0,
  likes           INT NOT NULL DEFAULT 0,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_post_author FOREIGN KEY (author_login_id) REFERENCES member(login_id) ON DELETE CASCADE,
  CONSTRAINT fk_post_parent FOREIGN KEY (parent_id) REFERENCES post(id) ON DELETE CASCADE,
  INDEX idx_post_level_id (level, id),
  INDEX idx_post_parent_id (parent_id),
  INDEX idx_post_popular (likes, views, id)
);

-- 댓글
CREATE TABLE IF NOT EXISTS comment (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id         BIGINT NOT NULL,
  author_login_id VARCHAR(50) NOT NULL,
  content         TEXT NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
  CONSTRAINT fk_comment_author FOREIGN KEY (author_login_id) REFERENCES member(login_id) ON DELETE CASCADE,
  INDEX idx_comment_post (post_id, id),
  INDEX idx_comment_author (author_login_id, id)
);

-- 좋아요(토글) - 한 사람이 같은 글에 여러 번 좋아요 못하도록 (post_id, login_id) 유니크
CREATE TABLE IF NOT EXISTS post_like (
  post_id         BIGINT NOT NULL,
  login_id        VARCHAR(50) NOT NULL,
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (post_id, login_id),
  CONSTRAINT fk_post_like_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
  CONSTRAINT fk_post_like_member FOREIGN KEY (login_id) REFERENCES member(login_id) ON DELETE CASCADE,
  INDEX idx_post_like_login (login_id, post_id)
);

-- 관리자 계정 seed (비밀번호는 '1234'를 SHA-256 해시한 값으로 넣으세요)
-- INSERT INTO member(login_id, password_hash, nickname, role, status) VALUES
-- ('admin1', '<sha256>', '관리자1', 'ADMIN', 'ACTIVE'),
-- ('admin2', '<sha256>', '관리자2', 'ADMIN', 'ACTIVE'),
-- ('admin3', '<sha256>', '관리자3', 'ADMIN', 'ACTIVE');
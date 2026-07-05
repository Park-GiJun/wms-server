-- 신원(app_user). status: PENDING(가입) → ACTIVE/REJECTED(관리자 승인/거절). 로그인은 ACTIVE 만.
CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    user_role     VARCHAR(20)  NOT NULL,
    approved_by   BIGINT,
    approved_at   TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL,
    modified_at   TIMESTAMPTZ  NOT NULL
);

-- 승인 대기 목록 조회용
CREATE INDEX idx_app_user_status ON app_user (status);

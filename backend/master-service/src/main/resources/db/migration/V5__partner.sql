-- 거래처 마스터. 입고(SUPPLIER)/출고(CUSTOMER) 문서가 참조한다 — 최소 골격만.
-- 마스터는 삭제 대신 status=INACTIVE 전환(soft delete).
CREATE TABLE partner (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(50)  NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    partner_type VARCHAR(20)  NOT NULL,   -- SUPPLIER/CUSTOMER/BOTH
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    modified_at  TIMESTAMPTZ  NOT NULL
);

-- 유형별 목록 조회용(입고 화면은 SUPPLIER 만, 출고 화면은 CUSTOMER 만)
CREATE INDEX idx_partner_type ON partner (partner_type);

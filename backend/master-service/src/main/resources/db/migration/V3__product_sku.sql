-- 상품/SKU 마스터. 재고원장은 SKU 단위로만 참조한다(상품은 카탈로그/집계 단위).
-- 마스터는 삭제 대신 status=INACTIVE 전환(soft delete).
CREATE TABLE product (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50)  NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    large_category  VARCHAR(100) NOT NULL,
    medium_category VARCHAR(100),
    small_category  VARCHAR(100),
    status          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL,
    modified_at     TIMESTAMPTZ  NOT NULL
);

CREATE TABLE sku (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT      NOT NULL REFERENCES product (id),
    sku_code    VARCHAR(50) NOT NULL UNIQUE,
    barcode     VARCHAR(50) UNIQUE,          -- 스캔용. 없을 수 있고, PG 는 NULL 끼리 중복 허용
    unit        VARCHAR(20) NOT NULL,        -- 재고 최소 단위(EA 등) — 원장 qty 의 단위
    status      VARCHAR(20) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    modified_at TIMESTAMPTZ NOT NULL
);

-- 상품 상세(SKU 목록) 조회용
CREATE INDEX idx_sku_product_id ON sku (product_id);

-- SKU 옵션(색상/사이즈 등). JPA @ElementCollection — 수정 시 전체 교체된다.
CREATE TABLE sku_option (
    sku_id       BIGINT       NOT NULL REFERENCES sku (id),
    option_order INT          NOT NULL,
    option_name  VARCHAR(100) NOT NULL,
    option_value VARCHAR(255),
    PRIMARY KEY (sku_id, option_order)
);

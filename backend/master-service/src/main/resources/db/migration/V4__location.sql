-- 로케이션 마스터. 재고원장 (sku, location) 축의 location 쪽.
-- 계층(창고>존>셀)은 zone 문자열로 표현 — 필요해지면 트리로 승격.
-- 마스터는 삭제 대신 status=INACTIVE 전환(soft delete).
CREATE TABLE location (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50) NOT NULL UNIQUE,
    zone          VARCHAR(50) NOT NULL,
    location_type VARCHAR(20) NOT NULL,   -- RECEIVING/STORAGE/PICKING/PACKING/SHIPPING
    status        VARCHAR(20) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL,
    modified_at   TIMESTAMPTZ NOT NULL
);

-- 존 단위 목록 조회용
CREATE INDEX idx_location_zone ON location (zone);

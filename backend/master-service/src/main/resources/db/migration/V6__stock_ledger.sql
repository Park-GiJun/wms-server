-- 재고원장(척추). 모든 물리 이동의 append-only 사실 기록 — UPDATE/DELETE 없음,
-- 정정은 역방향 movement 추가. TRANSFER 는 2행(출발지 -qty / 도착지 +qty)으로 분해된다.
CREATE TABLE stock_movement (
    id            BIGSERIAL PRIMARY KEY,
    sku_id        BIGINT NOT NULL,
    location_id   BIGINT NOT NULL,
    movement_type VARCHAR(20) NOT NULL,             -- RECEIPT/PUTAWAY/TRANSFER/PICK/PACK/SHIP/ADJUSTMENT
    qty           BIGINT NOT NULL CHECK (qty <> 0), -- 부호 있는 수량(+유입/-유출)
    ref_type      VARCHAR(30),                      -- 유발 문서 종류 — 조정 등은 없을 수 있다
    ref_id        VARCHAR(100),
    seq           BIGINT NOT NULL,                  -- (sku_id, location_id) 쌍 안에서 단조 증가
    occurred_at   TIMESTAMPTZ NOT NULL,
    -- 단일 라이터가 깨졌을 때의 최후 방어(같은 쌍에 같은 seq 이중 기록 차단). 쌍 단위 이력 조회 인덱스 겸용.
    CONSTRAINT uq_stock_movement_pair_seq UNIQUE (sku_id, location_id, seq)
);

-- 현재 잔고 = 원장의 파생 상태이자 qty >= 0 불변식의 집행 지점(단일 라이터 관문).
-- 커맨드는 이 행을 SELECT ... FOR UPDATE 로 잠근 뒤 movement INSERT 와 한 트랜잭션으로 갱신한다.
CREATE TABLE stock_balance (
    sku_id      BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    qty         BIGINT NOT NULL CHECK (qty >= 0),   -- 음수재고 DB 레벨 이중 방어
    last_seq    BIGINT NOT NULL,
    modified_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (sku_id, location_id)
);

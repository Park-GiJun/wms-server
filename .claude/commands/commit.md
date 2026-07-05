---
description: 서비스별로 나눠 커밋·push 하고 history/YYYYMMDD/ 에 커밋 단위 작업 히스토리를 기록한다
argument-hint: [커밋 메시지 힌트 (선택)]
allowed-tools: Bash, Read, Write, Edit, Glob, Grep, Agent
---

# /commit — 서비스별 분리 커밋 + 작업 히스토리 기록 + push

현재 작업 트리의 변경사항을 **서비스(모듈) 단위로 나눠 각각 커밋**하고, 커밋마다 저장소 루트
`history/YYYYMMDD/` 에 작업 히스토리 문서를 하나씩 남긴 뒤, 마지막에 **`git push` 한 번**으로
원격에 반영한다. **히스토리 문서 작성은 반드시 Sonnet 모델 서브에이전트에게 위임한다**
(본 세션 모델이 직접 쓰지 않는다).

## 입력

- `$ARGUMENTS`: 커밋 메시지 힌트(선택). 있으면 커밋 메시지·히스토리 제목에 반영한다.

## 커밋 메시지 형식 (필수)

```
<type>[<scope>] : <한 줄 요약>
```

- `type`: `feat` / `fix` / `refactor` / `docs` / `chore` / `test` / `ci` 중 변경 성격에 맞는 것.
- `scope`: 커밋 그룹의 모듈명. 예: `master-service`, `user-service`, `gateway`,
  `platform-server`, `shared`. 서비스가 아닌 그룹은 `config-repo` / `infra` / `deploy` /
  `docs` / `claude` 등 디렉토리 성격에 맞는 이름.
- 예: `feat[master-service] : 재고 조정 커맨드 API 추가`
- 본문 마지막 줄: `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`

## 절차 (순서대로)

### 1. 변경사항 파악
- `git status --porcelain` 과 `git diff HEAD` (스테이징 여부 무관하게 전체)를 확인한다.
- 변경이 하나도 없으면 **중단**하고 사용자에게 알린다.
- `.env`·자격증명 등 시크릿으로 보이는 파일이 있으면 **진행 전 중단**하고 사용자에게 확인.
- 최근 커밋 스타일 참고를 위해 `git log --oneline -5` 를 본다.

### 2. 서비스별 커밋 그룹 나누기
변경 파일을 아래 규칙으로 그룹핑한다. **그룹 1개 = 커밋 1개 = 히스토리 문서 1개.**

- `backend/<module>/**` → 모듈별로 각각 한 그룹 (scope = `<module>`).
- `config-repo/<service>.yml` → 해당 서비스 그룹에 **합류**시킨다(같이 바뀐 경우).
  해당 서비스의 코드 변경 없이 설정만 바뀌었으면 scope `config-repo` 로 별도 그룹.
- `backend/settings.gradle.kts`·`backend/build.gradle.kts`·`gradle/**` 등 빌드 루트 공통 파일:
  특정 서비스 신설/변경에 딸린 것이면 그 서비스 그룹에 합류, 아니면 scope `build` 로 별도 그룹.
- `infra/**` → `infra`, `deploy/**`·`.teamcity/**` → `deploy`, `.claude/**` → `claude`,
  루트 문서(`CLAUDE.md`, `README` 등) → `docs`.
- 논리적으로 한 작업인데 위 규칙이 억지로 쪼개는 경우(예: 새 서비스 스캐폴드 = backend 모듈
  + config-repo + gateway 라우트 + settings)는 **한 그룹으로 묶는 게 우선**이다. 그룹핑의
  목적은 "커밋 하나 = 의미 있는 작업 단위 하나"이지 기계적 디렉토리 분할이 아니다.
- 그룹 순서: 의존받는 쪽 먼저 (shared → 서비스들 → config/infra/docs 순 권장).

### 3. 히스토리 경로·시퀀스 결정
- 오늘 날짜: `Get-Date -Format yyyyMMdd` (예: `20260705`).
- 디렉토리: `history/<YYYYMMDD>/` (없으면 생성).
- Glob(`history/<YYYYMMDD>/*.md`)으로 기존 최대 시퀀스를 찾아, 그룹들에 순서대로
  `<YYYYMMDD>-<NN>.md`(`NN`=`01`~`99`, zero-pad) 번호를 배정한다. `99` 초과 시 중단.

### 4. 히스토리 문서 작성 — **Sonnet 서브에이전트**
Agent 툴 호출 (**반드시 `model: "sonnet"`, `run_in_background: false`**). 그룹이 여러 개면
**한 번의 호출로 모든 그룹의 문서를 한꺼번에** 쓰게 한다(호출 남발 금지).

- 프롬프트에 담을 것: 그룹별 파일 목록과 diff(길면 `--stat` + 핵심 발췌), 그룹별 커밋 메시지
  (2단계에서 정한 것), 사용자 힌트(`$ARGUMENTS`), 이번 세션 작업 요약, 그룹별 대상 파일 경로.
- **파일 내용만 정확히 쓰고 다른 작업은 하지 말 것**을 명시한다.
- 각 문서는 아래 템플릿의 **한국어** 마크다운:

```markdown
# <작업 한 줄 요약>

- **일시**: YYYY-MM-DD HH:mm
- **커밋**: <커밋 메시지 제목 한 줄 — 해시는 적지 않는다(커밋이 자기 해시를 담을 수 없음)>

## 작업 내용
<무엇을 왜 했는지 2~5문장>

## 변경 파일
<파일 경로 목록 + 각 파일에서 바뀐 것 한 줄>

## 주요 결정사항
<설계/구현 판단이 있었다면. 없으면 "없음">

## 후속 작업
<남은 일이 있다면. 없으면 "없음">
```

- 서브에이전트 종료 후 모든 문서가 실제로 생성됐는지, 끝에 잘못 섞인 텍스트(태그 잔여물 등)가
  없는지 확인하고 필요하면 정리한다.

### 5. 그룹별 커밋 → push
그룹 순서대로 반복한다:
1. 그 그룹의 파일들 + 해당 히스토리 문서만 `git add <경로들>` 로 스테이징한다
   (**`git add -A` 금지** — 다른 그룹 파일이 섞인다).
2. 위 형식의 메시지로 커밋한다.

모든 그룹 커밋 후 남은 변경이 없는지 `git status --porcelain` 으로 확인하고(누락분이 있으면
적절한 그룹으로 추가 커밋), 마지막에 **`git push`** 한다. push 실패(rejected 등) 시 임의로
force push 하지 말고 사용자에게 보고한다.

### 6. 보고
- 커밋별 `해시 · 메시지 · 히스토리 파일 경로` 목록과 push 결과를 사용자에게 알린다.

## 규칙

- 히스토리 문서 작성 주체는 항상 Sonnet 서브에이전트 — 비용 절감 목적이므로 직접 쓰지 말 것.
- `history/` 는 git 추적 대상이다(.gitignore 에 넣지 않는다).
- 같은 날 커밋이 쌓이면 `-01`, `-02`, … 로 이어진다.
- push 는 `git push` 만. force push·브랜치 변경은 하지 않는다.

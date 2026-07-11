# 아키텍처

## 도메인

두 개의 엔티티로 충분하다.

- **StockItem** — 품목. `barcode`(고유), `name`, `quantity`. 재고 수량의 정본.
- **StockMovement** — 입출고 이력 한 건. `type`(INBOUND/OUTBOUND), `quantity`,
  `quantityAfter`(처리 후 재고), `memo`.

재고 수량은 이력의 합으로 계산할 수도 있지만, 데모 규모에서는 `StockItem.quantity`를
정본으로 두고 입출고 시 한 트랜잭션 안에서 수량을 갱신하면서 이력을 함께 남긴다.
출고 수량이 현재 재고보다 크면 거부한다.

## API

| Method | Path | 설명 | 실패 |
|--------|------|------|------|
| GET | `/api/items` | 품목 목록 | |
| GET | `/api/items/{barcode}` | 바코드로 품목 조회 | 404 미등록 |
| POST | `/api/items` | 신규 등록 | 409 바코드 중복 |
| POST | `/api/items/{barcode}/inbound` | 입고 | 404 미등록 |
| POST | `/api/items/{barcode}/outbound` | 출고 | 400 재고 부족, 404 미등록 |
| GET | `/api/movements?barcode=` | 이력 조회(바코드 필터 선택) | |

요청/응답 본문은 Swagger UI(`/swagger-ui.html`)에서 확인할 수 있다.

에러 응답은 `{ status, message, timestamp }` 형태로 통일했고, 앱과 웹 모두 이 `message`를
그대로 사용자에게 보여준다.

## 계층

```
Controller  →  Service (@Transactional)  →  Repository (Spring Data JPA)  →  H2
```

- 컨트롤러는 DTO 매핑과 검증만 맡고 로직은 서비스에 둔다.
- 재고 증감 규칙(입출고 검증)은 `StockItem` 엔티티 안에 둬서 서비스가 얇게 유지된다.
- H2는 파일 모드다. MySQL 전환은 `application-mysql.yml`과 커넥터 의존성 추가만으로
  되도록 데이터소스 설정을 프로파일로 분리해 뒀다.

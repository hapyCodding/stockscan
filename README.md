# StockScan

바코드 스캔으로 재고를 입출고하는 소규모 재고관리 데모다. 안드로이드 앱으로 품목을
스캔해서 입고/출고를 기록하고, 같은 REST API를 웹 대시보드에서 조회한다. 안드로이드 앱,
Spring 백엔드, 웹 대시보드를 한 사람이 하나의 API 계약으로 엮을 수 있다는 걸 보여주려고
만들었다. 실제 서비스가 아니라 포트폴리오용이라 기능은 작게, 대신 구조는 실무에 가깝게 잡았다.

## 구성

| 폴더 | 내용 | 스택 |
|------|------|------|
| [stockscan-api](stockscan-api) | REST API, 재고 도메인, 이력 | Spring Boot, Kotlin, JPA, H2 |
| [stockscan-app](stockscan-app) | 바코드 스캔 앱 | Kotlin, Jetpack Compose, CameraX, ML Kit, Retrofit |
| [stockscan-web](stockscan-web) | 재고/이력 대시보드 | HTML, CSS, JS (프레임워크 없음) |

## 아키텍처

```
 ┌─────────────────┐   스캔·입출고   ┌──────────────────┐
 │  Android 앱      │ ─────────────▶ │  Spring Boot API │
 │  (CameraX+ML Kit)│ ◀───────────── │  (H2, JPA)       │
 └─────────────────┘   품목/재고     └──────────────────┘
                                              ▲
                                     조회      │
                                     ┌─────────┴────────┐
                                     │  웹 대시보드      │
                                     │  (재고·이력 표)   │
                                     └──────────────────┘
```

세 파트가 공유하는 건 `/api` REST 계약 하나다. 앱이 스캔한 바코드로 품목을 조회하고
입고(+)/출고(-)를 보내면 서버가 재고 수량을 갱신하고 이력을 남긴다. 웹은 같은 API를
읽어 현재 재고와 이력을 표로 보여준다. 도메인 설계와 API 명세는
[docs/architecture.md](docs/architecture.md)에 정리했다.

## 실행

### 백엔드

```
cd stockscan-api
./gradlew bootRun
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 콘솔: `http://localhost:8080/h2-console`
- 첫 실행 시 샘플 품목 4개가 자동으로 들어간다. 데이터는 `stockscan-api/data`에 파일로 남는다.
- 기본은 H2(파일 DB)다. 로컬 NAS의 MariaDB로 붙이려면 [docs/nas-mariadb.md](docs/nas-mariadb.md) 참고.
- NAS에 컨테이너로 올려 HTTPS로 공개하는 방법은 [docs/public-demo.md](docs/public-demo.md) 참고.

### 웹 대시보드

정적 파일이라 아무 정적 서버로 열면 된다.

```
cd stockscan-web
python -m http.server 5500
```

`http://localhost:5500` 접속. 백엔드를 8080이 아닌 다른 포트로 띄웠다면
`http://localhost:5500/?api=http://localhost:9000` 처럼 `api` 쿼리로 주소를 넘긴다.

### 안드로이드 앱

`stockscan-app`을 Android Studio로 열고 에뮬레이터 또는 실기기에서 실행한다.
API 주소(`app/build.gradle.kts`의 `API_BASE_URL`)는 빌드 타입별로 나눠 뒀다.

- **debug**: `http://10.0.2.2:8080/` — 에뮬레이터에서 호스트 PC의 로컬 백엔드로 접속.
  실기기로 로컬에 붙일 땐 PC의 LAN IP로 바꾼다.
- **release**: `https://psh55401.synology.me:8443/` — NAS에 배포된 공개 백엔드. 실기기에서
  네트워크 상관없이 그대로 동작한다.

## 데모 흐름

앱에서 품목을 스캔하면 서버에 조회한다. 없는 바코드면 등록 화면이, 있으면 상세 화면이
뜬다. 입고/출고를 누르면 서버 재고가 바뀌고 이력이 쌓인다. 이 상태를 웹 대시보드에서
새로고침하면 그대로 보인다. 시연 순서는 [docs/demo-scenario.md](docs/demo-scenario.md)에 적어뒀다.

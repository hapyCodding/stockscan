# NAS MariaDB 연동

기본 실행은 H2(파일 DB)라 아무 설정 없이 돌아간다. 로컬 NAS(Synology DS220+)의
MariaDB를 실제 DB로 쓰고 싶을 때만 아래를 따른다. `mariadb` 프로파일로 전환한다.

## DSM 쪽 준비 (한 번만)

1. **패키지 센터**에서 `MariaDB 10` 설치.
2. MariaDB 10 앱을 열고 **"TCP/IP 연결 허용"** 체크, 포트 확인 후 적용. MariaDB 5와
   공존하면 10은 `3307`을 쓴다(이 NAS 기준). (DSM 방화벽을 켜뒀다면 해당 포트 허용)
3. 데이터베이스와 계정 생성. `패키지 센터`에서 phpMyAdmin을 설치해 root로 로그인한 뒤
   [stockscan-api/db/init.sql](../stockscan-api/db/init.sql)을 실행한다. 스크립트의
   `CHANGE_ME`를 쓸 비밀번호로 바꾼다. (SSH를 켰다면 `mysql -u root -p < init.sql`도 됨)

포트가 열렸는지는 PC에서 이렇게 확인한다.

```
# PowerShell
Test-NetConnection 192.168.35.2 -Port 3307
```

## 백엔드 실행

비밀번호는 환경변수로 넘긴다(소스에 넣지 않는다).

```
# PowerShell
$env:DB_PASSWORD="위에서 정한 비밀번호"
cd stockscan-api
./gradlew bootRun --args='--spring.profiles.active=mariadb'
```

첫 실행 때 `ddl-auto: update`가 테이블을 만들고, 샘플 시더가 품목 4개를 넣는다.
접속 정보는 [application-mariadb.yml](../stockscan-api/src/main/resources/application-mariadb.yml)에
있고, NAS IP가 다르면 `url`만 바꾼다.

웹 대시보드와 앱은 백엔드 주소만 바라보므로 DB가 H2든 MariaDB든 그대로 동작한다.

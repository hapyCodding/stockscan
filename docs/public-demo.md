# 공개 데모 (앱만 HTTPS 노출)

목표 구조. DB 포트는 인터넷에 내보내지 않는다.

```
인터넷 ── HTTPS ──▶ DSM 리버스 프록시(443/8443) ──▶ 백엔드 컨테이너(8080) ──▶ MariaDB(내부 3307)
```

## 0. DB 외부 포워딩 닫기 (먼저)

`psh55401.synology.me:3307`이 인터넷에 열려 있으면 닫는다. 공유기 관리 페이지 또는
Synology가 포트포워딩을 관리하면 **제어판 > 외부 액세스 > 라우터 구성**에서 3307 규칙을 삭제.
백엔드는 내부에서 `192.168.35.2:3307`로만 붙으므로 외부 노출이 필요 없다.

## 1. 프로젝트 배포 (Container Manager)

이 NAS는 Container Manager의 **프로젝트(docker-compose)** 로 컨테이너를 운영한다
(`/volume1/docker/<이름>`). 같은 방식으로 올린다.

1. `stockscan-api/` 폴더를 NAS `/volume1/docker/stockscan/`에 복사한다(File Station 또는
   git clone). `Dockerfile`, `docker-compose.yml`, 소스가 함께 있어야 빌드된다.
2. 그 폴더에 `.env`를 만든다(`.env.example` 참고):
   ```
   DB_PASSWORD=설정한_비밀번호
   ```
3. **Container Manager > 프로젝트 > 생성** → 경로 `/volume1/docker/stockscan` →
   기존 `docker-compose.yml` 사용 → 빌드 후 실행.

컨테이너는 호스트 LAN IP(`192.168.35.2:3307`)로 MariaDB에 접속한다(compose에 지정됨).
빌드는 NAS에서 진행되어 2GB 환경에선 다소 느릴 수 있다. 더 가볍게 하려면 PC에서
`docker build`로 이미지를 만들어 Container Manager로 가져오고 compose의 `build: .`를 지운다.

기동 후 LAN에서 먼저 확인: `http://192.168.35.2:8080/api/items`

## 3. 인증서 + 리버스 프록시 (DSM)

DDNS `psh55401.synology.me`는 이미 있다.

1. **제어판 > 보안 > 인증서**: Let's Encrypt 인증서 발급(도메인 `psh55401.synology.me`).
2. **제어판 > 로그인 포털 > 고급 > 리버스 프록시 > 생성**:
   - 소스: HTTPS / `psh55401.synology.me` / `8443`
   - 대상: HTTP / `localhost` / `8080`
3. 공유기에서 `8443` 포워딩. 리버스 프록시 서비스에 위 인증서를 지정.

확인:

```
curl https://psh55401.synology.me:8443/api/items
```

## 4. 인증 주의 (쓰기 API)

이 API는 인증이 없다. 공개하면 누구나 재고를 바꿀 수 있으므로 둘 중 하나를 택한다.

- 데모로 공개하고 시드를 주기적으로 리셋(가장 단순).
- 리버스 프록시에 basic auth를 건다. 단 브라우저 대시보드가 `fetch` 시 자격증명을
  같이 보내야 하므로, 이 경우 대시보드도 같은 인증 뒤에 둔다.

## 5. 웹 대시보드 공개

`stockscan-web`을 **Web Station**으로 정적 호스팅하고, API 주소를 공개 백엔드로 넘긴다.

```
https://<웹호스팅주소>/index.html?api=https://psh55401.synology.me:8443
```

백엔드 CORS가 모든 오리진을 허용(GET/POST)하도록 돼 있어 다른 호스트에서 열어도 동작한다.
공개 범위를 좁히려면 `WebConfig`의 `allowedOrigins`를 실제 대시보드 주소로 제한한다.

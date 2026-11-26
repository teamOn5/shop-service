# API · 에러 · 통신 규칙

## REST API

- RESTful로 설계하고, 리소스 이름은 복수형 명사를 쓴다.
- 응답은 `ApiResponses`로 만든 `ResponseEntity<ApiResponse<T>>`를 반환한다. 그래야 HTTP 상태 코드와 body의 `status`가 일치한다.
- 페이지네이션: 페이지 번호는 0부터, 기본 크기는 20. `PageConstants`를 쓰고 `PageResult`/`CursorResult`로 반환한다.
- springdoc api-docs 경로: `/api/live-service/v3/api-docs`

### 경로 규칙

| 구분 | 경로 | Controller 예 |
|---|---|---|
| 사용자(시청자) | `/api/v1/lives/**` | `LiveController` |
| 판매자 | `/api/v1/seller/lives/**` | `SellerLiveController` |
| 관리자 | `/api/v1/admin/lives/**` | `AdminLiveController` |
| 내부(서비스 간) | `/internal/v1/lives/**` | `InternalLiveController` |

- 판매자, 관리자, 내부 API는 Controller를 분리한다. 모두 `adapter/in/web`에 둔다.
- 내부 API는 Gateway로 외부에 노출하지 않는 것을 전제로 한다.

## 에러 처리

- 비즈니스 예외는 항상 `BusinessException`을 쓴다.
- LIVE 전용 에러는 `LiveErrorCode` enum(`ErrorCode` 구현)에 정의한다. 처음 필요할 때 `domain` 패키지에 만든다.
  - 상수명: 대문자 SNAKE_CASE
  - 코드: `LIVE` + HTTP 상태 3자리 + 순번 1자리. 예: `LIVE4040`(NOT_FOUND), `LIVE4090`(CONFLICT)
  - 코드의 상태값 3자리는 `status()`와 같아야 한다. status는 400~599 범위, 코드는 중복 불가
  - 테스트에서 `ErrorCodeValidator.validate(LiveErrorCode.values())`로 검증한다.
- `detail`은 로그용이다. 사용자 응답에는 `errorCode.message()`가 쓰인다.
- 잘못된 입력, 권한, 외부 서비스 오류 같은 공통 상황에는 `CommonErrorCode`를 쓴다.

## 내부 API / FeignClient

- `@EnableFeignClients`는 `LiveServiceApplication`에 선언되어 있다.
- FeignClient는 `adapter/out/client`에 두고, `application/port/out`의 인터페이스를 구현하는 adapter를 통해 사용한다.
- 호출 대상은 Eureka 서비스 이름으로 지정한다. 예: `@FeignClient(name = "user-service")`
- 내부 API도 `ApiResponse` 형식을 쓰고, 내부 서비스의 에러 코드는 호출한 서비스에 그대로 전파한다.

## WebSocket (STOMP)

설정: `adapter/in/websocket/WebSocketConfig`

| 항목 | 값 |
|---|---|
| 연결 엔드포인트 | `/ws` |
| 클라이언트 → 서버 | `/app/**` (`@MessageMapping`) |
| 브로드캐스트 구독 | `/topic/**` |
| 개인 메시지 구독 | `/queue/**` |

- 브로커는 Spring 내장 simple broker다. 인스턴스가 여러 개가 되면 외부 브로커나 Redis Pub/Sub 도입을 검토해야 하므로 개발자에게 문의한다.
- 허용 Origin은 아직 설정하지 않았다(동일 출처만 허용). 프론트엔드 도메인이 정해지면 `setAllowedOriginPatterns`를 추가한다.
- 메시지 핸들러(`@MessageMapping`)는 `adapter/in/websocket`에 둔다.

## Kafka

- 설정: `application.yaml`의 `spring.kafka`
  - 접속 주소: `SPRING_KAFKA_BOOTSTRAP_SERVERS`
  - consumer group: `live-service`
  - key와 value 모두 String 직렬화
- 발행: 공통 이벤트 객체를 String으로 직렬화해서 보낸다. 소비: String을 받아 공통 이벤트 객체로 역직렬화한다.
- Producer와 Consumer는 `adapter/out/messaging`에 둔다.
- `common-event`가 나오기 전에는 이벤트 클래스를 구현하지 않는다. 작업이 필요하면 먼저 개발자에게 확인한다.

## 저장소 역할 (기본안)

| 저장소 | 용도 |
|---|---|
| MySQL (JPA) | 방송 정보, 상태 등 영속 핵심 데이터 |
| Redis | 실시간 시청자 수, 접속 세션 등 휘발성·고빈도 데이터 |
| MongoDB | 채팅 메시지 등 대량 로그성 데이터 |

기본안이다. 이 표에 없는 데이터를 새로 저장하거나 용도를 바꿀 때는 개발자에게 확인한다.

## 영상 (Nginx-RTMP)

- RTMPS 송출 수신과 HLS 재생은 Nginx-RTMP가 담당하며, 설정은 이 저장소에서 관리하지 않는다.
- Spring Boot는 영상 스트림을 처리하지 않는다. Nginx-RTMP와 연동(송출 인증 콜백, HLS URL 구성 등)이 필요하면 개발자에게 방식을 확인한다.

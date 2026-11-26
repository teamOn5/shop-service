# 공통 모듈 (`com.msa.common.*`)

GitHub Packages(`https://maven.pkg.github.com/teamon5/common-module`)에서 받는다.
사용 전에 실제 클래스와 메서드를 확인하고, 구현을 추측하지 않는다. 소스 jar 위치는 `~/.gradle/caches/modules-2/files-2.1/com.teamon5/`이다.
공통 모듈이 이미 제공하는 기능은 서비스 안에 다시 구현하지 않는다.

## common-core 0.1.0 (`com.msa.common.core`)

- `error`
  - `ErrorCode`: `status()`, `code()`, `message()`
  - `CommonErrorCode`: INVALID_INPUT, NOT_FOUND, CONFLICT, EXTERNAL_SERVICE_ERROR 등 공통 에러
  - `BusinessException(errorCode)`, `(errorCode, detail)`, `(errorCode, cause)`
  - `ErrorCodeValidator.validate(ErrorCode[])`
- `page`
  - `PageConstants`: `DEFAULT_PAGE=0`, `DEFAULT_SIZE=20`, `MAX_SIZE=100`
  - `PageResult<T>`: `of(content, page, size, totalElements)`, `map()`
  - `CursorResult<T>`: `of(content, nextCursor)`, `map()`
- `type`: `MediaType`, `ShopRole`, `UserRole`

## common-web 0.1.0 (`com.msa.common.web`)

- `response`
  - `ApiResponse<T>`: `status`, `code`, `message`, `data`, `errors`, `timestamp`
  - `ApiResponses`: `ok(data)`, `ok()`, `created(data)`, `fail(errorCode)`. 반환형은 `ResponseEntity<ApiResponse<T>>`
  - `FieldErrorResponse`, `SuccessCode`(`COMMON2000`, `COMMON2010`)
- `exception.GlobalExceptionHandler`
  - 자동 설정(`CommonWebAutoConfiguration`)으로 등록된다.
  - `BusinessException`, 검증 예외, 타입 불일치, 404, 405, 그 밖의 `Exception`을 처리한다.
  - 같은 예외를 처리하는 `@RestControllerAdvice`를 서비스 안에 만들지 않는다.

## common-monitoring 0.1.2 (`com.msa.common.monitoring`)

EnvironmentPostProcessor가 가장 낮은 우선순위로 아래 기본값을 넣는다. 이 설정들을 `application.yaml`에 다시 작성하지 않는다.

- `management.endpoints.web.exposure.include=health,info,prometheus`
- `management.metrics.tags.application=${spring.application.name}`
- `management.metrics.distribution.percentiles-histogram.http.server.requests=true`
- `eureka.instance.metadata-map[prometheus.scrape|prometheus.path]`
- `eureka.instance.prefer-ip-address=true`

## common-event: 아직 개발 전

- Kafka 공통 이벤트 직렬화·역직렬화를 담당할 예정이지만 아직 개발·배포되지 않았다.
- **클래스나 메서드를 참조하지 않고, 공통 이벤트 클래스를 임의로 만들지 않는다.**
- Kafka 이벤트 작업이 필요하면 먼저 개발자에게 문의한다.

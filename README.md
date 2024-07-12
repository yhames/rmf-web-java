# 로봇서비스 API Gateway 고도화

- [로봇서비스 API Gateway 고도화](#로봇서비스-api-gateway-고도화)
  - [프로젝트 요약](#프로젝트-요약)
    - [실행 방법](#실행-방법)
      - [Mockup 노드 기능](#mockup-노드-기능)
    - [팀원](#팀원)
    - [사용기술](#사용기술)
    - [Git Branch 전략](#git-branch-전략)
    - [프로젝트 구조](#프로젝트-구조)
  - [요구사항](#요구사항)
    - [Python 모듈 구조도](#python-모듈-구조도)
    - [Java 전환 방향성 및 개발 범위](#java-전환-방향성-및-개발-범위)
  - [아키텍처 설계](#아키텍처-설계)
    - [Java API 모듈 구조도](#java-api-모듈-구조도)
    - [데이터 플로우 다이어그램](#데이터-플로우-다이어그램)
  - [화면 설계](#화면-설계)
  - [데이터베이스 설계](#데이터베이스-설계)
    - [테이블 및 인덱스 정의서](#테이블-및-인덱스-정의서)
  - [테스트 시나리오](#테스트-시나리오)
    - [통합테스트](#통합테스트)
    - [단위테스트](#단위테스트)

## 프로젝트 요약

> [Open-RMF](https://github.com/open-rmf)

오픈소스 프로젝트인 `Open-RMF`의 `rmf-web`를 활용한 `Python → Java` 전환 프로젝트

### 실행 방법

* `PostgreSQL`, `Spring Cloud Gateway`, `api-java-server`

```bash
docker-compose up
```

* `rmf-web` 실행

```bash
docker exec -it innovation-rmf-web /bin/bash
pnpm start:react
```

* `ROS Mock Server` 실행

```bash
docker exec -it innovation-mock-server /bin/bash
python3 src/mock_server/mock_server/websocket_client.py
```

#### Mockup 노드 기능

- 웹소켓 메시지(`fleet_state`, `task_state`, `task_log`) 전송
- `/mock/dummy` 요청
    - `/building_map` 이벤트 발행 요청
    - `Quartz` 스케쥴러에 `door_state` 이벤트 발행 `Job` 추가

### 팀원

| 정승철<br>@middlefitting | 김예건<br>@dawnpoems | 박정우<br>@yhames |
|:--------------------:|:----------------:|:-------------:|

↗️ [업무분담 및 개인별 진행사황](documents%2F%EA%B0%9C%EC%9D%B8%EB%B3%84%20%EC%A7%84%ED%96%89%EC%83%81%ED%99%A9.md)

### 사용기술

> `Java` `Spring` `ROS` `MySQL` `Docker`  
> `WebSocket` `Socket.io` `ReactiveX`  
> `Python` `Typescript`  `React`

### Git Branch 전략

* `branch`는 `main`, `dev`, `feature`로 구분합니다.
* `issue` 및 `branch` 생성
  * `WBS` 작업 목록을 `milestone`으로 등록
  * `issue` 생성하고 `milestone`과 연결된 상세 내용 및 담당자 설정
  * 생성된 `issue`에서 `branch` 생성
  * `branch` `issue`에서 자동으로 생성되는 것을 사용
* `feature` 작업시 [Conventional Commit](https://www.conventionalcommits.org/ko/v1.0.0/) 적용
* 작업 완료된 `feature`는 `dev`로 `pull request`
  * `main`, `dev`에는 `push` 지양
* `merge`
  * `squash merge` 사용
  * `commit message`는 `#<issue 번호>-<issue 제목> (#<pr 번호>)` 형식 준수

### 프로젝트 구조

* 전체 프로젝트 구조

```
.
├── api-server-java
├── database
├── documents
│   ├── 01 분석
│   ├── 02 설계
│   ├── 03 개발
│   └── 04 테스트
├── rmf-api-gateway
├── rmf-web
│   ├── docker
│   ├── packages
│   │   ├── api-client
│   │   ├── api-server
│   │   ├── dashboard
│   │   ├── dashboard-e2e
│   │   ├── react-components
│   │   ├── rmf-auth
│   │   ├── rmf-models
│   │   └── ros-translator
│   ├── pipenv-install
│   └── scripts
└── src
    ├── mock_server
    │   ├── bags
    │   │   └── rosbag2_2024_03_26-06_07_58
    │   ├── dummy
    │   ├── launch
    │   ├── mock_server
    │   ├── resource
    │   └── test
    ├── rmf_api_msgs
    ├── rmf_building_map_msgs
    ├── rmf_internal_msgs
    └── rmf_visualization_msgs
```

* `api-server-java` 프로젝트 구조

```
api-server-java
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── rmf
    │   │           └── apiserverjava
    │   │               ├── baseentity
    │   │               ├── config
    │   │               ├── controller
    │   │               ├── dto
    │   │               ├── entity
    │   │               ├── global
    │   │               │   ├── annotation
    │   │               │   ├── constant
    │   │               │   ├── converter
    │   │               │   ├── exception
    │   │               │   ├── parser
    │   │               │   └── utils
    │   │               ├── jobs
    │   │               ├── mock
    │   │               ├── repository
    │   │               ├── rmfapi
    │   │               ├── rosmsgs
    │   │               ├── rxjava
    │   │               │   ├── bookkeepers
    │   │               │   ├── eventbus
    │   │               │   ├── eventconsumer
    │   │               │   └── watchdog
    │   │               ├── security
    │   │               ├── service
    │   │               └── websocket
    │   └── resources
    │       └── door_dummy
    └── test
        └── java
            └── com
                └── rmf
                    └── apiserverjava
                        ├── config
                        ├── entity
                        ├── global
                        ├── integration
                        ├── repository
                        └── service
```

## 요구사항

### Python 모듈 구조도

![Python_API_모듈_분석.png](documents%2Fimages%2FPython_API_%EB%AA%A8%EB%93%88_%EB%B6%84%EC%84%9D.png)

### Java 전환 방향성 및 개발 범위

* Java 전환 방향성

|    구분     |             Python             |          Java          |
|:---------:|:------------------------------:|:----------------------:|
| Framework | Uvicorn<br>FastAPI<br>Pydantic |      Spring Boot       |
| Protocol  |     WebSocket<br>socket.io     | WebSocket<br>socket.io |
|    ORM    |            Tortoise            |          JPA           |
| ReactiveX |              RxPy              |         RxJava         |
| Database  |             SQLite             |       PosgreSQL        |

* 개발 범위

|      구분       | 설명                                                       |
|:-------------:|----------------------------------------------------------|
|   REST API    | API Client에서 요청하는 REST API 구현                            |
|   WebSocket   | 리액티브 프로그래밍으로 Fleet Adapter에서 전달하는 WebSocket 데이터 처리 기능 구현 |
|   socket.io   | API Client에서 구독하는 Socket.io API 구현                       |
|      로그인      | DB, REST API, 클라이언트 개발 및 인증과 인가 처리                       |
| (ROS Mocking) | RMF Core와 연결된 기능의 Mocking으로 ROS 의존성 제거                   |
|   (사용자 관리)    | 사용자 추가 및 정보 변경 기능 개발                                     |

* 개발 방식 구조도

![Java_서버_개발_구조도.png](documents%2Fimages%2FJava_%EC%84%9C%EB%B2%84_%EA%B0%9C%EB%B0%9C_%EA%B5%AC%EC%A1%B0%EB%8F%84.png)

## 아키텍처 설계

### Java API 모듈 구조도

[[로봇서비스 API 고도화] 01 Java API 모듈 구조도.pdf](documents%2F02%20%EC%84%A4%EA%B3%84%2F%5B%EB%A1%9C%EB%B4%87%EC%84%9C%EB%B9%84%EC%8A%A4%20API%20%EA%B3%A0%EB%8F%84%ED%99%94%5D%2001%20Java%20API%20%EB%AA%A8%EB%93%88%20%EA%B5%AC%EC%A1%B0%EB%8F%84.pdf)

![Java_API_모듈_분석.png](documents%2Fimages%2FJava_API_%EB%AA%A8%EB%93%88_%EB%B6%84%EC%84%9D.png)

### 데이터 플로우 다이어그램

[[로봇서비스 API 고도화] 02 데이터 플로우 다이어그램.pdf](documents%2F02%20%EC%84%A4%EA%B3%84%2F%5B%EB%A1%9C%EB%B4%87%EC%84%9C%EB%B9%84%EC%8A%A4%20API%20%EA%B3%A0%EB%8F%84%ED%99%94%5D%2002%20%EB%8D%B0%EC%9D%B4%ED%84%B0%20%ED%94%8C%EB%A1%9C%EC%9A%B0%20%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.pdf)

![DataFlow_Diagram.png](documents%2Fimages%2FDataFlow_Diagram.png)

## 화면 설계

[[로봇서비스 API 고도화] 04 FE-화면설계-와이어프레임.pdf](documents%2F02%20%EC%84%A4%EA%B3%84%2F%5B%EB%A1%9C%EB%B4%87%EC%84%9C%EB%B9%84%EC%8A%A4%20API%20%EA%B3%A0%EB%8F%84%ED%99%94%5D%2004%20FE-%ED%99%94%EB%A9%B4%EC%84%A4%EA%B3%84-%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84.pdf)

![화면_설계_01.png](documents%2Fimages%2F%ED%99%94%EB%A9%B4_%EC%84%A4%EA%B3%84_01.png)

![화면_설계_02.png](documents%2Fimages%2F%ED%99%94%EB%A9%B4_%EC%84%A4%EA%B3%84_02.png)

![화면_설계_03.png](documents%2Fimages%2F%ED%99%94%EB%A9%B4_%EC%84%A4%EA%B3%84_03.png)

## 데이터베이스 설계

### 테이블 및 인덱스 정의서

[[로봇서비스 API 고도화] 05 DB 물리 설계서.xlsx](documents%2F02%20%EC%84%A4%EA%B3%84%2F%5B%EB%A1%9C%EB%B4%87%EC%84%9C%EB%B9%84%EC%8A%A4%20API%20%EA%B3%A0%EB%8F%84%ED%99%94%5D%2005%20DB%20%EB%AC%BC%EB%A6%AC%20%EC%84%A4%EA%B3%84%EC%84%9C.xlsx)

## 테스트 시나리오

### 통합테스트

개발 범위에 대한 테스트 시나리오 기반 통합 테스트 작성

[[로봇서비스 API 고도화] 02 테스트 시나리오 기반 통합테스트 결과서.pdf](documents%2F04%20%ED%85%8C%EC%8A%A4%ED%8A%B8%2F%5B%EB%A1%9C%EB%B4%87%EC%84%9C%EB%B9%84%EC%8A%A4%20API%20%EA%B3%A0%EB%8F%84%ED%99%94%5D%2002%20%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4%20%EA%B8%B0%EB%B0%98%20%ED%86%B5%ED%95%A9%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EA%B2%B0%EA%B3%BC%EC%84%9C.pdf)

![Integration_Test_Coverage.png](documents%2Fimages%2FIntegration_Test_Coverage.png)

### 단위테스트

비즈니스 로직이 존재하는 `Service`, `Utils`, `Entity` 계층에 메서드 단위의 테스트 코드 작성

![Unit_Test_Coverage.png](documents%2Fimages%2FUnit_Test_Coverage.png)

# Withmarket-main-server

이 프로젝트는 WithMarket의 **가게와 관련된 서비스**를 담당하는 스프링 기반의 프로젝트입니다.

현재는 **아키텍처의 확장** 에 집중하고 있으며, 요구사항이 모두 정해질 시 기능 구현을 할 에정입니다.

* * *

### 👉 본 프로젝트의 목적

본 프로젝트는 졸업작품을 위한 것 뿐만 아니라 본인의 실력 향상에도 목적을 두고있다.

따라서 본 프로젝트의 구현에 있어서 우선순위는 다음과 같다.

1️⃣ 확장에 유연한 아키텍처 설계 능력 향상

2️⃣ 역할과 책임이 명확하게 분리되어있는 설계

3️⃣ 신기술에 대해서 트레이드오프를 따지며 기술선정을 하는 능력

4️⃣ 기능 구현

* * *

### 👉 프로젝트 참여자 (Contributors of this project)
1️⃣ Doyeop Kim (18k7102dy@naver.com)

* * *

### 👉 사용되는 기술 스택 (Tech used in this project)

1️⃣ **사용 언어**

<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=black">

2️⃣ **프로젝트 아키텍처**

* Multi-Module Architecture (멀티모듈 기반의 설계)
* CQRS Architecture (Command-Query Responsibility Segregation)

3️⃣ **개발 방법론**

* DDD (Domain Driven Development)

4️⃣ **사용된 데이터베이스**

<img src="https://img.shields.io/badge/DynamoDB-4053D6?style=for-the-badge&logo=Amazon DynamoDB&logoColor=white"> 
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">

5️⃣ **사용된 프레임워크**

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=black">
<img src="https://img.shields.io/badge/Spring Data Redis-6DB33F?style=for-the-badge&logo=Redis&logoColor=white">
<img src="https://img.shields.io/badge/Spring Webflux-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">

6️⃣ **사용된 라이브러리**

<img src="https://img.shields.io/badge/AWS DynamoDB Sdk-4053D6?style=for-the-badge&logo=Amazon DynamoDB&logoColor=white">
<img src="https://img.shields.io/badge/Spring Kafka-231F20?style=for-the-badge&logo=Apache Kafka&logoColor=white">
<img src="https://img.shields.io/badge/Kotlin Coroutines-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=black">

7️⃣ **빌드 툴**

<img src="https://img.shields.io/badge/Gradle-4053D6?style=for-the-badge&logo=Gradle&logoColor=white">

8️⃣ **Test Libraries**

<img src="https://img.shields.io/badge/MockK-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=black">
<img src="https://img.shields.io/badge/Kotest-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=black">

9️⃣ **Container Tool**

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">

* * *

### **본 프로젝트 모듈의 계층 (Hierarchy of modules in this project)**

1️⃣ Application Layer (Application 역할과 책임을 수행하는 레이어)

* **application-query**
가게 시스템에 대해서 Read Logic만을 처리하는 애플리케이션이다.
domain-queryservice 모듈을 참조하여 해당 모듈에서 짜여진 service code를 controller로 가공하는 역할을 한다.

2️⃣ Domain Layer (Domain과 관련된 역할과 책임을 수행하는 레이어)

* **domain-dynamo**
dynamoDB에 대한 설정과 repository, entity를 담고있는 모듈이다. 동시에 repository 단계에서 redis로의 캐싱을 정의한다.

* **domain-redis**
redis에 대한 설정을 담고있는 모듈이다. domain-dynamo에서 본 모듈을 참조하여 redis에 대한 캐싱을 정의한다

* **domain-queryservice**
domain-dynamo, client-mobilequery, common 모듈을 참조하여 가게 노출에 대한 서비스 로직을 담당하는 모듈이다.

3️⃣ System Layer (Domain과 관련은 없지만, System을 알고있는 코드를 모아둔 레이어)

* **client-mobilequery**
CRUD 중에 R을 위한 DTO 클래스들을 모아둔 레이어이다.
추후에 CQRS 패턴 구현을 위해서 command를 담당하는 client와 분리해둔 상태이다.

4️⃣ Common Layer (System을 모르지만, System을 구성하기 위해 필요한 데이터들을 모아둔 레이어)

# Withmarket-main-server

이 프로젝트는 WithMarket Application의 **가게 노출 서비스**를 담당하는 스프링 기반의 프로젝트입니다.

* * *

### 👉 프로젝트 참여자 (Contributors of this project)
1️⃣ Doyeop Kim (18k7102dy@naver.com)

* * *

### 👉 사용되는 기술 스택 (Tech used in this project)

1️⃣ **사용 언어**

<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=black">

2️⃣ **프로젝트 아키텍처**

* Multi-Module Architecture (멀티모듈 기반의 설계)

3️⃣ **개발 방법론**

* TDD (Test Driven Development)
* DDD (Domain Driven Development)

4️⃣ **사용된 데이터베이스**

<img src="https://img.shields.io/badge/DynamoDB-4053D6?style=for-the-badge&logo=Amazon DynamoDB&logoColor=white">

* * *

### **본 프로젝트 모듈의 계층 (Hierarchy of modules in this project)**

1️⃣ Application Layer (Application 역할과 책임을 수행하는 레이어)

* **application-query**
가게 시스템에 대해서 Read Logic만을 처리하는 애플리케이션이다.
domain-queryservice 모듈을 참조하여 해당 모듈에서 짜여진 service code를 controller로 가공하는 역할을 한다.

2️⃣ Domain Layer (Domain과 관련된 역할과 책임을 수행하는 레이어)

* **domain-dynamo**
dynamoDB에 대한 설정과 repository, entity를 담고있는 모듈이다.

* **domain-queryservice**
domain-dynamo, client-mobilequery, common 모듈을 참조하여 가게 노출에 대한 서비스 로직을 담당하는 모듈이다.

3️⃣ System Layer (Domain과 관련은 없지만, System을 알고있는 코드를 모아둔 레이어)

* **client-mobilequery**
CRUD 중에 R을 위한 DTO 클래스들을 모아둔 레이어이다.
추후에 CQRS 패턴 구현을 위해서 command를 담당하는 client와 분리해둔 상태이다.

4️⃣ Common Layer (System을 모르지만, System을 구성하기 위해 필요한 데이터들을 모아둔 레이어)

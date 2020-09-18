# Spring Webflux Practice Project

## Spring Library(ver_2.3.3)
- Spring Boot

- Spring Reactive Web

- Spring Reactivce Data(MongoDB)
<br/>

```
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive'
	implementation 'org.springframework.boot:spring-boot-starter-webflux'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'de.flapdoodle.embed:de.flapdoodle.embed.mongo'
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
}
```

## 프로젝트 구현 목적
- 스프링 Reactive에 대한 사용법을 미리 익혀 이후 일할 때 가용한 도구로 만들기 위함

- 공부를 하면서 추가적으로 알게 된 지식들을 코드로 작성하여 추후 참고할 목적으로 제작
<br/>

## 구현 예정 Outline
- Webflux 컨트롤러 작성 및 테스트 코드 작성

- 리액티브 데이터베이스 붙이고, 관련 기능 및 테스트 코드 작성

- Spring Webclient를 이용하여 외부 api 호출하는 코드 작성
<br/>

## 세부 구현 가상 Scenario

- 게임 아이템 획득 상황 및 상점 시스템 구현: 

  - 이용자 정보: 아이템 획득, 인벤토리 현황 확인(전체, 타입별), 이벤트 아이템 조회(외부 호출), 종류별 아이템 카운트   
  
  - 상점 시스템: 아이템 구매 및 판매  
  
  - 이벤트 시스템: 이벤트 아이템 전체/선별 지급  
  
- DB는 MongoDB 사용(flapdoodle embed)

- Controller는 Functional한 방식으로 구현(기존 MVC 방식말고)

<br/>

## 클래스 설명
```
domain package
- class User: 유저 정보를 저장하는 클래스
- class ItemInformation: 유저와 상점이 소유한 아이템 정보 클래스 
- class Shop: 상점 정보를 저장하는 클래스

controller package: 
Handler는 기존 MVC에서의 컨트롤러 내부 구조를 담당
Router는 기존 MVC에서의 Mapping 부분을 담당
- UserHandler, UserRouter
- ShopHandler, ShopRouter
- AdminHandler, AdminRouter

repository package: ReactiveMongoRepository를 모두 구현.
- UserRepository
- ItemInformationRepository
- ShopRepository

service package
- InventoryManager: 인벤토리 조회/저장 등의 기능 담당
- ShopManager: 물건 조회/판매/구매 등의 기능 담당
- AdminManager: 운영자의 관점으로 유저들에게 아이템 배포 기능 담당
```

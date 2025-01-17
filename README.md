# FindMe-App

## 프로젝트 소개

🦮**FindMe**는 잃어버린 애완동물을 찾기 위해 크라우드 소싱을 이용하는 서비스입니다.

동물을 잃어버린 사람이 실종 장소와 함께 profile을 업로드하면, 유저들은 해당 제보를 지도 위에서 볼 수 있습니다.
유저는 찾을 동물을 선택한 후, 주변을 돌면서 두 가지 활동을 할 수 있습니다.

- a) 애완동물이 없다 → Not here 제보
- b) 애완동물이 있다 → Here 제보 & 사진 촬영

🗺️ 제보는 지도 위에 시각적으로 표시됩니다.

최근 5분간의 Not here 제보는 heatmap으로 표시되어 찾아볼 범위를 줄이는데 도움을 줍니다. 
Here 제보는 지도에 마커를 띄워서 표시하며, 마커를 클릭하면 사진, 촬영일시, 촬영장소를 조회할 수 있습니다.

*🧪*23명의 테스터를 모집하여 인형을 찾는 테스트를 진행한 결과…

- 35분만에 인형을 찾아냈습니다.
- 유저는 평균 5.8개의 here report를 작성했습니다.
- 참가자 중 실종 동물을 찾는데 관심있는 사람은 60% 뿐이었지만, 이 서비스와 함께라면 90% 이상이 실종 동물을 찾는데 나서겠다고 밝혔습니다.
- ‘게임을 하는 것 같아서 재미있다.’ ‘실종 동물을 돕는데에 많은 노력이 필요하지 않다’가 주요 이유였습니다.
- `HERE` 리포트 제보 접수 이후 10분 이내에 모든 사람들이 타깃을 찾아냈으며, 실종 반려동물을 찾는 일에 관심이 없던 사람들도 해당 서비스와 함께라면 참여할 의사가 있다는 긍정적인 반응을 보였습니다. ( 2.95 → 4.04 / 5.0점 만점)

**프로토 타입 체험하기** ➡ [Go](https://www.figma.com/proto/N8pLIRHiRh46WvIjShYPYu/FindMe?scaling=scale-down&page-id=0%3A1&starting-point-node-id=16%3A22&show-proto-sidebar=1&node-id=16%3A22)

---


## 기술 스택

- Frontend: Android studio, Java
- Backend: Firebase (Firebase Authentication, Firebase Cloud Messaging, Firebase Firestore, Firebase Storage)
- API / Library: Google Map, Glide

---

## 조원 소개

|[김경연](https://github.com/KimGyeongyeon)|[김철환](https://github.com/kadiace)|[박준영](https://github.com/jjjunyeong)|[신정윤](https://github.com/shinbastien)|
|:--:|:--:|:--:|:--:|
User motivation 전략 수립<br>상세정보 화면 담당|firebase 스키마 설계<br>게임 기능 구현|지도 화면 담당<br>서버 관리 어플 개발|로그인 화면 개발<br>Here post 화면 개발<br>기획 매니징

---

## 기능 소개


<img src="https://user-images.githubusercontent.com/67995865/205860396-38e17ad7-0904-42f8-8f15-5c3b50ce3b03.png" height="300px" >



### Main page

> 잃어버린 동물들을 지도상에서 확인할 수 있습니다.
우측 하단 버튼을 누르면 게임을 하거나, 실종 신고를 등록할 수 있습니다.
> 

<img src="https://user-images.githubusercontent.com/67995865/205860094-675fadb0-9c2a-4cb4-9c23-1ae523c88e49.png" height="300px" >

### Pet detail page

> 메인 화면에서 동물 사진을 눌러서 잃어버린 동물의 구체적인 정보를 볼 수 있습니다.
주변을 살펴본 뒤 하단의 `NOT HERE` 버튼을 눌러서 해당 반려동물이 여기에는 없다고 알려줄 수 있습니다.
> 

<div text-align="center">
<img src="https://user-images.githubusercontent.com/67995865/205860127-ec81aee3-74d7-4043-bb2a-e618cfbd5c10.png" height="300px" >
<img src="https://user-images.githubusercontent.com/67995865/205860145-cb1ecdcb-405e-43da-bd7f-9899ce1df096.png" height="300px" >
</div>

### Pet detail Map

> 상세화면 하단의 지도를 눌러서 해당 동물에 대한 신고를 볼 수 있습니다.
`NOT HERE` 신고 결과가 이 페이지에 Heatmap 형태로 나타나서, 붉게 표시된 지역은 동물이 없다는 것을 알려줍니다.
만약 주변을 수색하다가 잃어버린 동물을 찾게되면 이 페이지에서 `Here report` 를 사진과 함께 제출합니다. Here report는 빨간색 마커**📍** 형태로 표시되며, 클릭하면 사진과 시간, 신고자 이름을 볼 수 있습니다.
> 

<img src="https://user-images.githubusercontent.com/67995865/205860120-a8e029cc-d055-4390-aee9-af614b817210.png" height="300px" >

### Game page

> 메인 화면에서 게임 아이콘을 눌러서 시작할 수 있습니다.
상단에는 실종 반려동물의 메인 프로필 고정되어 나타나고, 하단에는 10개의 다른 사진이 나타납니다.
둘이 같은 동물인지, 다른 동물인지 구분하는 간단한 게임입니다.
이 게임을 통해 잘못된 `Here report` 를 걸러낼 수 있습니다.
>

---

## Hard Coded Parts

> Missing pet reports are already uploaded in the App.  
> Game data randomize not implemented.  
> Notification message is send to all users using the app at the same time.  


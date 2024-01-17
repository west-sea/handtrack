# Kinetic Playground
> ### Contributor: 서해린, 조백균
> 2024.01.10 ~ 2024.01.17 <br />
*for 2023 Winter Madcamp Week03 Project* <br/>
<p>
<img alt="Android" src="https://img.shields.io/badge/Android-3DDC84.svg?&style=for-the badge&logo=Android&logoColor=white"/>
</p>
<img src="https://github.com/west-sea/handtrack/assets/128574611/db79f90f-9d30-44ff-a686-612ddd0c3353" width="200px" height="200px"/><br />


### 📁 프로젝트 개요

---

> **추진 배경**
> 
 Mediapipe를 이용해 모션 인식이 용이해졌으나, 대부분 데스크탑 위주의 응용프로그램이 구성되어있고 안드로이드에 응용한 어플리케이션이 부족한 것을 확인

 → Handtracker를 사용해 손동작으로 조작이 가능한 Tetris 게임 구축

 → FaceMesh를 사용해 입으로 공을 삼키는 FaceMesh 게임 구축

> **목표**
> 
- Handtracker 및 FaceMesh의 rendering 결과로 나타난 Landmark들의 상대적인 위치를 실시간으로 계산해 원하는 command를 전달하기

### 📱 구성 및 기능

---

### 1️⃣ Loading to StartMenu
<img src="https://github.com/west-sea/handtrack/assets/128574611/5b02725d-702d-4dc3-a1a3-60e65a8b9a9c" width="300px" height="600px"/> <br />
> - Lottie Animation을 통한 splash animation
> - Tutorial, Tetris, FaceMesh 게임을 선택할 수 있는 StartMenu 창


### 2️⃣ Tutorial
<img src="https://github.com/west-sea/handtrack/assets/128574611/2618c307-5936-452e-89ad-0656905902e1" width="300px" height="600px"/> <br />
> - 사용자가 게임에 대한 친숙도를 높이기 위한 창
> - 화면에 제시된, 방향을 손으로 따라하게 된다면 다음 동작이 제시됨
> - 각 행동을 3번씩 따라하게 된다면, tetris 게임에 대한 설명을 animation으로 보여주고 tetris 게임으로 자동으로 넘어감 (tts 재생됨)
> 
> 
### 3️⃣ Tetris 
<img src="https://github.com/west-sea/handtrack/assets/128574611/900da673-5bd0-43e6-9b07-1caa78afa0df" width="300px" height="600px"/> <br />
> - Rule: tetris 기존 게임과 동일함
> - Command[손 모양]

>     1) 실시간으로 촬영된 모습이 FrameLayout에 표시됨
>     
>     2) 인식된 손 모양에 따른 글자와 그림이 실시간으로 표시됨 
>     
>     - 주먹: 블록이 돌아감
>     - 가위: 블록이 빨리 내려감
>     - 왼쪽: 블록이 왼쪽으로 이동
>     - 오른쪽: 블록이 오른쪽으로 이동
> - GameOver
>     - 화면
>         
>        
>         
>     - 사용자의 점수
>     - 최고 기록 점수
>     - RETRY 버튼 누르면, 새로 게임이 시작
>     
### 4️⃣ FaceMesh
<img src="https://github.com/west-sea/handtrack/assets/128574611/5e613f19-119b-47e1-934c-13a27b250427" width="300px" height="600px"/> <br />
> - Rule
>     - 원안에 있는 돌아다니는 공들을 모두 먹으면 성공
>     - 공들은 크기, 속도, 방향 모두 무작위
>     - 입안으로 들어와야 공이 사라지기 때문에 최대한 입의 너비를 넓히는게 유리함
> - 게임화면
> 
>         
>     - 화면 상단: 시간 측정기, 남은 공의 수
>     - 화면 중앙: FaceMesh 실시간 반영, 생성된 공
>     - 화면 하단: Retry 버튼
>     - 게임이 끝나면, Game Clear 화면으로 변환됨

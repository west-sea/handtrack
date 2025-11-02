# 🎮 Kinetic Playground

<p align="center">
  <img src="https://github.com/west-sea/handtrack/assets/128574611/db79f90f-9d30-44ff-a686-612ddd0c3353" width="200px" height="200px"/>
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-3DDC84.svg?&style=for-the-badge&logo=Android&logoColor=white"/>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-7F52FF.svg?&style=for-the-badge&logo=Kotlin&logoColor=white"/>
  <img alt="MediaPipe" src="https://img.shields.io/badge/MediaPipe-0097A7.svg?&style=for-the-badge&logo=Google&logoColor=white"/>
</p>

## 📖 프로젝트 소개

**Kinetic Playground**는 Google MediaPipe를 활용하여 손동작과 얼굴 인식으로 제어하는 인터랙티브 게임 애플리케이션입니다.
터치 대신 카메라를 통한 제스처 인식으로 게임을 플레이할 수 있는 혁신적인 사용자 경험을 제공합니다.

### 📅 개발 정보
- **개발 기간**: 2024.01.10 ~ 2024.01.17 (1주)
- **프로젝트**: 2023 Winter Madcamp Week03 Project
- **개발자**: 서해린, 조백균

---

## 💡 프로젝트 배경

MediaPipe를 이용한 모션 인식 기술이 발전했으나, 대부분 데스크탑 위주의 응용프로그램으로 구성되어 있고 안드로이드 기반 애플리케이션이 부족한 상황을 확인했습니다.

**해결책**
- ✋ **Hand Tracker**: 손동작 인식을 통한 테트리스 게임 구현
- 👄 **Face Mesh**: 얼굴 인식을 활용한 입으로 공 먹기 게임 구현

**목표**
- Hand Tracker 및 Face Mesh의 렌더링 결과로 나타난 Landmark들의 상대적인 위치를 실시간으로 계산하여 게임 명령으로 변환

---

## 🚀 주요 기능

### 1️⃣ Splash & Start Menu
<img src="https://github.com/west-sea/handtrack/assets/128574611/5b02725d-702d-4dc3-a1a3-60e65a8b9a9c" width="300px" height="600px"/>

- Lottie Animation을 활용한 세련된 스플래시 화면
- Tutorial, Tetris, FaceMesh 게임 선택 메뉴

### 2️⃣ Interactive Tutorial
<img src="https://github.com/west-sea/handtrack/assets/128574611/2618c307-5936-452e-89ad-0656905902e1" width="300px" height="600px"/>

- 게임 조작법을 단계별로 학습할 수 있는 인터랙티브 튜토리얼
- 화면에 표시된 방향을 손동작으로 따라하면 다음 단계로 진행
- 각 동작을 3번씩 성공하면 테트리스 게임 설명 애니메이션 재생 (TTS 지원)
- 튜토리얼 완료 후 자동으로 테트리스 게임 시작

### 3️⃣ Hand Gesture Tetris
<img src="https://github.com/west-sea/handtrack/assets/128574611/900da673-5bd0-43e6-9b07-1caa78afa0df" width="300px" height="600px"/>

**게임 규칙**
- 기본적인 테트리스 게임 규칙을 따름
- 손동작으로 블록 제어

**손동작 명령어**
| 손동작 | 기능 |
|--------|------|
| 👊 주먹 | 블록 회전 |
| ✌️ 가위 | 블록 빠르게 낙하 |
| 👈 왼쪽 | 블록 왼쪽 이동 |
| 👉 오른쪽 | 블록 오른쪽 이동 |

**게임 화면**
- 실시간 카메라 영상이 FrameLayout에 표시
- 인식된 손동작에 따른 명령어 아이콘 실시간 표시
- MediaPipe Hand Landmarks 시각화

**게임 오버**
- 현재 점수 및 최고 기록 표시
- RETRY 버튼으로 게임 재시작

### 4️⃣ Face Mesh Ball Catcher
<img src="https://github.com/west-sea/handtrack/assets/128574611/5e613f19-119b-47e1-934c-13a27b250427" width="300px" height="600px"/>

**게임 규칙**
- 화면에 떠다니는 모든 공을 입으로 먹어야 클리어
- 공의 크기, 속도, 방향은 무작위로 생성
- 입 안으로 공이 들어와야 사라지므로 입을 크게 벌리는 것이 유리

**게임 화면 구성**
- **상단**: 경과 시간, 남은 공 개수 표시
- **중앙**: Face Mesh 실시간 렌더링 및 공 애니메이션
- **하단**: Retry 버튼
- 게임 클리어 시 축하 화면 표시

---

## 🛠️ 기술 스택

### Android
- **Language**: Kotlin, Java
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 33 (Android 13)
- **Compile SDK**: 34

### Core Libraries
- **MediaPipe**: 손동작 및 얼굴 인식
  - `solution-core:latest.release`
  - `hands:latest.release`
  - `facemesh:latest.release`
- **Lottie**: 고품질 애니메이션 (`5.0.2`)
- **TedPermission**: 권한 관리 (`3.3.0`)

### UI/UX
- Material Design Components
- ViewBinding & DataBinding
- ConstraintLayout
- Custom OpenGL Rendering

---

## 📦 설치 및 실행

### 요구사항
- Android Studio Arctic Fox 이상
- JDK 8 이상
- Android SDK 26 이상
- 카메라가 있는 Android 기기 또는 에뮬레이터

### 설치 방법

1. **저장소 클론**
```bash
git clone https://github.com/west-sea/handtrack.git
cd handtrack
```

2. **프로젝트 열기**
```bash
# Android Studio에서 프로젝트 열기
# File > Open > handtrack 폴더 선택
```

3. **Gradle 동기화**
```bash
# Android Studio에서 자동으로 Gradle 동기화 진행
# 또는 터미널에서 실행:
./gradlew build
```

4. **앱 실행**
- Android 기기를 USB로 연결하거나 에뮬레이터 실행
- Run 버튼 클릭 (Shift + F10)

### 권한 설정
앱 실행 시 다음 권한이 필요합니다:
- 📷 **카메라**: 손동작 및 얼굴 인식을 위해 필수

---

## 📁 프로젝트 구조

```
handtrack/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/myapplication/
│   │   │   │   ├── StartMenu/
│   │   │   │   │   ├── SplashActivity.kt          # 스플래시 화면
│   │   │   │   │   └── StartMenuActivity.java     # 메인 메뉴
│   │   │   │   ├── GameRules/
│   │   │   │   │   ├── TetrisRuleActivity.java    # 테트리스 튜토리얼
│   │   │   │   │   └── FacemeshRuleActivity.java  # FaceMesh 튜토리얼
│   │   │   │   ├── FaceMesh/
│   │   │   │   │   ├── FaceMeshActivity.kt         # FaceMesh 게임 메인
│   │   │   │   │   ├── Ball.java                   # 공 객체
│   │   │   │   │   ├── CustomBallView.java         # 공 렌더링
│   │   │   │   │   └── FaceMeshResultGLRenderer.kt # OpenGL 렌더러
│   │   │   │   ├── MainActivity.java               # 테트리스 게임 메인
│   │   │   │   ├── HandsResultGIRenderer.kt        # Hand Tracking 렌더러
│   │   │   │   ├── GestureActionListener.java     # 제스처 이벤트 처리
│   │   │   │   ├── Draw.java                       # 테트리스 게임 로직
│   │   │   │   ├── blocks.java                     # 테트리스 블록
│   │   │   │   └── TetrisGameOverActivity.java    # 게임 오버 화면
│   │   │   ├── res/
│   │   │   │   ├── layout/                         # XML 레이아웃
│   │   │   │   ├── drawable/                       # 이미지 리소스
│   │   │   │   └── raw/                            # Lottie JSON, 사운드
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                   # 유닛 테스트
│   │   └── androidTest/                            # 인스트루먼트 테스트
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🎯 핵심 기술 구현

### 1. MediaPipe Hand Tracking
```java
// 손동작 인식 및 제스처 분류
- Landmark 21개 포인트 실시간 추적
- 손가락 각도 계산을 통한 제스처 분류
- 주먹/가위/왼쪽/오른쪽 제스처 인식
```

### 2. MediaPipe Face Mesh
```kotlin
// 얼굴 메시 478개 포인트 추적
- 입술 Landmark를 활용한 입 영역 계산
- 공과 입 영역의 충돌 감지
- 실시간 OpenGL 렌더링
```

### 3. 게임 로직
- 테트리스 블록 생성 및 회전 알고리즘
- 줄 제거 및 점수 계산
- 공 물리 엔진 (속도, 방향, 충돌)

---

## 🎥 데모 영상

> *추후 YouTube 링크 또는 GIF 추가 예정*

---

## 🤝 기여자

| <img src="https://github.com/identicons/user1.png" width="100"> | <img src="https://github.com/identicons/user2.png" width="100"> |
|:---:|:---:|
| **서해린** | **조백균** |
| [@west-sea](https://github.com/west-sea) | - |

---

## 📄 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Google MediaPipe](https://google.github.io/mediapipe/) - 손동작 및 얼굴 인식 솔루션
- [Lottie Android](https://github.com/airbnb/lottie-android) - 애니메이션 라이브러리
- [TedPermission](https://github.com/ParkSangGwon/TedPermission) - Android 권한 관리

---

<p align="center">
  Made with ❤️ for 2023 Winter Madcamp Week03 Project
</p>

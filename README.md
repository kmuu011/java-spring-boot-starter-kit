# java-spring-boot-starter-kit

## Prerequisites
- **Java 17**
- **Docker Compose v2**

## Linux 환경에서 개발 서버 실행 방법
1. **실행 권한 부여**  
   `chmod +x ./commands/*` 실행

2. **컨트롤러 실행**  
   `commands/dev-first-run.sh` 파일을 실행합니다.

3. **서버 실행**  
   IDE에서 프로젝트를 실행할때 spring.profiles.active=dev로 돌 수있도록 설정

## Linux 환경에서 Production 서버 무중단 배포 방법
prod-first-run.bat을 이미 실행한 후 상황에서  
수정사항이 생겼을 경우 아래 과정을 진행하면 됩니다.
1. **이미지 빌드**  
   `commands/prod-build-server-image.bat` 실행
2. **컨테이너 순차적 재구동**  
   `commands/prod-release-server.bat` 실행


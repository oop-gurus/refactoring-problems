# Refactoring Problems 001 - Accounts Api
- 계좌의 입출금 상태를 모델링하는 리팩토링 문제
- Api와 Database를 통해 상태를 추적/관리 해야하는 도메인을 모델링 한다.

## 학습 목표
1. Boolean 타입을 사용하지 않고 도메인 상태를 모델링 할 수 있다.
2. 도메인 테스트의 범위를 이해하고 구현할 수 있다.
3. 객체지향 도메인 모델링을 통해, 폭발적으로 증가하는 통합 테스트 개수를 억제할 수 있다.
4. Type-Safe 이 가져다주는 강점을 활용하여 잠재적인 버그를 찾아낼 수 있다.

## 요구 사항
- 시간 제한 : 180분
- 아래 클래스 내에서는 도메인 로직을 다루지 않는다.
    - `com.gitub.lette1394.refactoringproblems.accounts.AccountService`
    - `com.gitub.lette1394.refactoringproblems.accounts.AccountController`
    - `com.gitub.lette1394.refactoringproblems.accounts.AccountEntity`
    - `com.gitub.lette1394.refactoringproblems.accounts.AccountNotificationApi`
    - `com.gitub.lette1394.refactoringproblems.accounts.AccountRepository`
- `~/http-request` 디렉토리 내의 통합 테스트를 모두 통과한다.
- `~/http-requests` 디렉토리 내의 통합 테스트를 모두 도메인 테스트로 변경한다.
- `if`, `if-else`, `else` 지시어를 가능한 적게 사용한다.
- `com.gitub.lette1394.refactoringproblems.accounts.Account` 클래스에 새로운 상태가 추가되더라도, 도메인 테스트의 개수를 폭발적으로 늘리지 않게 테스트를 작성한다.
- Boolean 타입을 메서드 인자로 전달하지 않는다.
- 누락된 테스트 케이스를 찾고, 도메인 테스트에 추가한다.

## 문제 풀기
1. repository fork
2. 새로운 branch 생성
3. 요구사항 확인 후 리팩토링 진행
4. 리팩토링 완료 후 PR 생성
5. PR에 문제 풀이 설명 추가


### Test coverage

**Service tests**
* `AdminCardServiceImplTest`
* `UserCardServiceImplTest`
* (and other service tests in `src/test/java`)

**Controller tests (MockMvc)**
* `UserAccountControllerTest`
* `UserCardControllerTest`
* `AdminCardControllerTest`

Controller tests are located under:
* `src/test/java/com/example/bank_service/accounting/controller`
* `src/test/java/com/example/bank_service/card/controller`

> Tip: controller tests focus on HTTP contract (status codes + JSON), while services are tested separately with business logic.

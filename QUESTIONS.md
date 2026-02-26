# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I'd refactor the data access layer to be more consistent. Right now we have three different strategies:

• Panache entities (Store) — entity is tightly coupled to persistence. Quick to write but painful for unit testing; you can't easily mock Store.findById() without a real DB. Persistence leaks into the REST layer too.

• PanacheRepository (Product) — better, you can mock the repo. But you're still passing JPA entities up to the REST handler, so any DB schema change hits the API contract.

• Custom repository + port (Warehouse) — Hexagonal style. Domain model is independent of JPA, use cases depend only on port interfaces, and you can swap the DB adapter without touching business logic. More boilerplate upfront but pays off in testability and maintainability.

If I were maintaining this long-term I'd move everything toward the Warehouse pattern. Not in one big bang — module by module when we touch that code.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
OpenAPI / codegen (Warehouse):
• Pros — contract-first, single source of truth, generated types. Good when other teams or clients need a stable spec.
• Cons — we had to add a filter for 201 because the generator didn't support it; you're a bit at the mercy of the tool. Spec has to be right upfront.

Hand-coded (Store, Product):
• Pros — full control, easy to tweak status codes and validation, no generator quirks.
• Cons — no machine-readable contract unless we write OpenAPI separately; more chance of drift later.

My choice: I wouldn't force one approach everywhere. For internal or fast-moving APIs I'm fine with hand-coded. For external or shared APIs I'd use OpenAPI and codegen so the contract is clear. I'd decide based on whether it's internal vs external and how much we care about a formal contract.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I'd prioritize in this order:

• Use case / business logic tests (first) — Cover the core rules: duplicate BU codes, capacity limits, stock matching, max warehouses per location, etc. Fast (mock the ports), no DB needed, and they protect what actually matters. If a capacity check breaks we could oversell warehouse space. I'd cover happy path, each validation failure, and boundary cases (e.g. capacity exactly at limit).

• Integration / REST tests (second) — A handful per resource with REST Assured, hitting the real HTTP layer. They catch wiring issues like the @Transactional not binding through the proxy we ran into. I wouldn't test every validation via HTTP — just main flows and that error codes map correctly.

• Repository tests — A few against real Postgres (DevServices) for custom queries like countActiveByLocation, totalCapacityByLocation. A JPQL typo can silently return wrong data so worth checking.

• CDI / observer tests — For the Store legacy sync I'd test that the observer fires only after a successful commit and stays silent on rollback. Easy to break during refactors.

Keeping coverage useful over time: rely on the 80% JaCoCo gate in CI so PRs can't drop coverage. Name tests after the business rule they protect, not the method — helps the next person see why the test exists and if it's still relevant.
```
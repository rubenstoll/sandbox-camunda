# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Hands-on Camunda 8 examples: a Spring Boot app that automates a BPMN process ("Process 1" — check inventory → charge payment → ship items) using self-managed Camunda 8 job workers via `camunda-spring-boot-starter` 8.9.0. It's a learning sandbox progressing from a fully automated BPMN/DMN flow toward service-worker based process automation (see README.md for the Camunda "Getting Started" guides it follows).

## Commands

Use the Maven wrapper (`./mvnw`), not a system-installed Maven.

- Build: `./mvnw clean install`
- Compile only: `./mvnw compile`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=CamundaSandboxApplicationTests`
- Run a single test method: `./mvnw test -Dtest=CamundaSandboxApplicationTests#shouldCompleteProcessInstance`
- Run the app: `./mvnw spring-boot:run` (main class: `CamundaSandboxApplication`)

Requires Java 25 (`java.version` in pom.xml).

## Architecture

- **Two `@SpringBootApplication` classes exist**: `CamundaSandboxApplication` (package `ch.toto.sandobox.camundasandbox`, the actual runnable entry point) and `ProcessOrderApplication` (package `...camundasandbox.process_order`, appears to be leftover tutorial scaffolding). Because `CamundaSandboxApplication` sits in the parent package, its component scan picks up everything under `process_order` anyway — treat `ProcessOrderApplication` as dead/unused rather than a second entry point.
- **Job workers** live in `process_order/` as `@Component` classes with `@JobWorker(type = "...")`-annotated methods (`CheckInventoryWorker`, `ChargePaymentWorker`, `ShipItemsWorker`). Each corresponds to a `zeebe:taskDefinition type` in a BPMN service task. Job variables are bound via `@Variable(name = "...")` parameters; a worker returns a `Map<String, String>` to set output process variables.
- **BPMN process definitions** under `src/main/resources/*.bpmn` are auto-deployed to the Camunda cluster on startup by `camunda-spring-boot-starter`'s default classpath resource scanning — no manual deployment step is needed for files placed there. `Quick Start_ Human Tasks.bpmn` is an unrelated tutorial file with no linked workers.
- **Cluster connectivity** is configured in `application.properties` for `mode: self-managed` against a local Zeebe gRPC (`26500`) and REST (`8080`) endpoint. `compose.yaml` currently defines no services — a Camunda 8 cluster (Zeebe/Operate/etc.) must be started separately (e.g. via Camunda Desktop Modeler's "Start instance" or an external docker-compose stack) before running the app or its process-level tests.
- **Testing**: `camunda-process-test-spring` (`@CamundaSpringProcessTest`) spins up an in-memory/test Camunda engine per test class. Tests deploy a BPMN resource explicitly (e.g. `src/test/resources/order-process.bpmn`), start a process instance by `bpmnProcessId`, stub job workers with `processTestContext.mockJobWorker(type).thenComplete()`, and assert outcomes with `CamundaAssert.assertThat(processInstance).isCompleted()`. This lets process-flow tests run without the real workers or a live cluster. `TestcontainersConfiguration`/`TestCamundaSandboxApplication` wire in Testcontainers-backed dev services for the plain `SpringBootTest` context.
- The pom's `maven-resources-plugin` copy-bpmn-files execution copies `*.bpmn` from `src/main/resources/bpmn` into test resources during `process-test-resources`. Since `src/main/resources/bpmn` is already on the main/test classpath as a normal resource directory, this execution is now redundant with default Maven resource inheritance — kept as-is unless it causes an actual conflict.

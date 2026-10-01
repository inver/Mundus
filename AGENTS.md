# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Mundus (Gradle root project name `abyssus`) is a desktop 3D world/scene editor built on LibGDX + VisUI, written in Java
(Java 11 target) with some Kotlin. Scenes/projects are saved as JSON + assets. Save-file formats change freely; backward
compatibility is not a goal. The upstream repo is `mbrlabs/Mundus`; this is a fork (`inver/Mundus`, default CI branch
`develop`).

## Commands

Uses the Gradle wrapper (JDK 11+; CI uses Corretto 11).

- Build everything: `./gradlew build`
- Verify (tests + checkstyle): `./gradlew check`
- Run the editor: `./gradlew :app-editor:run` (working dir is `projects/app-editor/assets`; adds `-XstartOnFirstThread`
  on macOS automatically)
- Fat jar: `./gradlew :app-editor:distEditor`
- Tests for one module: `./gradlew :lib-core:test`
- Single test class / method: `./gradlew :app-editor:test --tests 'com.mbrlabs.mundus.editor.core.SomeTest.someMethod'`
- Checkstyle only: `./gradlew checkstyleMain` (config in `config/checkstyle/`)

Tests use JUnit 5 (+ Mockito); every `Test` task is finalized by `jacocoTestReport`. Pitest is applied to all modules.

## Modules (`projects/`)

`settings.gradle` maps each module to `projects/<name>/<name>.gradle`.

- `lib-commons` (`com.mbrlabs.mundus.commons`) — runtime-usable library: scene graph (`scene3d`), terrain, skybox,
  shaders, asset/model loaders, DTOs for the saved format. Intended to be usable in games, so it should only depend on
  LibGDX, Lombok and small utility libs — keep editor/Spring code out.
- `lib-core` (`net.nevinsky.abyssus.core`) — newer rendering core: meshes, models, nodes, shaders, builders, providers.
  Depends on LibGDX, gdx-gltf, artemis-odb (ECS), JTS.
- `lib-assets` (`net.nevinsky.abyssus.lib.assets`) — asset import using LWJGL Assimp (native libs for
  linux/macos/windows are all bundled).
- `app-editor` (`com.mbrlabs.mundus.editor`) — the editor application, entry point `Main`. Depends on `lib-commons` and
  `lib-core`.

Note the two package roots (`com.mbrlabs.mundus` legacy vs `net.nevinsky.abyssus` new): the codebase is mid-migration
from the original Mundus code to the new `abyssus` libs.

## Editor architecture

- **DI via Spring**, editor module only (see `docs/Architecture.md`). `config/InitListener` builds an
  `AnnotationConfigApplicationContext(RootConfig.class)`; `RootConfig` component-scans the editor packages. Services,
  importers, dialogs and large UI modules (e.g. Inspector) are Spring beans (`@Component`/`@Autowired`). `lib-*` modules
  must not use Spring.
- **UI layer uses Model-View-Presenter**, built from VisUI widgets; see `ui/` (`modules`, `widgets`, `dsl`, `ecs`).
  `ui/ecs` holds the inspector components for entity components (the in-progress work on this branch is read-only
  model/material/texture inspector widgets).
- **Entities** use artemis-odb ECS (`core/ecs`: `EcsService` and components such as `PickableComponent`,
  `ModelPropertiesComponent`) alongside the legacy component-based scene graph in `lib-commons`.
- **Picking** (`tools/picker`) is color-id based: entities are rendered into an offscreen framebuffer with unique colors
  and the pixel under the cursor is read back. Tool handles (translate/rotate gizmos) use a separate picker.
- **Tools / brushes** in `tools/` and `tools/brushes`; terrain editing in `terrain/`; undo/redo in `history/`; app-wide
  event bus types in `events/`; project/scene/asset/shader registries in `core/` (`project`, `scene`, `registry`,
  `assets`, `shader`, `light`); export to JSON + assets in `exporter/`.
- **Shaders** can be Groovy-scripted/bundled (Groovy + JSR223 are dependencies; see `src/test/resources/bundledShader`).
- **Resources/data model** (`docs/Scene.md`): the editor keeps a library of assets/shaders in `~/.mundus`; a project
  contains scenes; each scene references the assets/materials/shaders added to it; the asset tab shows assets from all
  scenes of the project.

## Build notes

- `app-editor` sets `assets` as both a Java source dir and resource dir, in addition to `src/main` (sources live
  directly under `src/main/com/...`, not `src/main/java`). Tests are in `src/test/java` and `src/test/kotlin`.
- Lombok is enabled in all modules (including tests).
- Further docs: `docs/Architecture.md`, `docs/Scene.md`, `docs/Terrain.md`.

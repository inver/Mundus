# Architecture

## Modules

- Common - can be use in games. It depends on only from LibGdx, Lombok and some utils libraries like Slf4j.
- Editor - Mundus application module.

DI - Spring, only for application module.

- Use spring for service classes, like Importers, or for dialogs and big UI modules like Inspector.
- Use Model-View-Presenter architecture for UI layer.

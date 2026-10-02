# Convenciones del proyecto

## Convenciones de ramas

- Funcionalidades nuevas: `feature/<nombre>` (ej. `feature/gmf-decorator`).
- Correcciones: `fix/<nombre>` (ej. `fix/daily-limit-reset`).
- Nunca se hace commit directo a `main`; se integra con merge desde la rama.

## Convenciones de commits

Formato: `<tipo>: <descripción en inglés, en imperativo>`

| Tipo | Uso |
|---|---|
| `feature` | Nueva funcionalidad |
| `fix` | Corrección de errores |
| `docs` | Documentación |
| `refactor` | Cambio interno sin alterar comportamiento |
| `test` | Pruebas |
| `chore` | Configuración, build, mantenimiento |

Ejemplos: `feature: add GMF tax decorator`, `fix: reject negative amounts`.

## Reglas de código

- Todo el código (clases, métodos, variables, comentarios) en inglés. Solo la interfaz y el README en español.
- POO: una clase por archivo.
- Arquitectura hexagonal: `domain` no depende de `application` ni de `infrastructure`.
- Los decoradores viven en `application/decorator` y extienden `TransferDecorator`.
- Los commits solo llevan como autores a los integrantes del equipo. No se agregan herramientas de IA como coautores (sin trailers `Co-Authored-By`).

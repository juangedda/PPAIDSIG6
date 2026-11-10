# PPAI 2026 · Bolsines · CU 28 Registrar recepción de bolsín

Diseño de Sistemas de Información · UTN FRC · 3K5 · Grupo 06

Implementación del CU 28 con el rediseño por el **patrón State sobre Documentación** (Entrega 2).
Sólo se implementa este caso de uso.

| Parte | Tecnología |
|---|---|
| Lenguaje y framework | Java 21 · Spring Boot 4.1.1 (igual que el TPI de Backend) |
| Tecnología | Web: API REST + front en React |
| Base de datos y persistencia | H2 en modo archivo · Spring Data JPA / Hibernate |
| Front | React 19 · Vite 7 · Material UI 7 · Axios (misma base que el repo de la cátedra) |

## Estructura

```
ppai-bolsines/
├── back/                 Spring Boot (Maven)
│   └── src/main/java/ppai/bolsines/
│       ├── pantalla/     boundary: PantallaRegistroRecepcionBolsines (@RestController)
│       ├── gestor/       control: GestorRegistroRecepcionBolsines (@Service)
│       ├── modelo/       entidades del dominio + jerarquía del patrón State
│       ├── dto/          records que devuelve / recibe la pantalla
│       ├── repositorio/  Spring Data JPA
│       └── config/       beans (reloj del sistema)
├── front/                React + Vite + MUI (wizard con los pasos del CU)
├── docs/
│   ├── catedra/          ERS, descripción del caso, consignas, modelo de dominio
│   ├── diagramas/        diagramas de diseño finales (clases, secuencia con State, máquina de estados) y proyectos de EA
│   ├── sesiones/         registro de cada sesión de trabajo
│   └── PENDIENTE.txt     plan de trabajo en orden
├── AGENTS.md             reglas del proyecto (también para asistentes de IA)
├── CONTEXTO_IA.md / .pdf estado del trabajo y cómo seguir (para el grupo y para pasarle a una IA)
└── COMO_EJECUTAR.txt     paso a paso para levantarlo en una computadora nueva
```

## Requisitos

- JDK 21
- Node.js 22 LTS (Vite 7 pide 20.19 o superior)

## Cómo ejecutar (Windows, PowerShell)

Paso a paso completo, con la prueba del CU y los problemas comunes: `COMO_EJECUTAR.txt`.

```powershell
# 1. Back -> http://localhost:8080 (la base H2 se crea sola en back/data)
cd back
.\mvnw.cmd spring-boot:run

# 2. Front (otra terminal) -> http://localhost:3000
cd front
npm install
npm run dev
```

Abrir http://localhost:3000. La usuaria logueada es Laura Gómez, Encargada de Bolsines de la
Comisión Médica Córdoba; aparecen los bolsines con precinto 5001, 5002 y 5005. Al confirmar la
recepción, la notificación del CU 29 se ve en la consola del back. Las pruebas del patrón State
se corren con `.\mvnw.cmd test`.

Cada arranque del back recrea las tablas y carga `data.sql`, así la demo siempre empieza igual.
Para ver las tablas: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/bolsines`,
usuario `sa`, sin contraseña).

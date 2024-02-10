# Propentia

## Tech "Stack"

**Propentia** is currently engineered as a single page application with no backend (where all components reside in this monorepo).

- [React w/ TS](https://reactjs.org/): Frontend framework.
- [Material UI](https://material-ui.com/): For easier styling.
- [Vite](https://vitejs.dev/): For faster development.
- [Kotlin w/ Gradle](https://kotlinlang.org/): For the logic.

ReactTS, Material, and Vite integrate seamlessly. Kotlin is compiled to JS with TS annotations.

If server-side components are required in the future, ...

## Getting Started
Compile the Kotlin to JS
```bash
npm compile-kotlin
```
Install NPM
```bash
npm install
```
Link the generated TypeScript declaration files to the logic node module.
Add the following line to `logic/build/js/packages/logic/package.json`:
```json
  "types": "kotlin/logic.d.ts",
```
Update the logic npm module
```bash
npm run update-logic
```
Start vite
```bash
npm run dev
```
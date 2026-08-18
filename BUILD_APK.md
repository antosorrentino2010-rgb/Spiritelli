# Generare l'APK automaticamente

## Metodo consigliato: GitHub Actions

1. Crea un repository GitHub vuoto.
2. Carica **tutti i file e le cartelle** di questo progetto.
3. Vai nella scheda **Actions**.
4. Apri **Build APK**.
5. Premi **Run workflow**.
6. Al termine apri l'esecuzione completata e scarica l'artifact **Collezioni-debug-apk**.
7. Estrai lo ZIP dell'artifact: dentro trovi `app-debug.apk`.
8. Copia l'APK sul telefono e installalo.

Il workflow usa JDK 17 e Gradle 8.10.2, genera automaticamente il Gradle Wrapper e compila una APK debug installabile.

## Nota
Questa è una build debug. Per pubblicare sul Play Store servirà una build release firmata con una chiave di firma.

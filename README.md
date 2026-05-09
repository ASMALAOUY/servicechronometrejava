# TP — Chronomètre Service Android (Java)

## Objectif
Créer une application Android en Java utilisant un **Foreground Service** et un **Bound Service** pour faire tourner un chronomètre en arrière-plan avec une notification persistante.

## Technologies utilisées
- Java
- Android Foreground Service
- Bound Service (IBinder / LocalBinder)
- NotificationManager & NotificationChannel
- ScheduledExecutorService
- Handler (mise à jour UI)

## demo


https://github.com/user-attachments/assets/3e8f43e8-ca4e-4a32-b000-866db46b3525



## Fichiers principaux
| Fichier | Rôle |
|---|---|
| `ChronometreService.java` | Service qui gère le timer et la notification |
| `MainActivity.java` | Interface utilisateur + connexion au service |
| `activity_main.xml` | Layout avec TextView et 2 boutons |
| `AndroidManifest.xml` | Déclaration du service et permissions |

## Permissions requises
- `POST_NOTIFICATIONS`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_DATA_SYNC`

## Fonctionnement
1. Clic sur **DÉMARRER** → lance le Foreground Service + affiche la notification
2. Le chronomètre continue même si l'app est fermée
3. Clic sur **ARRÊTER** → stoppe le service et supprime la notification

## Compatibilité
- Minimum SDK : API 24
- Testé sur : Android 14 (API 34)

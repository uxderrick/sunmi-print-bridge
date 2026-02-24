# Sunmi Print Bridge

Headless companion APK for the banking kiosk web app. Receives `sunmiprint://` URLs from Chrome, prints receipts on the Sunmi V3H's built-in 58mm thermal printer via AIDL, and closes immediately.

## How it works

```
Chrome (Print button tap)
  → window.location.href = "sunmiprint://receipt?data=<base64-JSON>"
  → Android opens PrintActivity (registered for sunmiprint:// scheme)
  → PrintActivity decodes JSON, binds AIDL printer service, prints, finish()
  → User is back in Chrome instantly
```

## Build (no local tools needed)

### Option 1: GitHub Actions (recommended)
1. Push this repo to GitHub
2. Go to **Actions** → **Build APK** → **Run workflow**
3. Download the APK from the build artifacts
4. Transfer to V3H via USB/ADB and install

### Option 2: Local build (requires JDK 17 + Android SDK)
```bash
cd android/sunmi-print-bridge
./gradlew assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

## Install on device
```bash
adb install app-debug.apk
```
Or transfer the APK file to the device and tap to install (enable "Install from unknown sources" first).

## JSON receipt format

The web app sends base64-encoded JSON via the URL:

```json
{
  "title": "Queue Ticket",
  "brand": "MTN",
  "status": "Submitted",
  "ticketId": "TKD-095",
  "date": "24/02/2026, 10:30:00 AM",
  "message": "Your ticket information has been sent to you by SMS."
}
```

All fields are optional except `title`.

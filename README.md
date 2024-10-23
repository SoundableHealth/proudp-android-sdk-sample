# proudP Android SDK usage guide

### 1. Open the build.gradle(app-level) file and add soundablehealth sdk.

```kotlin
dependencies {
    implementation("io.github.soundablehealth:1.0.3")
}
```

### 2. Initialize SDK

- Set 3 server keys to connect to the Soundable Health server.
- Please contact dev@soundable.health to get these keys.

```kotlin
SoundableHealth.init(
  apiKey = "SERVER_API_KEY",
  serverUrl = "SERVER_URL",
  socketUrl = "WEBSOCKET_URL"
)
```

### 3. Start recording

- Enter 3 key values before recording
    - id: user's de-identified ID
    - gener: male(m) or female(f)
    - clinic : clinic name or company name
- Audio recording permission is required.

```kotlin
SoundableHealth.start(
  context = context,
  user = User(
      id = "USER_ID",
      gender = "USER_GENDER",
      clinic = "CLINIC_NAME"
  ),
  onError = { message: String -> 
    // TODO, handle recording error
  },
  onRecording = { sec: Int -> 
    // TODO, record time callback
  },
)
```

### 4. Cancel recording

- Cancel recording and delete the recorded file.

```kotlin
SoundableHealth.cancel()
```

### 5. Stop recording

- Finish recording and upload the recording file to the server.
- Analysis results can be processed through callback.

```kotlin
SoundableHealth.stop(
  onError = { message: String ->
      // TODO, handle error
  }
  onFinish = { analysisResult: AnalysisResult ->
      // TODO, handle the received result
  }
)
```

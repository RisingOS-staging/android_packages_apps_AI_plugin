# Oxygen Customizer - AI Plugin

## This is a plugin for Oxygen Customizer, which adds AI features to extract the subject.

You can always use this plugin in your app, look at the [Usage](#-usage) section.

## Table of Contents

- [How it works](#-how-it-works)
- [Credits](#-credits)
- [License](#-license)

# 🤖 How it works

This plugin uses the [removebg](https://github.com/AppcentMobile/removebg) library to remove the background from an image.

# 🚀 Usage

Use permission 
```xml
<uses-permission android:name="it.dhd.oxygencustomizer.aiplugin.REQUEST_EXTRACT_SUBJECT" />
```
in your app.

Send a broadcast with the image source path and the destination path to save the extracted image.

```java
Intent intent = new Intent("it.dhd.oxygencustomizer.aiplugin.REQUEST_EXTRACT_SUBJECT");
intent.putExtra("sourcePath", "/path/to/image.png");
intent.putExtra("destinationPath", "/path/to/save/extracted/image.png");
sendBroadcast(intent);
```

If something goes wrong, the plugin will send a broadcast with the error message.
Intent action: `it.dhd.oxygencustomizer.aiplugin.ACTION_EXTRACT_FAILURE`

If the extraction is successful, the plugin will send a broadcast to the calling package.
Intent action: `it.dhd.oxygencustomizer.aiplugin.ACTION_EXTRACT_SUCCESS`

NOTE: The generated subject is always compressed as [PNG](./app/src/main/java/it/dhd/oxygencustomizer/aiplugin/receivers/SubjectExtractionReceiver.java).

# ❤ Credits

### Thanks to:

- [erenalpaslan](https://github.com/erenalpaslan) for his [work](https://github.com/AppcentMobile/removebg).

# © License

This project is licensed under GPLv3. Please see [`LICENSE`](./LICENSE.md) for the full license text.
Portions of this project include code licensed under the MIT License:
- [removebg](https://github.com/AppcentMobile/removebg)
- Copyright (c) [2023] [Eren Alpaslan]

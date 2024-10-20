# Oxygen Customizer - AI Plugin

## This is a plugin for Oxygen Customizer, which adds AI features to extract the subject.

You can always use this plugin in your app, look at the [Usage](#-usage) section.

## Table of Contents

- [How it works](#-how-it-works)
- [Credits](#-credits)
- [License](#-license)

# 🤖 How it works

This plugin uses some code from [removebg](https://github.com/AppcentMobile/removebg) library to remove the background from an image using pytorch.
This plugin also uses ONNX Runtime to run AI Models on Android.

# 🚀 Usage

Send a broadcast with the image source path and the destination path to save the extracted image.
Remember to add a package name, so the app will send an intent on success or failure.

```java
Intent intent = new Intent("it.dhd.oxygencustomizer.aiplugin.REQUEST_EXTRACT_SUBJECT");
intent.putExtra("sourcePath", "/path/to/image.png");
intent.putExtra("destinationPath", "/path/to/save/extracted/image.png");
intent.setPackage("your.package.name");
sendBroadcast(intent);
```

If something goes wrong, the plugin will send a broadcast with the error message.
Intent action: `it.dhd.oxygencustomizer.aiplugin.ACTION_EXTRACT_FAILURE`

If the extraction is successful, the plugin will send a broadcast to the calling package.
Intent action: `it.dhd.oxygencustomizer.aiplugin.ACTION_EXTRACT_SUCCESS`

NOTE: The generated subject is always compressed as [PNG](./app/src/main/java/it/dhd/oxygencustomizer/aiplugin/receivers/SubjectExtractionReceiver.java).

# ❤ Credits

### Thanks to:

- Microsoft for [ONNX Runtime](https://github.com/microsoft/onnxruntime)
- [danielgatis](https://github.com/danielgatis/) for his [rembg](https://github.com/danielgatis/rembg)
- [erenalpaslan](https://github.com/erenalpaslan) for his [work](https://github.com/AppcentMobile/removebg).

# 📝 License

This project is licensed under GPLv3. Please see [`LICENSE`](./LICENSE.md) for the full license text.
Portions of this project include code licensed under the MIT License:

- [removebg](https://github.com/AppcentMobile/removebg)
- Copyright (c) [2023] [Eren Alpaslan]

- [rembg](https://github.com/danielgatis/rembg)
- Copyright (c) [2020] Daniel Gatis

# ⭐ Donations

[<img src=".github/resources/PayPal.svg"
alt='Donate with PayPal'
height="80">](https://www.paypal/luigifale) [<img src=".github/resources/BMC.svg"
alt='Donate with BMC'
height="80">](https://www.buymeacoffee.com/DHD2280)
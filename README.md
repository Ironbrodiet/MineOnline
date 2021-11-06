![logo](mineonlinelogo.png)

[![Build status](https://ci.appveyor.com/api/projects/status/hd3fpaffd9qqqx6d?branch=main&svg=true)](https://ci.appveyor.com/project/craftycodie/mineonline)

Launch old versions of minecraft just as you remembered them.

## What is MineOnline?
MineOnline is a launcher Minecraft capable of running web applets without a browser, and redirecting web requests to a new API.

For example, if you wanted to play classic right now, you'd have no way to launch it without a lot of outdated vulnerable software and luck. And even if you pulled it off, you'd be running a stipped down version of the game, with no skins, no server authentication and no online map saving. MineOnline fixes this. The program can also run regular desktop versions of the game.

**Compatible with Java 8u261+**

[Download](https://github.com/craftycodie/MineOnline/releases/latest)

[Known Issues](https://github.com/craftycodie/MineOnline/discussions/300)

## Features
These are features MineOnline will bring to Minecraft.

- Skins and Cloaks
- Sounds (the right ones for each version!)
- Server List (accessible in launcher and game)
- FOV Slider
- Microsoft Login Support
- Classic World Saving
- Screenshots (F2)
- Texture packs (for versions before Alpha 1.2.2, when it was officially added)
  - Custom font and animated texture support.
- GUI scaling (for versions before Beta 1.5, when it was officially added)
- Full Discord Integration
- Tab player list.
- Secure Server Authentication (online-mode and verify-names)
- Resizable & Fullscreenable Applets with Mac and Linux Support
- M1 Mac Support
- Bit depth fix (removes jagged lines from clouds and other stuff).

![launcher](launcherdemo.png)

## For Modders
You can add custom version information to the launcher by creating a version info file at `.mineonline\custom-version-info\`.
The file should be in a directory named client or server, and it's name should contain the version name and md5.
For example:
`.mineonline\custom-version-info\client\Skylands 0.1 F8F78A4ED4033547CC1EA28C776DA7AE.json`
This file should contain an array of JSON versions, like this:

```json
{ 
  "name": "Skylands 0.1", 
  "md5": "F8F78A4ED4033547CC1EA28C776DA7AE", 
  "type": "client",
  "baseVersion": "b1.7.3",
  "info": "Beta 1.7.3 mod",
  "legacy": true
}
```

Legacy is true for any pre-1.6 minecraft version.
For 1.6 and above, libraries should be provided. Examples can be found [here](https://github.com/craftycodie/MineOnline/blob/master/res/version-info/client/).

I recommend you start with the unmodded version information as a template, as most of the settings will carry over.

If you would like a version to be added to the main list, contact me [@craftycodie](github.com/craftycodie/).

## For Developers
As per the license you are welcome to use the launcher code under non-commercial conditions.

I also request that forks remain up to date for security.

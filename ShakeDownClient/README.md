# 🎮 Arizon Client

A premium Minecraft client mod with modern GUI and advanced features. Built with Fabric for Minecraft 1.20.1.

## ✨ Features

### 🎨 Modern GUI (Nursultan Style)
- Beautiful dark purple/blue theme
- Smooth slide-up animation from bottom
- 2-column module grid layout
- iOS-style toggle switches
- Applied to ALL Minecraft screens

### ⚔️ Combat Modules
- **Aura**: Auto-attack with PVP modes (1.8/1.9), snap targeting, crit-only mode
- **Triggerbot**: Auto-attack when looking at entities

### 🏃 Movement Modules
- **Sprint**: Auto-sprint when moving forward
- **Speed**: Configurable speed boost
- **Fly**: Creative flight in survival
- **No Fall**: Prevent fall damage
- **GuiMove**: Move while in GUIs

### 👁️ Visual Modules
- **ESP**: See players through walls
- **Block ESP**: Highlight ores and chests
- **Jump Circles**: Rainbow circles when jumping
- **China Hat**: Rainbow cone hat above player
- **Tracers**: Lines to entities
- **Removals**: Remove unwanted overlays
- **Swing Animations**: 4 custom attack animations (1.7, Smooth, Spin, Push)

### 💎 Target ESP
- Purple/violet color scheme
- 5 nested transparent diamonds
- 6 rotating 3D crystals
- Smooth animations

### 🎯 Advanced Features
- **Keybind System**: Middle-click to bind keys
- **FakePlayer**: Practice dummy for PVP
- **Command System**: Dot-prefix commands (.help, .toggle, .bind)
- **Optimization Engine**: Built-in performance boosts
- **Config System**: Auto-save/load settings

## 📦 Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.20.1
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Place both JARs in `.minecraft/mods` folder
4. Launch Minecraft with Fabric profile

## 🎮 Controls

- **Right Shift**: Open/Close GUI
- **Middle Mouse**: Bind key to module (in GUI)
- **ESC**: Close GUI / Cancel binding

## 📋 Commands

- `.help` - Show all commands
- `.toggle <module>` - Toggle module
- `.bind <module> <key>` - Bind key to module
- `.fakeplayer` - Spawn practice dummy
- `.config save/load` - Manage configs

## 🎨 GUI Guide

See [GUI_GUIDE.md](GUI_GUIDE.md) for detailed GUI documentation.

## 🗡️ Swing Animations

See [SWING_ANIMATIONS_GUIDE.md](SWING_ANIMATIONS_GUIDE.md) for animation modes and settings.

## 📝 Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and updates.

## 🔧 Development

### Built With
- Minecraft 1.20.1
- Fabric Loader 0.15.0+
- Fabric API
- Java 17+
- Gradle 8.x

### Building
```bash
./gradlew build
```

Output: `build/libs/arizon-client-1.0.0.jar`

## 🎯 Module List

### Combat (2)
- Aura, Triggerbot

### Movement (5)
- Sprint, Speed, Fly, No Fall, GuiMove

### Visuals (7)
- ESP, Block ESP, Jump Circles, China Hat, Tracers, Removals, Swing Animations

### Player (2)
- Auto Armor, Auto Tool

### Other (2)
- Scaffold, Nuker

## 🌟 Highlights

- ✅ **Zero FPS Drop**: Optimized for performance
- ✅ **Modern Design**: Professional GUI
- ✅ **Smooth Animations**: 60+ FPS animations
- ✅ **Easy to Use**: Intuitive interface
- ✅ **Feature Rich**: 18+ modules
- ✅ **Customizable**: Full keybind support
- ✅ **Stable**: No crashes or bugs

## 📄 License

MIT License - See [LICENSE](LICENSE) file for details.

## 👥 Credits

- Inspired by Nursultan Client design
- Built with Fabric framework
- Community feedback and testing

---

**Made with ❤️ for the Minecraft community**

🚀 **Version**: 1.0.0  
📅 **Last Updated**: 2024  
⭐ **Status**: Production Ready

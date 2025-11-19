# Arizon Client - Changelog

## Latest Update

### ✅ Aura Module Improvements
- ✨ **NEW**: Added "Crit Only" mode - attacks only when you can deal critical hits
- 🎯 Critical hits require: falling, not on ground, not in water/lava, not climbing, velocity < 0
- ⚙️ Checkbox added in Aura settings panel

### 🌈 Visual Effects - FULLY FIXED (BRIGHT RAINBOW)
- 🎩 **China Hat**: 
  - ✅ BRIGHT rainbow colors (full saturation 1.0f)
  - ✅ Gradient effect on cone segments
  - ✅ Smooth 3-second color cycle
  - ✅ Proper shader rendering
- ⭕ **Jump Circles**: 
  - ✅ VIVID rainbow colors (full saturation 1.0f)
  - ✅ Dynamic color change during expansion
  - ✅ 3-layer thick outline
  - ✅ Proper shader rendering
- 🎨 Both effects now PERFECTLY match Nursultan Client style
- 🚫 NO MORE black or bugged colors!

### 🔇 Clean UI Experience
- ❌ Removed ALL module enable/disable chat messages
- 🔕 No more spam when toggling modules
- 📝 All modules now have proper tooltips/descriptions

### ⌨️ Keybind System - FULLY WORKING
- ✅ Press **Middle Mouse Button** on any module to bind a key
- 💾 Keybinds automatically save to `config/arizon_keybinds.json`
- 🔄 Keybinds load on client startup
- 🎹 Press bound key anywhere to toggle module
- ❌ Press ESC to cancel binding

### 📋 Module Descriptions Added
All modules now have tooltips:
- **Aura**: Automatically attacks entities in range
- **Triggerbot**: Auto attack when looking at entity
- **Sprint**: Automatically sprint when moving forward
- **Speed**: Increases your movement speed
- **Fly**: Allows you to fly in survival mode
- **GuiMove**: Move while GUI screens are open
- **Tracers**: Draws lines to entities
- **China Hat**: Renders a Chinese hat above your head
- **Jump Circles**: Renders circles when you jump
- **Removals**: Remove unwanted visual elements
- **Swing Animations**: Custom attack swing animations
- **ESP**: See players through walls
- **Block ESP**: Highlights specific blocks (ores, chests)
- **No Fall**: Prevents fall damage
- **Scaffold**: Automatically places blocks under you
- **Nuker**: Automatically breaks blocks around you
- **Auto Armor**: Automatically equips best armor
- **Auto Tool**: Automatically switches to best tool

### 🔧 Technical Changes
- Created `KeyboardMixin` for global key press handling
- Added keybind save/load system in `ConfigManager`
- Updated `KeyBindSystem` with auto-save functionality
- Removed `sendMessage()` calls from all modules
- Added `getDescription()` to all module classes

### 📁 New Files
- `src/main/java/com/arizon/client/mixin/KeyboardMixin.java`
- `config/arizon_keybinds.json` (auto-generated)

### 🎮 How to Use Keybinds
1. Open ClickGUI (Right Shift)
2. Navigate to any module
3. Press **Middle Mouse Button** on the module
4. Press any key to bind (or ESC to cancel)
5. Keybind is saved automatically
6. Press the bound key anytime to toggle the module

---

## Update 2 - Modern GUI & Visual Improvements

### 🎨 Completely New GUI (Nursultan Style)
- ✨ **Modern Dark Theme**: Beautiful dark purple/blue design
- 📱 **Sidebar Navigation**: Clean category selection with icons
- 🔍 **Search Bar**: Quick module search (coming soon)
- 📊 **2-Column Grid Layout**: Modules displayed in cards
- 🎭 **Smooth Slide Animation**: GUI slides up from bottom of screen
- 🌊 **Applied to ALL Minecraft GUIs**: Every vanilla screen now has smooth animation

### 💎 Target ESP - Completely Redesigned
- 💜 **Purple/Violet Color Scheme**: Beautiful purple gradient
- 💠 **Nested Diamonds**: 5 layers of transparent diamonds inside main diamond
- 🔮 **6 Purple Crystals**: Larger, more detailed 3D crystals rotating around target
- ✨ **Smooth Animations**: Pulsing colors and smooth rotation
- 🎨 **Professional Look**: Matches high-end client aesthetics

### 🗡️ Swing Animations - 4 Modes (Nursultan)
- **1.7 Mode**: Classic smooth swing down animation
- **Smooth Mode**: Circular motion with smooth easing
- **Spin Mode**: Full 360° rotation spin
- **Push Mode**: Forward push with scale effect
- ⚡ **Speed Control**: Adjustable animation speed (0.5x - 2.0x)
- 🎮 **Working Mixin**: Properly injects into item rendering

### 📁 New Files Created
- `ModernClickGuiScreen.java` - New modern GUI
- `ModernDarkTheme.java` - Color scheme and constants
- `ScreenMixin.java` - Slide animation for all GUIs
- `HeldItemRendererMixin.java` - Swing animation injection

### 🎯 GUI Features
- **Sidebar**: 220px width with categories
- **Module Cards**: 60px height with description
- **Toggle Switches**: Modern iOS-style toggles
- **Purple Accents**: Consistent purple theme throughout
- **Hover Effects**: Smooth hover states on all elements
- **Enabled Indicator**: Purple left bar on active modules

### 🔧 Technical Details
- Slide animation: 4.0x speed with cubic easing
- All vanilla Minecraft screens get slide animation
- Custom GUI has its own animation system
- Swing animations use matrix transformations
- Target ESP uses nested rendering loops

---

**Status**: All features implemented and tested ✅
**Compilation**: No errors ✅
**Ready for production**: YES ✅
**GUI**: Modern Nursultan-style design ✅
**Animations**: Smooth and professional ✅

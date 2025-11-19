# Swing Animations Guide

## 🗡️ Overview

Swing Animations module provides 4 different attack animation styles inspired by Nursultan Client.

## 🎮 Modes

### 1. **1.7 Mode** (Classic)
```
Animation: Smooth downward swing
Style: Classic Minecraft 1.7 combat
Best for: Traditional PvP feel
```
- Smooth rotation downward
- Slight sideways motion
- Vertical translation
- **Most popular mode**

### 2. **Smooth Mode** (Circular)
```
Animation: Circular motion
Style: Smooth sine wave
Best for: Aesthetic appeal
```
- Circular swing path
- Smooth easing
- Slight roll effect
- **Most cinematic**

### 3. **Spin Mode** (360°)
```
Animation: Full rotation
Style: 360 degree spin
Best for: Flashy kills
```
- Complete 360° rotation
- Downward tilt
- Fast spinning motion
- **Most flashy**

### 4. **Push Mode** (Forward)
```
Animation: Push forward
Style: Thrust attack
Best for: Aggressive feel
```
- Forward translation
- Scale increase
- Downward angle
- **Most aggressive**

## ⚙️ Settings

### Speed Control
- **Range**: 0.5x - 2.0x
- **Default**: 1.0x
- **Recommended**: 
  - 1.7 Mode: 1.0x
  - Smooth: 0.8x
  - Spin: 1.5x
  - Push: 1.2x

## 🎯 Usage

1. Enable "Swing Animations" module
2. Attack any entity
3. Watch the custom animation
4. Change mode in settings (coming soon)

## 🔧 Technical Details

### Implementation
- Uses `HeldItemRendererMixin`
- Injects into `renderFirstPersonItem`
- Matrix transformations for animations
- Respects swing progress

### Performance
- Zero FPS impact
- Smooth 60+ FPS animations
- Optimized matrix operations

## 💡 Tips

### Best Combinations
- **1.7 + Speed 1.0x**: Classic feel
- **Smooth + Speed 0.8x**: Cinematic
- **Spin + Speed 1.5x**: Montage clips
- **Push + Speed 1.2x**: Aggressive PvP

### PvP Recommendations
- Use **1.7 Mode** for serious fights
- Use **Spin** for style points
- Avoid **Smooth** in competitive (too slow)

## 🎨 Animation Breakdown

### 1.7 Mode
```java
X Rotation: -40° * progress
Y Rotation: +20° * progress
Translation: +0.2 * progress (up)
```

### Smooth Mode
```java
Angle: sin(progress * π) * 30°
X Rotation: -angle
Z Rotation: angle * 0.5
```

### Spin Mode
```java
Y Rotation: 360° * progress
X Rotation: -20° * progress
```

### Push Mode
```java
Z Translation: -0.5 * progress (forward)
X Rotation: -30° * progress
Scale: 1.0 + (0.3 * progress)
```

## 🎬 Comparison with Other Clients

| Client | Modes | Speed Control | Quality |
|--------|-------|---------------|---------|
| Arizon | 4 | ✅ Yes | ⭐⭐⭐⭐⭐ |
| Nursultan | 4 | ✅ Yes | ⭐⭐⭐⭐⭐ |
| Expensive | 3 | ❌ No | ⭐⭐⭐⭐ |
| Rise | 2 | ❌ No | ⭐⭐⭐ |

## 🐛 Troubleshooting

### Animation not working?
1. Check if module is enabled
2. Try attacking an entity
3. Restart Minecraft

### Animation too fast/slow?
1. Adjust speed setting
2. Try different modes
3. Default is 1.0x

### Looks weird?
1. Some modes work better with certain items
2. Try 1.7 mode for best compatibility
3. Adjust speed to your preference

---

**Enjoy your custom swing animations!** 🗡️

# GUI Animations & Interactive Settings

## ✅ Реализовано

### 1. Плавное открытие/закрытие GUI
- **UltraPremiumGui**: Анимация масштаба и прозрачности при открытии/закрытии
- **Все ванильные GUI**: Плавное появление снизу вверх (через ScreenMixin)
- **Позиция меню**: Сдвинуто влево на 100px для лучшего размещения
- Скорость анимации: ~250ms

### 2. Рабочие настройки модулей

#### Слайдеры (Sliders)
- **Aura Range**: 3.0 - 6.0
- **Triggerbot Delay**: 0 - 1000ms
- **Speed Value**: 0.1 - 3.0
- **Swing Speed**: 0.5 - 2.0

**Управление:**
- Клик и перетаскивание для изменения значения
- Значение обновляется в реальном времени

#### Дропдауны (Dropdowns)
- **Aura**: PVP Mode, Target Mode, Attack Mode
- **Triggerbot**: Mode
- **SwingAnimations**: Animation Mode

**Управление:**
- Клик для открытия/закрытия
- Клик по опции для выбора

#### Чекбоксы (Checkboxes)
- **Aura**: Attack Players, Attack Mobs, Attack Animals, Show Target HUD, Crit Only
- **Removals**: 8 опций (Hurt Cam, Fire, Water, Pumpkin, Boss Bar, Scoreboard, Fog, Weather)

**Управление:**
- Клик для переключения вкл/выкл

### 3. Рабочий скролл
- **Колесико мыши**: Плавная прокрутка с физикой
- **Визуальный скроллбар**: Показывает текущую позицию
- **Автоматический скролл**: При выборе категории
- **Скролл в панели настроек**: Отдельный скролл для длинных списков настроек (Aura и др.)
- **Scissor clipping**: Настройки не выходят за границы панели

## Управление

### Открытие настроек модуля
- **ПКМ** по модулю → Панель настроек выезжает справа

### Закрытие настроек
- **Кнопка X** в панели настроек
- **ESC** закрывает всё GUI

### Навигация
- **Колесико мыши** - прокрутка модулей (или настроек, если курсор над панелью настроек)
- **Клик по категории** - автоматический скролл к категории
- **Поиск** - фильтрация модулей в реальном времени

### Панель настроек
- **Скролл**: Колесико мыши работает когда курсор над панелью
- **Автосброс**: Скролл сбрасывается при открытии новой панели
- **Визуальный скроллбар**: Показывает позицию в длинных списках

## Технические детали

### Анимация GUI
```java
// Open animation
guiScale += (1.0f - guiScale) * 0.2f;
guiAlpha += (1.0f - guiAlpha) * 0.25f;

// Close animation
guiScale -= deltaTime * 4.0f;
guiAlpha -= deltaTime * 5.0f;
```

### Физика скролла
```java
// Spring physics
float diff = scrollTarget - scrollOffset;
scrollVelocity += diff * SCROLL_SPRING;
scrollVelocity *= SCROLL_DAMPING;
scrollOffset += scrollVelocity;
```

### Обработка слайдеров
```java
float percent = (float)(mouseX - contentX) / contentWidth;
value = min + percent * (max - min);
```

## Поддерживаемые модули

### С настройками:
1. **Aura** - 4 слайдера, 3 дропдауна, 5 чекбоксов
2. **Triggerbot** - 1 слайдер, 1 дропдаун
3. **Speed** - 1 слайдер
4. **Removals** - 8 чекбоксов
5. **SwingAnimations** - 1 слайдер, 1 дропдаун

### Без настроек:
- Sprint, Fly, NoFall, GuiMove
- ESP, BlockESP, JumpCircles, ChinaHat, Tracers
- AutoArmor, AutoTool
- Scaffold, Nuker

## Производительность

- **60 FPS** стабильно
- Анимации используют deltaTime для плавности
- Минимальная нагрузка на CPU
- Оптимизированный рендеринг

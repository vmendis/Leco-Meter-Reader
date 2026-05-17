# LECO Smart Meter Analyzer & Solar Planning App

## Project Overview

### Objective
Build a simple but scalable Android application for personal electricity usage tracking and solar planning analysis.

The app will:
- Capture LECO smart meter readings twice daily. Meter readings may occasionally be unavailable for various reasons, such as the user forgetting to read the meter or being unavailable to do so. 
- Store data locally on the Android device
- Calculate electricity usage patterns
- Produce graphs and analytics
- Estimate electricity costs using LECO TOU (Time Of Use) tariffs
- Help size a future off-grid or hybrid solar installation

Development Environment:
- Windows 11
- Android Studio Panda
- RooCode AI Agent

Preferred Initial Approach:
- Offline-first
- Single user
- Local database
- Simple manual entry
- Expandable architecture

---

# 1. Functional Requirements

## 1.1 Meter Reading Capture

The user will manually enter electricity meter readings.

There are four cumulative readings from the LECO smart meter:

1. Total Reading
2. Rate 1 - Day Usage
3. Rate 2 - Off-Peak Usage
4. Rate 3 - Peak Usage

All readings are cumulative kWh values.

Typical capture times:
- Morning around 09:00
- Evening around 21:00

Actual times may vary.

The app must store:
- Exact timestamp
- Raw cumulative readings
- Optional notes/comments

---

## 1.2 Time Of Use (TOU) Categories

Approximate LECO TOU windows:

| Category | Time Window |
|---|---|
| Off-Peak | 22:30 – 05:30 |
| Day | 05:30 – 18:30 |
| Peak | 18:30 – 22:30 |

Approximate tariff values:

| Category | Approximate Cost |
|---|---|
| Off-Peak | LKR 13–24 |
| Day | Moderate |
| Peak | LKR 50–70 |

The app should support configurable tariff values.

---

# 2. Clarification Questions

## 2.1 User Model

- Single user
- Offline only
- No login/accounts
- No cloud sync initially
- Local-only storage

---

## 2.2 Target Devices

- Android phone only
- Portrait orientation priority
- Tablet support not required
- Landscape mode not required initially

---

## 2.3 Data Entry Method

User manually types:
- Total Reading 
- Rate 1 Day Usage
- Rate 2  Off-Peak Usage
- Rate 3 Peak Usage

Phase 1 Scope:
- Manual entry only
- OCR/camera capture excluded from initial build
- Fast numeric-entry workflow is critical
- Large keypad UX preferred

---

## 2.4 Validation Rules

Required validation features:

- Readings must never decrease
- Usage delta auto-calculation
- Warning if totals mismatch
- Detection of impossible values
- Duplicate reading detection
- Meter readings may occasionally be unavailable for various reasons, such as the user
forgetting to read the meter or being unavailable to do so

Validation behaviour:
- Invalid entries must be blocked
- Validation errors must be shown clearly
- Save operation must fail if validation fails
- User must correct invalid data before save

Additional recommendation:
- Validation engine should be modular for future rule additions

---

## 2.5 Cost Estimation

Required features:

- Daily electricity cost estimate
- Monthly projected bill
- TOU category cost breakdown
- Historical cost trends

Critical Requirement:
- Calculations must mimic LECO billing calculations as closely as possible
- Support exact TOU tariff calculations
- Support fixed monthly charges
- Tariff values must be configurable
- Tariff history should support future tariff changes
- Build billing engine as an isolated service/module
- Make tariff rules configurable from database

---

## 2.6 Solar Planning Features - Not required in MVP

Phase 1 Requirement:
Start with Advanced solar planning capabilities.

Required Phase 1 features:

- Solar panel sizing
- Battery sizing
- Inverter sizing
- Solar offset estimation
- Grid dependency analysis
- Average daily usage calculations
- Day vs night usage analysis
- Peak consumption analysis

Future enhancements:

### Very Advanced
- ROI/payback calculator
- Weather integration
- Solar production simulation
- Exportable reports

Recommendation:
- Solar planning engine should be modular
- Allow configurable assumptions:
  - Sun hours
  - Battery depth of discharge
  - System efficiency
  - Solar panel wattage

---

## 2.7 Graphs & Analytics

Required graphs:

- Daily total usage
- Weekly trends
- Day vs Off-Peak vs Peak comparison

Recommended graph style:

- Modern Material 3 design
- Clean minimal UI
- Interactive charts
- Smooth animations
- Dark mode friendly

Recommended chart types:

| Graph | Recommended Type |
|---|---|
| Daily usage | Line chart |
| Weekly trends | Bar + line combination |
| TOU comparison | Stacked bar chart |
| Cost trends | Line chart |

Recommended chart library:
- Vico Charts

---

## 2.8 Export Features

Required export format:

- CSV export

Recommended CSV capabilities:

- Export raw readings
- Export calculated usage
- Export cost calculations
- Export solar analysis summaries

---

## 2.9 Data Retention

Expected usage:
- Short-term analysis exercise
- Primarily for pre-solar-installation analysis
- Long-term archival not required

Design implication:
- Database volume will remain relatively small
- Performance optimization for massive datasets not required

---

## 2.10 App Complexity Preference
- Modern 
- Graphs
- Tabs
- Material Design


Requirements:
- Modern Material 3 UI
- Graphs and analytics
- Clean navigation
- Tabs/navigation bar
- Good UX polish
- No unnecessary enterprise complexity

---

# 3. Recommended Technical Architecture

## Core Stack

Recommended:

- Kotlin
- Jetpack Compose
- Room Database
- Material 3
- MVVM Architecture

---

## Local Database

Recommended:
- Room Database

Reasons:
- Reliable
- Offline-first
- Structured queries
- Scalable
- RooCode-friendly

---

## Graphing Libraries


Recommendation:
- Vico Charts or MPAndroidChart

---

# 4. Suggested Database Design

## 4.1 Raw Meter Readings Table

| Field | Type |
|---|---|
| id | Long |
| timestamp | DateTime |
| totalReading | Double |
| rate1Day | Double |
| rate2OffPeak | Double |
| rate3Peak | Double |
| notes | String |
| createdAt | DateTime |

---

## 4.2 Calculated Usage Table

Derived from differences between consecutive readings.

| Field | Type |
|---|---|
| id | Long |
| fromReadingId | Long |
| toReadingId | Long |
| totalUsed | Double |
| dayUsed | Double |
| offPeakUsed | Double |
| peakUsed | Double |
| estimatedCost | Double |
| calculationTimestamp | DateTime |

---

## 4.3 Tariff Configuration Table

| Field | Type |
|---|---|
| id | Long |
| dayRate | Double |
| offPeakRate | Double |
| peakRate | Double |
| effectiveDate | DateTime |

---

# 5. Suggested Application Screens

## Phase 1 Screens

### Splash Screen
Simple app logo.

### Dashboard
Displays:
- Latest reading
- Daily usage
- Estimated daily cost
- Quick stats

### Add Reading Screen
Fields:
- Timestamp
- Total Reading
- Rate 1  Day Usage
- Rate 2  Off-Peak Usage
- Rate 3  Peak Usage
- Notes

### History Screen
Displays all readings.

### Reading Detail Screen
Displays:
- Usage calculations
- Cost breakdown
- Validation warnings

---

## Phase 2 Screens - not in MVP

### Analytics Screen
Charts and trends.

### Cost Analysis Screen
Electricity bill estimates.

### TOU Breakdown Screen
Usage by tariff category.

---

## Phase 3 Screens - not in MVP

### Solar Planner
Inputs:
- Desired grid independence
- Battery reserve days
- Solar efficiency assumptions

Outputs:
- Suggested solar panel size
- Battery bank estimate
- Inverter recommendation

---

# 6. Suggested Development Phases

## Phase 1 — Core Logging App

Goals:
- Manual data entry
- Local database
- CRUD operations
- Usage calculations
- Validation rules
- Simple dashboard

---

## Phase 2 — Analytics & Graphs

Goals:
- Charts
- Trend analysis
- Cost estimation
- Monthly summaries

---

## Phase 2.5 — Solar Planning Features (High Priority) - not in MVP

Note:
Solar planning features are considered high priority and should begin immediately after analytics implementation.

Primary goals:
- Battery sizing
- Solar sizing
- Inverter sizing
- Grid dependency calculations
- Day vs night consumption analysis
- Solar offset estimation

---

## Phase 3 — Advanced Features - not in MVP

Goals:
- Battery sizing
- Solar sizing
- Off-grid calculations
- Usage simulations

---

## Phase 4 — Advanced Features - not in MVP

Possible additions:
- OCR meter scanning
- Cloud backup
- Multi-device sync
- Widgets
- Notifications/reminders

---

# 7. Recommended Validation Logic

Potential rules:

- New readings must be >= previous readings or blank
- Delta calculations auto-generated
- Total delta should roughly equal Rate1 + Rate2 + Rate3 deltas
- Warn on abnormal spikes
- Warn on duplicate timestamps
- Strict blocking

---

# 8. Suggested UX Features

Possible UX improvements:

- Dark mode
- Quick-add presets
- Large numeric keypad
- Smart defaults for timestamps
- Auto-focus field progression
- Data backup/export
- Swipe-to-edit

---

# 9. RooCode AI Agent Guidance

The project should be built incrementally.

Recommended RooCode workflow:

1. Create project skeleton
2. Build Room database layer
3. Create data models/entities
4. Build repository layer
5. Build ViewModels
6. Build manual entry UI
7. Build dashboard UI
8. Implement calculations
9. Add graphs
10. Add export features
11. Add solar calculations

---

# 10. Possible breakdown of tasks

## 1 — Create Project Skeleton

Create a modern Android application using:
- Kotlin
- Jetpack Compose
- Material 3
- MVVM architecture
- Room Database

The application is an electricity smart meter logger for Sri Lankan LECO Time Of Use (TOU) electricity readings.

Create:
- Project structure
- Navigation
- Theme
- Base screens
- Room setup
- Repository pattern
- Dependency injection structure

Use clean architecture principles.

---

## 2 — Build Database Layer

Create Room entities, DAOs, repositories and database classes for:

1. MeterReadings
2. CalculatedUsage
3. TariffConfiguration

Use Kotlin coroutines and Flow.

---

## 3 — Build Reading Entry Screen

Create a Jetpack Compose screen for entering smart meter readings.

Fields:
- Timestamp
- Total Reading
- Rate 1 Day
- Rate 2 Off-Peak
- Rate 3 Peak
- Notes

Requirements:
- Validation
- Numeric keyboard for meter readings. Use large Keypad with big buttons and big display.
- Material 3 design
- Save button
- Snackbar error handling

---

# 11. Notes & Future Ideas

Potential future enhancements:

- AI anomaly detection
- Solar ROI calculator- Battery discharge simulation
- Weather API integration
- Home appliance usage tracking
- MQTT smart meter integration
- BLE/WiFi smart meter sync
- Home Assistant integration
- Automatic reminders at 09:00 and 21:00

---

# 12. Reference Information

Approximate LECO TOU timing:

| Category | Time |
|---|---|
| Off-Peak | 22:30 – 05:30 |
| Day | 05:30 – 18:30 |
| Peak | 18:30 – 22:30 |

Approximate TOU pricing:

| Category | Approximate Cost |
|---|---|
| Off-Peak | LKR 13–24 |
| Day | Moderate |
| Peak | LKR 50–70 |

Note:
Tariff rates should be configurable because LECO pricing changes periodically.


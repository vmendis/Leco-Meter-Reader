# Leco-Meter-Reader
- Room Database
- MVVM Architecture
- Vico Charts
- Kotlin Coroutines & Flow

---

# Project Goals

The primary objective of this application is to:

1. Capture accurate LECO smart meter readings
2. Analyse household electricity consumption patterns
3. Understand peak and off-peak usage behaviour
4. Estimate realistic electricity costs
5. Determine the most cost-effective solar installation strategy

---

# Planned Features

## Phase 1
- Meter reading capture
- Validation engine
- Local database
- Dashboard
- Usage calculations

## Phase 2
- Graphs and analytics
- Cost estimation engine
- CSV export

## Phase 3
- Solar sizing tools
- Battery calculations
- Inverter recommendations
- Grid dependency analysis

## Future Ideas
- OCR meter reading capture
- ROI calculator
- Solar simulation
- Weather integration
- Home Assistant integration

---

# Validation Rules

The app validates:

- Readings never decrease
- Duplicate entries
- Impossible values
- TOU total mismatches
- Invalid calculations

Invalid readings are blocked from being saved.

---

# Architecture

The application follows:

- MVVM architecture
- Repository pattern
- Offline-first design
- Modular service structure

---

# Development Environment

- Windows 11
- Android Studio Panda
- RooCode AI Agent

---

# Current Status

Project currently in active development.

---

# License

MIT License

---

# Disclaimer

This application is an independent personal analysis tool and is not affiliated with LECO or CEB.

Electricity tariff calculations are estimates and should be verified against official LECO billing information.
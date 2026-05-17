# Solar Planning Screen Architecture

## System Architecture Diagram

```mermaid
graph TB
    subgraph "UI Layer"
        SS[SolarScreen<br/>Main Screen]
        SIF[SolarInputForm<br/>Input Components]
        SRD[SolarResultsDisplay<br/>Results Cards]
        SC[ScenarioComparison<br/>Scenario Management]
        SP[SolarProductionChart<br/>Visualizations]
    end
    
    subgraph "ViewModel Layer"
        SPV[SolarPlanningViewModel<br/>State Management]
        SC[SolarCalculations<br/>Business Logic]
        BS[BatterySizing<br/>Battery Calculations]
        PS[PanelSizing<br/>Panel Calculations]
        IS[InverterSizing<br/>Inverter Calculations]
    end
    
    subgraph "Data Layer"
        SPD[SolarPlanningData<br/>Data Models]
        SR[SolarRepository<br/>Data Persistence]
        LS[Local Storage<br/>Room Database]
    end
    
    subgraph "Components"
        IC[Input Components<br/>NumberInput, Selector]
        CC[Chart Components<br/>Line Charts, Bar Charts]
        RC[Result Components<br/>Cards, Metrics]
    end
    
    subgraph "External Services"
        WS[Weather Service<br/>Sun Hours Data]
        CS[Cost Service<br/>Electricity Rates]
        LS[Location Service<br/>Regional Data]
    end
    
    %% Connections
    SS --> SPV
    SIF --> SPV
    SRD --> SPV
    SC --> SPV
    SP --> SPV
    
    SPV --> SC
    SPV --> BS
    SPV --> PS
    SPV --> IS
    
    SPV --> SPD
    SPV --> SR
    
    SR --> LS
    
    SC --> IC
    SRD --> RC
    SP --> CC
    
    SPV --> WS
    SPV --> CS
    SPV --> LS
    
    %% Style
    classDef ui fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef viewModel fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef data fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef components fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef external fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    
    class SS,SIF,SRD,SC,SP ui
    class SPV,SC,BS,PS,IS viewModel
    class SPD,SR,LS data
    class IC,CC,RC components
    class WS,CS,LS external
```

## Data Flow Diagram

```mermaid
graph TD
    subgraph "User Input"
        UI1[Daily Consumption<br/>kWh]
        UI2[Peak Usage<br/>kWh]
        UI3[Off-Peak Usage<br/>kWh]
        UI4[Panel Wattage<br/>W]
        UI5[Battery DoD<br>%]
        UI6[Sun Hours<br/>hours]
        UI7[Electricity Rate<br>$/kWh]
    end
    
    subgraph "Solar Planning ViewModel"
        SV1[Update Parameters]
        SV2[Validate Inputs]
        SV3[Trigger Calculations]
        SV4[Update State]
        SV5[Notify UI Changes]
    end
    
    subgraph "Calculation Engine"
        CE1[Battery Sizing<br/>Wh capacity]
        CE2[Panel Count<br/>Number of panels]
        CE3[Inverter Size<br/>W requirements]
        CE4[Grid Dependency<br>% independence]
        CE5[Cost Analysis<br/>ROI calculations]
        CE6[Production Estimates<br>kWh/day]
    end
    
    subgraph "Results Display"
        RD1[System Overview<br/>Cards]
        RD2[Performance Metrics<br/>Charts]
        RD3[Economic Analysis<br/>Savings]
        RD4[Scenario Comparison<br/>Side-by-side]
    end
    
    subgraph "Data Persistence"
        DP1[Save Scenarios<br/>Local storage]
        DP2[Export Results<br/>PDF/CSV]
        DP3[User Preferences<br/>Settings]
    end
    
    %% Flow connections
    UI1 --> SV1
    UI2 --> SV1
    UI3 --> SV1
    UI4 --> SV1
    UI5 --> SV1
    UI6 --> SV1
    UI7 --> SV1
    
    SV1 --> SV2
    SV2 --> SV3
    SV3 --> CE1
    SV3 --> CE2
    SV3 --> CE3
    SV3 --> CE4
    SV3 --> CE5
    SV3 --> CE6
    
    CE1 --> SV4
    CE2 --> SV4
    CE3 --> SV4
    CE4 --> SV4
    CE5 --> SV4
    CE6 --> SV4
    
    SV4 --> SV5
    SV5 --> RD1
    SV5 --> RD2
    SV5 --> RD3
    SV5 --> RD4
    
    RD1 --> DP1
    RD2 --> DP2
    RD3 --> DP2
    RD4 --> DP1
    
    %% Style
    classDef input fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef viewModel fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef calculation fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef display fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef persistence fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    
    class UI1,UI2,UI3,UI4,UI5,UI6,UI7 input
    class SV1,SV2,SV3,SV4,SV5 viewModel
    class CE1,CE2,CE3,CE4,CE5,CE6 calculation
    class RD1,RD2,RD3,RD4 display
    class DP1,DP2,DP3 persistence
```

## State Management Flow

```mermaid
sequenceDiagram
    participant U as User
    participant UI as SolarScreen
    participant VM as SolarPlanningViewModel
    participant CL as CalculationEngine
    participant DB as Database
    
    U->>UI: Enters consumption data
    UI->>VM: updateParameter(DAILY_CONSUMPTION, value)
    VM->>VM: validateInput(value)
    VM->>CL: calculateSolarSystem()
    CL->>CL: batterySizing()
    CL->>CL: panelSizing()
    CL->>CL: inverterSizing()
    CL->>CL: gridAnalysis()
    CL->>CL: costAnalysis()
    CL-->>VM: returns SolarResults
    VM->>VM: updateState(results)
    VM-->>UI: state update
    UI->>UI: re-render with new results
    
    U->>UI: Changes panel wattage
    UI->>VM: updateParameter(PANEL_WATTAGE, value)
    VM->>VM: validateInput(value)
    VM->>CL: calculateSolarSystem()
    CL->>CL: panelSizing()
    CL->>CL: inverterSizing()
    CL->>CL: gridAnalysis()
    CL->>CL: costAnalysis()
    CL-->>VM: returns SolarResults
    VM->>VM: updateState(results)
    VM-->>UI: state update
    UI->>UI: re-render with new results
    
    U->>UI: Saves scenario
    UI->>VM: saveScenario(name)
    VM->>DB: insertScenario(scenario)
    DB-->>VM: success
    VM-->>UI: scenario saved
```

## Component Interaction Diagram

```mermaid
graph LR
    subgraph "Main Screen"
        MS[SolarScreen]
    end
    
    subgraph "Input Section"
        IF[SolarInputForm]
        MI[MonthlySunHoursConfig]
        SC[ScenarioComparison]
    end
    
    subgraph "Results Section"
        RD[SolarResultsDisplay]
        PC[SolarProductionChart]
        EC[EconomicAnalysisChart]
    end
    
    subgraph "Shared Components"
        IC[InputComponents]
        CC[ChartComponents]
        RC[ResultComponents]
    end
    
    subgraph "Business Logic"
        VM[SolarPlanningViewModel]
        CALC[SolarCalculations]
        BATT[BatterySizing]
        PANEL[PanelSizing]
        INV[InverterSizing]
    end
    
    %% Connections
    MS --> IF
    MS --> MI
    MS --> SC
    MS --> RD
    MS --> PC
    MS --> EC
    
    IF --> IC
    MI --> IC
    SC --> IC
    
    RD --> RC
    PC --> CC
    EC --> CC
    
    IF --> VM
    MI --> VM
    SC --> VM
    RD --> VM
    PC --> VM
    EC --> VM
    
    VM --> CALC
    CALC --> BATT
    CALC --> PANEL
    CALC --> INV
    
    %% Style
    classDef main fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef input fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef results fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef shared fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef logic fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    
    class MS main
    class IF,MI,SC input
    class RD,PC,EC results
    class IC,CC,RC shared
    class VM,CALC,BATT,PANEL,INV logic
```

## Error Handling Flow

```mermaid
graph TD
    subgraph "User Input"
        UI[User enters data]
    end
    
    subgraph "Validation"
        V1[Range validation]
        V2[Type validation]
        V3[Business logic validation]
    end
    
    subgraph "Error States"
        E1[Invalid range]
        E2[Invalid type]
        E3[Business rule violation]
        E4[Calculation error]
    end
    
    subgraph "Error Display"
        D1[Input field error]
        D2[Toast message]
        D3[Error dialog]
        D4[Fallback calculation]
    end
    
    subgraph "Recovery"
        R1[Auto-correction]
        R2[User guidance]
        R3[Default values]
    end
    
    %% Flow
    UI --> V1
    UI --> V2
    UI --> V3
    
    V1 --> E1
    V2 --> E2
    V3 --> E3
    
    E1 --> D1
    E2 --> D2
    E3 --> D3
    
    D1 --> R1
    D2 --> R2
    D3 --> R3
    
    E4 --> D4
    D4 --> R3
    
    %% Style
    classDef user fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef validation fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef error fill:#ffebee,stroke:#c62828,stroke-width:2px
    classDef display fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef recovery fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    
    class UI user
    class V1,V2,V3 validation
    class E1,E2,E3,E4 error
    class D1,D2,D3,D4 display
    class R1,R2,R3 recovery
```

## Performance Optimization Flow

```mermaid
graph TD
    subgraph "Performance Concerns"
        PC1[Expensive calculations]
        PC2[Large datasets]
        PC3[Frequent re-renders]
        PC4[Memory usage]
    end
    
    subgraph "Optimization Strategies"
        OS1[Calculation caching]
        OS2[Lazy loading]
        OS3[Memoization]
        OS4[State hoisting]
    end
    
    subgraph "Implementation"
        I1[Debounce input changes]
        I2[Background calculations]
        I3[Virtual scrolling]
        I4[Memory pooling]
    end
    
    subgraph "Monitoring"
        M1[Performance metrics]
        M2[Memory tracking]
        M3[Render time]
        M4[Calculation time]
    end
    
    %% Flow
    PC1 --> OS1
    PC2 --> OS2
    PC3 --> OS3
    PC4 --> OS4
    
    OS1 --> I1
    OS2 --> I2
    OS3 --> I3
    OS4 --> I4
    
    I1 --> M1
    I2 --> M2
    I3 --> M3
    I4 --> M4
    
    %% Style
    classDef concerns fill:#ffebee,stroke:#c62828,stroke-width:2px
    classDef strategies fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef implementation fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef monitoring fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    
    class PC1,PC2,PC3,PC4 concerns
    class OS1,OS2,OS3,OS4 strategies
    class I1,I2,I3,I4 implementation
    class M1,M2,M3,M4 monitoring
```

These diagrams provide a comprehensive view of the solar planning screen architecture, data flow, component interactions, error handling, and performance optimization strategies. They serve as a blueprint for implementation and help ensure all aspects of the system are properly designed and integrated.
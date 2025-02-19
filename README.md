# MaasNav 🚍🗺️

**A Java-based public transport routing engine for Maastricht, integrating real-time GTFS data to compute multi-modal routes and urban accessibility metrics.**  

---

## Overview

MaasNav is a Java application designed for **route optimization in Maastricht's public transport system**. It integrates **Dijkstra’s algorithm** to compute the shortest paths for **walking, cycling, and public transport** while considering real-time GTFS data. The system includes a **Java Swing GUI** for route visualization and an **SQLite database** to efficiently manage transit and demographic data.  

Additionally, it incorporates an **accessibility scoring system** that evaluates socio-economic opportunities within different neighborhoods by analyzing nearby amenities and transport connectivity.

---


## Features

✅ **Multi-Modal Routing**: Computes shortest paths for walking, cycling, and bus routes.  
✅ **GTFS Data Integration**: Processes real-time schedules and stop locations.  
✅ **Java Swing GUI**: Visualizes routes with interactive maps.  
✅ **Accessibility Scoring**: Evaluates neighborhoods based on nearby amenities.  
✅ **Optimized Performance**: Uses heuristics for fast query resolution.  
✅ **Database Management**: Efficient storage using an **SQLite relational database**.  

---

## Research Questions

This project is designed to address key urban mobility and accessibility challenges:

- **Is it possible to compute an optimal multi-modal route using Java and GTFS data?**  
- **How does accessibility vary across different regions of Maastricht?**  
- **What impact does urban infrastructure have on public transport efficiency?**  
- **How does our routing engine compare in efficiency against existing solutions?**  

---

## Running the Application

To start the application, run the **`MapViewer.java`** class, located in:  

📂 `src/java/com/group8/phase1/views/`  

The Java Swing GUI will launch, allowing users to enter start and destination points.

---

## Testing

✅ **Code Coverage**:  
- **89% coverage** achieved across all components.  
- Automated unit tests were executed using **JUnit & JaCoCo**.  
- Detailed coverage reports are available in the `TestResults` directory.  

✅ **Performance Benchmarks**:  
- **15% improvement** over OpenTripPlanner in travel time estimates.  
- **Efficiency testing** based on predefined multi-modal trip scenarios.  

---

## Contributors

👤 **Kaan Basaran**  
👤 **Rares Boghean**  
👤 **Nishan Mistry**  
👤 **Mihnea Şerbănescu**  
👤 **Gvidas Žilinskas**  

---


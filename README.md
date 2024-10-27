# Operating_Systems_**CPU-Scheduling**
---
Here’s a simplified breakdown of each point for your CPU scheduling project:

1. **Java-based GUI for CPU Scheduling**
    
    - [ ] Learn about Java GUI frameworks like Swing or JavaFX.
    - [ ] Design the layout to include inputs for process details, algorithm selection, and a display area for scheduling results.
    - [ ] Implement buttons for submitting data and running the simulation.
2. **Consider Process State (e.g., Ready, Running)**
    
    - [ ] Define possible states (like "Ready," "Running," and "Completed") as constants or enums.
    - [ ] Create a method to update the process state based on the scheduling algorithm's progress.
3. **Develop a Data Structure for Processes**
    
    - [ ] Define a `Process` class with properties like `processNumber`, `cpuTime`, `priority`, and `state`.
    - [ ] Create getters and setters for these properties to make it easy to access and modify process details.
4. **Ready Queue Implementation**
    
    - [ ] Use a Java data structure like `ArrayList` or `Queue` to represent the ready queue.
    - [ ] Implement functions to add processes to this queue and retrieve the next process based on the chosen scheduling algorithm.
5. **Assume All Processes Arrive at Time 0**
    
    - [ ] Set each process’s arrival time to 0, simplifying calculations.
    - [ ] Focus on scheduling without considering varying arrival times.
6. **Implement CPU Scheduling Algorithms**
    
    - [ ] For each algorithm, write a function that selects processes from the queue according to the specific rules:
        - [ ] **FCFS**: Process the queue in the order processes arrive.
        - [ ] **SJF**: Select the process with the shortest CPU time remaining.
        - [ ] **Round Robin (RR)**: Cycle through processes in the queue, using a fixed time slice or quantum.
        - [ ] **Priority Scheduling (non-preemptive)**: Choose the process with the highest priority (lowest priority value) and only switch when it completes.
7. **Inputs for Processes and RR Quantum**
    
    - [ ] Design the GUI to accept input fields for each process’s `processNumber`, `cpuTime`, and `priority`.
    - [ ] For RR, add a separate input for the time quantum.
    - [ ] Validate inputs and store them in the `Process` class and ready queue.

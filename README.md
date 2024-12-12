# CPU Scheduling Simulator Project To-Do List

### 1. Set Up Process States
- [ ] Define process states (READY, RUNNING, COMPLETED).
- [ ] Create a `Process` class with a `state` attribute, initially set to READY.
- [ ] Add methods to update the state based on scheduling rules.

### 2. Create the Process Data Structure
- [ x ] In the `Process` class, add properties like `processNumber`, `cpuTime`, `priority`, and `state`.
- [ ] Add methods to get and set these values.
- [ ] Create a constructor to initialize each process with its required values.

### 3. Set Up the Ready Queue
- [ ] Use a Java data structure like `ArrayList` or `Queue` for managing processes in the ready queue.
- [ ] Implement methods to add and remove processes from the queue.
- [ ] Add functionality to clear the queue for new simulations.

### 4. Set All Process Arrival Times to Zero
- [ ] Set the arrival time of each process to zero to simplify the simulation.
- [ ] Focus only on the order of execution, not different arrival times.

### 5. Build a Simple Java GUI
- [ ] Set up the GUI framework (Java Swing or JavaFX).
- [ ] Add input fields for process details (number, CPU time, priority, and RR time quantum).
- [ ] Add dropdowns or buttons to select a scheduling algorithm.
- [ ] Design an output area to display results and process state changes.

### 6. Implement Each CPU Scheduling Algorithm
- [ ] **FCFS (First-Come-First-Served)**: Write a function to process tasks in the order they arrive in the queue.
- [ ] **SJF (Shortest Job First)**: Write a function to select the process with the smallest CPU time.
- [ ] **RR (Round Robin)**: Write a function to give each process a set time (quantum) and cycle through the queue.
- [ ] **Priority Scheduling (non-preemptive)**: Write a function to select the highest-priority process (smallest number) and complete it before moving to the next.

### 7. Handle User Input for Processes and Quantum Time
- [ ] Add input fields in the GUI for process number, required CPU time, priority, and RR time quantum.
- [ ] Validate inputs to ensure they are correct (e.g., no negative numbers).
- [ ] Store input data in `Process` objects and add them to the ready queue.

---

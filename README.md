# Group project for Computer Science (Engineering) 214
A space shooter game with graphics implemented via java.awt and javax.swing libraries.

## Getting Your Way Around Code

### Main class from which game can be run is `./src/InvadersFrame.java`

#### Optional arguments:

`-f` or `-fullscreen` : Run in **Full-Screen Exclusive Mode** if available. (Recommended for better game performace.)

`-d` or `-debug` : Run with **Visual Debugging Mode**. Helps understand how collission shapes and bounding boxes work

For example: The command `> java InvadersFrame -f -d` runs the game in both Full-Screen Exclusive and Visual Debugging mode.

### Game resources are under `./resources/`

NOTE: It is recommended to run the project (at its current state) via VSCode debugger. There are also some VSCode tasks for running different argument configs. Otherwise you **might need to move the resources folder** to inside the `./src/` directory before running. I will tend to a fix for this when I get time.

## Code Features and Key Take-Aways from this Project

- JFrame, JPanel, JComponents
- **KeyListeners** and switching focus
- Switching active KeyListeners while **customizing game controls**
- **KeyBindings** regardless of whehter or not component has focus 

- **Game loop**. Calculating sleep times to target a given frame rate.
- Java's **Full-Screen Exclusive Mode**
- **Buffer Strategy** 

- Effective Collision Detection Strategies: 
  - each critter has a collision shape, either a circle or a rectangle. geom package contains classes for these as well as methods for checking intersections
  - groups of critters are wrapped in a AABB (axis-aligned bounding box). Cross checking each element only happens when the incoming object is at least intersecting the bounding box.
  - ray casting to show line of sight until first obstacle for shooter

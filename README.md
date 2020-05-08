# Computer Science E214 (2020) Project: Cosmic Conquistadors

<img src="screenshots/readme-screenshot.png" width="100%" >

## Group Members

|      | Name           | Student Number |
| ---- | -------------- | -------------- |
| #1   | Emi Dreckmeyr  |                |
| #2   | Michael Knight |                |
| #3   | Nicol Visser   | 16986431       |

## Execution Details

The main class is `MainGame.java` and is located under `./src/` directory.

```shell
> cd src
```

### Compile

```shell
> javac MainGame.java
```

### Compiling:

### Run:

#### Windowed Mode:

```shell
> java MainGame
```

#### Fullscreen Exclusive Mode (recommended):

Add the argument `-f` or `-fullscreen`.

```shell
> java MainGame -f
```

#### Visual Debugging Mode:

Add the argument `-d` or `-debug`.

```shell
> java MainGame -f -d
```



## Interface Inheritance

### Interfaces in the `geom` package:

The `geom` package contains objects that can be associated with 2D geometry.

It has two interfaces, the `Shape` interface and an extension of it, the `BoundingShape` interface.

`LineSegment` and `Ray` implement the `Shape` interface, whereas `Rectangle` and `Circle` implement the `BoundingShape` interface.

#### `Shape` Interface:

`Rectangle`, `Circle`, `LineSegment` and `Ray`, by (directly or indirectly) implementing the `Shape` interface, have methods for:

- drawing the shape on a `Graphics2D` object
  - `public boolean intersects(Shape shape);`
- check whether or not the shape intersects another shape.
  - `public void draw(Graphics2D g);`


#### `BoundingShape` Interface: 

`Rectangle` and `Circle`, by implementing the`BoundingShape` interface, also have methods for

- checking whether or not the bounding shape contains a point
  - `public boolean contains(double x, double y);`
  - `public boolean contains(Vector2D point);`
- checking whether or not the bounding shape completely contains another shape
  - `public boolean contains(Shape shape);`
- returning a random point inside the bounding shape
  - `public Vector2D getRandomPositionInside();`

##### Example:

### Interfaces in the game (default package)

#### `Collidable` Interface

`Collidable` aids with collision detection and handling.

Classes that implement `Collidable` have methods for:

- returning the `BoundaryShape` used for collision
  - `public Shape getCollisionShape();`
- checking whether or not the object is colliding with another ` Collidable` object
  - `public boolean isCollidingWith(Collidable otherCollidable);`
- handling the collision with another ` Collidable` object, assuming they do collide
  - `public void handleCollisionWith(Collidable otherCollidable);`

In addition the following static methods are available to be used for collission checking and handling

In our game, the game objects are often stored in `ArrayLists` of the same type. Instead of creating an N body simulation where all `Collidables` are checked against eachother, we rather only check certain groups of `Collidables` with other groups of `Collidables` or an individual `Collidable`. For this approach, two static methods are available to check and handle collisions.

- `public static void checkAndHandleCollisions(ArrayList<? extends Collidable> group1, ArrayList<? extends Collidable> group2) {`

- `public static void checkAndHandleCollisions(Collidable collidable1, ArrayList<? extends Collidable> group2)`

#### `Disposable` Interface

We mentioned that our game objects are often stored in `ArrayLists`. At some point after a `Missile` has exploded or an `Enemy` has died, we want to remove that object from the list so that it does not have to be rendered and can be garbage collected by Java. Therefore we made the `Disposable` interface.

A class that implements this interface has a method `public boolean mayBeDisposed()` to check whether or not the item is ready to be disposed.

For convenience the `Disposable` interface contains a method that will iterate through an `ArrayList` of `Disposables`, checks whether any item is ready to be disposed and then removes that item from the list.

#### `Shakeable` (an experimental) Interface

NOTE: This interface is experimental and is probably not be the best way to implement the functionality. The reason for choosing a functional interface is purely educational.

Shakeable is a functional interface that can be used together with lambda expressions to 'pass a method' to another class via its constructor. The idea was to pass a method that 'shakes' the screen from the InvaderGameState class to the Shooter class such that when a missile hits the shooter, we can call the shake method of the InvadersGameState class from within the Shooter class.

Idea gained from https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
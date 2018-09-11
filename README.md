# InteractiveGame-Shooting-Fish
The game is developed using jMonkey game engine and Blender.

## Introduction
An interactive game with a main character: Fish Blub swimming in a virtual scene like sea world. Users can control the fish and use cannonballs to shoot the fish or other targets. 

## Main Functions
- Keyframe Animations of Fish model:  
  - Swimming: caudal fin and the body wiggle side-to-side.  
  - Stationary: side fins and top fin wiggle side-to-side.  
  - Turn: turn 360 degrees  
- User Interactions:
   - Space bar: Create new sphere on the top plane arbitrarily with arbitrary velocity.   
   - Arrow keys: Apply a force parallel to the ground plane.  
   - Page up & down keys: Apply a force in up and down direction.  
   - ‘s’ key: Trigger a continuous swimming animation. (Stop if the key is pressed again)  
   - ‘i’ key: Trigger a continuous idle stationary animation. (Stop if the key is pressed again)  
   - ‘t’ key: Trigger a one time 360 degree bending turn.  
   - Mouse picking: switch control between blub and various balls.  
   - Apply a force to the blub or a selected ball using keys.  
- Sounds:    
   - For collisions between blub, balls, floor, etc.  
   - For user interactions: clicking and shooting balls  
   - Background music  

## Game Demo
### Keyframe Animations  
![](https://github.com/fanyuR/InteractiveGame-Shooting-Fish/blob/master/demo/Fish%20Animation.gif)

### User Interactions  
Space bar/Arrow keys/Page up & down keys/Mouse picking  
![](https://github.com/fanyuR/InteractiveGame-Shooting-Fish/blob/master/demo/p1_click.png)
![](https://github.com/fanyuR/InteractiveGame-Shooting-Fish/blob/master/demo/p2_click.png)  
![](https://github.com/fanyuR/InteractiveGame-Shooting-Fish/blob/master/demo/User%20Interaction.gif)

### keyboard Control of Fish Animations  
![](https://github.com/fanyuR/InteractiveGame-Shooting-Fish/blob/master/demo/Aimation%20and%20Interaction.gif)

## Main Development Steps
- Created skeleton and skin for a fish model in Blender
- Defined some keyframe animations of the fish model in Blender
- Loaded the animations in jMonkeyEngine with keyboard user interaction
- Created a virtual fish tank scene and added objects to corresponding geometry node
- Set physics-based effects for objects in the virtual game environment 
- Added more user interactions which allow user to apply forces to fish and other objects in the tank by using keyboard and mouse 
- Added shadows, textures and lighting effects and created sound effects for user interactions and collisions








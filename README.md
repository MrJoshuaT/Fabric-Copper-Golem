# Copper Golem for Fabric 🤖
Sadly at __Minecraft Live 2021__ the Copper Golem did not win the vote to get added into 1.18.

But this little golem is so cute I did not want to see it in Minecraft!

> Functionality is experimental and is based on what Mojang announced at Minecraft Live 2021.

### Requires at least Fabric API 0.41.0

<img src="./images/main_screenshot.png"/>

## Features

### Spawning
* The spawning combo is a bottom __Copper Block__, middle __Carved Pumpkin__ Block and top __Lightning Rod__.

### Goals, in priority order
* Wandering around, this is taken straight from the Iron Golem code
* Looking around
* Spin head
* Find buttons to press
* Look at player
* Look at Iron Golems, as they're their big brother
* Press button

### Oxidation
* Random ticks will cause oxidation to increment, of which are 4 levels (0-3)
* Interacting with the mob with Honeycomb to Wax it
* Interacting with the mob with an any Axe will unwax it, if waxed, otherwise if not at first oxidation level will deoxidise it by 1 level
* Lightning strikes will set oxidation level to 0 (min level)

## Current to do list
## Block / Entity
* Add copper buttons, and their oxidise variants

### Misc
* Server support
* Hitbox is a not great, should be smaller on X,Z and bigger on Y?
* ✅ Texture on stage 1 and 2 need adjusting
* ✅ Spawn egg
* ✅ Remove feature renderer and add texture selection to renderer
* ✅ Force enable the texture pack
* ✅ Interacting with a Copper Ingot should heal the golem

### Animation
* Arm/body when pressing buttons
* ✅ Spinning head

### Goals
* ✅ Separate goal and target selectors
* ✅ Add spinning head goal for random head spins

### Mod / Cloth menu
* Option to select all/specific buttons to press

... more to be added

### Special thanks to Carloski for the Copper Golem textures! ❤️
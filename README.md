<img alt="" src="logo.svg" height="80px" />

# Mundus [![gradle build](https://github.com/inver/Mundus/actions/workflows/gradle.yml/badge.svg?branch=develop)](https://github.com/inver/Mundus/actions/workflows/gradle.yml) [![Coverage Status](https://codecov.io/github/inver/Mundus/graph/badge.svg?token=NA0KWTETR5)](https://codecov.io/github/inver/Mundus) [![CodeQL](https://github.com/inver/Mundus/actions/workflows/codeql.yml/badge.svg?branch=develop&event=push)](https://github.com/inver/Mundus/actions/workflows/codeql.yml)

Mundus is a platform independent 3D world editor, built with Java, Kotlin and LibGDX + VisUI.
The plan is, that the world you create with Mundus can be exported
into the gltf format. The exported data can then be imported into any Game Engine,
if someone writes a runtime/parser for that engine.

![Screenshot](docs/images/overview.png)

This project is at a very early stage in development, so expect large
changes in the future.

## Current features
Please note, that a lot of the UI contains dummy controls for functionality that still needs to be
implemented.
Below are some of the more interesting features, that already work

- Creation of multiple terrains
- Height map loader for terrains
- Procedural terrain generation
- Texture splatting for the terrain texture (max 5 textures per terrain)
- A complete terrain editing system with texture & height brushes
- 4 different brushes (brush form can be an arbitrary image, like in Blender or Gimp)
- 3 brush modes for each brush: Raise/Lower, Flatten & texture paint
- A skybox (not exportable yet)
- Loading all types of files, which supports by Assimp.
- A visual translation & rotation tool (scaling coming soon)
- Multiple scenes in one project
- A component based scene graph (not fully implemented yet)
- Basic export of the project into a json format + assets
- Undo/Redo system for most operations
- Highly accurate game object picking system, based on id color coding & offscreen framebuffer rendering.
  Basic concept: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
  
## Backlog
- IBL sky box
- Cloud shader
- Shader editor

## Things to consider
- Mundus is constantly changing. Especially the internal representation of save files. At this stage of the project i don't care 
very much for backward compatibility, so don't fall to much in love with your creations if you want to stay up to date ;)
- Depending on your keyboard layout some key shortcuts might be twisted (especially CTRL+Z and CTRL+Y for QWERTZ and QWERTY layouts) 
because of the default GLFW keycode mapping. You can change the layout mapping in the settings dialog under Window -> Settings.

## Runtime
Currently i'm in the process of implementing the libGDX runtime, which is included in this repository.
Runtimes for other engines/frameworks are not planed in the near future.

## Contributing
Contributions are greatly appreciated. To make the process as easy as possible please follow the [Contribution Guide](https://github.com/mbrlabs/Mundus/wiki/Contributing).
To get an overview over the project you might also want to check out the [Project overview & architecture](https://github.com/mbrlabs/Mundus/wiki/Project-overview-%26-architecture) article.

## Working from source
See this [wiki article](https://github.com/mbrlabs/Mundus/wiki/Working-from-source).

## Credits
Logo design: [Franziska Böhm / noxmoon.de](http://noxmoon.de) ([CC-BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)) 

# Particle Core
<p align="left">
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-brightgreen.svg"></a>
</p>

# What Does It Do?
### Culling
The biggest performance improvement overall, Particle Core doesn't render particles you can't see. Makes sense! Only about a 12th of the lightbox is visible at standard FOVs, why render the 11/12 of particles you can't even see?

### Rendering Optimizations
Particles that are rendered are optimized. Vertex transformations and lightmap polling are optimized. Vertex optimizations defer to Sodium.

### By-Type Reduction or Disabling
Three methods are provided to either completely disable or reduce spawning of specific particle types. Any particle you can add via the /particle command is eligible.
1) A particle_type tag. Add particle types to `particle_core:excluded_particles` to completely disable those particle types. **Server Owners: this will disable those particles for every client that joins the server, use with caution!**
2) Config-based by-type reduction map. In the particle core config (see below), a user can add mappings of `particle type identifier` to `chance double`.
3) Turn of Potion Particles entirely. If you find potion particles on your screen annoying, head over to the config and turn them off!

Unlike invis-particle resource packs, this actually prevents the particles from spawning at all, improving performance.

### Tweaking Vanilla settings
Two config options are provided that can fine-tune the standard vanilla options of `ALL`, `DECREASED`, and `MINIMAL`. One is effectively a dial between ALL and DECREASED, the second a dial between DECREASED and MINIMAL. If you simply want to tweak particle spawning a bit, for example if your computer runs fine with DECREASED but you personally don't like how many particles are still on screen, you can dial DECREASED back without going all the way to MINIMAL.

[![Bisect Hosting Banner](https://www.bisecthosting.com/partners/custom-banners/85a7141b-76c9-4f0c-bd04-21f4b0d56c4a.webp "Bisect Hosting Banner")](https://bisecthosting.com/fzzyhmstrs)

# Config
Particle Core provides a config covering every feature added. locate the config in the standard .mincraft `config` folder. Config name is `particle_core_config_v[x].json`, where [x] is the current version number.

The config has a comprehensive "comment" section at the top of the file. Please read it for guidance on config usage.

### turnOffPotionParticles
To some, potion particles are extremely annoying. Use this setting to turn them off.

### reduceParticlesAllChance
A dial to tweak particle spawning between the `ALL` and `DECREASED` Minecraft setting.

### reduceParticlesDecreasedChance
A dial to tweak particle spawning between the `DECREASED` and `MINIMAL` Minecraft setting.

### disableParticles
Completely disable all client-side particle spawning.

### reduceParticlesByType
Map to reduce specific particle spawning.
* key: identifier of the particle type, ex. `minecraft:smoke`.
* value: double between 0.0 and 1.0. 0.0 will completely disable the particle, 1.0 is normal spawning. Anything in between will be that fractional chance a particle succeeds at spawning.

### disableOptimizations
If any feature in Particle Core is causing a conflict or it is simply undesired, every feature can be individually disabled by adding it's string key to this list. See comment for instructions.

# Particle Core 0.3.0
This version brings significant performance boost over 0.2.x, with several new optimization features.
* Benchmarked with max particles >16,000, all particles visible on screen, and a mix of translucent and opaque particles
* Version 0.2.6 hit approximately 230fps
* Version 0.3.0 hit approximately 450fps
* Both without Sodium or other optimization mods. With sodium fps jumps above 500

### Additions
* The block position of particles is now cached every tick, as well as their state and if they have no collider. This reduces the load for both lightmap caching and collision detection
* Created custom particle vertexing method, avoiding the costly "builder style" composition of vertexes
* Added "particle render distance", which changes how far away particles will render relative to the current block render distance
* New setting to control the maximum particle count. Increase this if you want to stress test your system!
* Added several optimizations to particle movement calculations
  * In particular, particles are first tested to see if they are in "open air". If so, no collision checks are run at all.
  * If particles do have potential collisions, only the blocks in relevant directions are checked, instead of every block in a (usually) 3x3x3 cube.
* New async particle ticking mode. This is enabled by default.
  * If instability is encountered, disable this mode
  * Particle core will try to catch issues and disable this mode automatically
  * When particle counts are low, ticking will happen synchronously regardless to reduce overhead and increase stability

### Changes
* Lightmap caching is now attached directly to the particles themselves instead of in a hashmap. The lightmap is also polled on tick now, not per frame.
* Config is now fully translated and reorganized somewhat
* Potion Particle disabling has been changed to be "multi-select". You will select each "part" that you want disabled (self, other, mobs), or "All" or "None"
* The "particle_core_disabled_optimizations" config is now updated to "particle_core_disabled_optimizations_v2", with more possible optimization switches described.
  * The previous config choices will be automatically carried over

### Fixes
* Embeddium compat is now handled properly in the same way that Sodium compat is. Particle rotation optimizations will be ignored by PC if either is loaded
  * Also Rubidium for whoever still uses that for whatever reason
* Added particle culling blacklist and new culling options to fix issues with very large or otherwise "unusual" particles
  * Aggressive Culling (previous behavior) - culls from the particle center
  * Bounding box culling - culls using the edges of the particle bounding box
  * No Culling - disable culling entirely
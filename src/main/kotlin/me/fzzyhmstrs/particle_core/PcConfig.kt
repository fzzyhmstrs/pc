package me.fzzyhmstrs.particle_core

import me.fzzyhmstrs.fzzy_config.config_util.SyncedConfigHelperV1
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

@Environment(EnvType.CLIENT)
object PcConfig {

    internal val validOptimizationStrings = listOf(
        "ROTATION",
        "CULLING",
        "TYPE",
        "DECREASE",
        "LIGHTMAP",
        "POTION"
    )

    internal val random = Random.createLocal()

    internal val byTypeParticleReduction: Map<Identifier, Double> by lazy {
        impl.reduceParticlesByType.mapKeys { Identifier(it.key) }
    }

    var impl = SyncedConfigHelperV1.readOrCreateAndValidate("particle_core_config_v0.json", base = "", configClass = {PcConfigImpl()})

    enum class PotionDisableType(val index: Int){
        NONE(0),
        SELF(1),
        PLAYER(2),
        ALL(3)
    }

    class Comment{
        var PotionParticles_Allowed_Values = mapOf(
            "NONE" to "(no particles removed)",
            "SELF" to "(only your own particles removed)",
            "PLAYER" to "(all player particles removed)",
            "ALL" to "(all mobs potion particles removed)")
        var ParticlesAllChance = "Turns down particles when in ALL mode. Value between 0.0 and 1.0. Ex. 0.1 will make 10% of particles DECREASED. Basically a dial between ALL and DECREASED."
        var ParticlesDecreasedChance = "Turns down particles when in DECREASED mode. Value between 0.0 and 1.0. Ex. 0.1 will make 10% of particles MINIMAL. Basically a dial between DECREASED and MINIMAL."
        var ParticlesByType = "Map of Particle Type to spawn chance. Ex. {\"minecraft:smoke\": 0.3, \"minecraft:capfire_cozy_smoke\": 0.3} (without the backslashes) will cause Smoke and Campfire particles to spawn at 30% of their normal rate. separate entries with a comma."
        var Disable_Optimizations_Options = mapOf(
            "ROTATION" to "[Impact: Medium] Disables mixins related to vertex rotation caching (ParticleManagerRotationMixin, BillboardParticleMixin)",
            "CULLING" to "[Impact: High] Disables mixins related to particle culling (FrustumAccessor, ParticleAccessor, ParticleManagerFrustumMixin, WorldRendererFrustumMixin)",
            "TYPE" to "[Impact: Low to Medium] Disables mixins related to type-specific particle disabling and reduction (WorldRendererTypeMixin)",
            "DECREASE" to "[Impact: Low] Disables mixins related particle settings reduction (ALL, DECREASED, MINIMAL) (WorldRendererDecreaseMixin)",
            "LIGHTMAP" to "[Impact: Medium] Disables mixins related to light map caching (ParticleManagerCachedLightMixin, ParticleMixin)",
            "POTION" to "[Impact: Low] Disables mixins related to potion particle disabling (LivingEntityMixin)")
    }

}